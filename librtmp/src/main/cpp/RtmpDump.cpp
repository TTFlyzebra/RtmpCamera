//
// Created by Administrator on 2023/6/23.
//

#include <cstdio>
#include <unistd.h>
#include "RtmpDump.h"
#include "buffer/BufferManager.h"
#include "buffer/LoopBuf.h"
#include "utils/FlyLog.h"
#include "librtmp/rtmp.h"

RtmpDump::RtmpDump(JavaVM *jvm, JNIEnv *env, jobject thiz)
        : send_t(nullptr), rtmp(nullptr), is_connect(false),
          _vps(nullptr), _sps(nullptr), _pps(nullptr) {
    FLOGD("%s()", __func__);
    callBack = new CallBack(jvm, env, thiz);
}

RtmpDump::~RtmpDump() {
    delete callBack;
    if (_vps) {
        free(_vps);
        _vps = nullptr;
    }
    if (_sps) {
        free(_sps);
        _sps = nullptr;
    }
    if (_pps) {
        free(_pps);
        _pps = nullptr;
    }
    FLOGD("%s()", __func__);
}

void RtmpDump::init(const char *url) {
    int len = strlen(url);
    int size = sizeof(rtmp_url);
    memset(rtmp_url, 0, sizeof(rtmp_url));
    memcpy(rtmp_url, url, len);
    FLOGD("RtmpDump set rtmp url to %s[%d]----%s[%d]", url, len, rtmp_url, size);
    if (send_t != nullptr) {
        is_stop = true;
        send_t->join();
        send_t = nullptr;
    }
    is_stop = false;
    send_t = new std::thread(&RtmpDump::sendThread, this);
}

void RtmpDump::release() {
    is_stop = true;
    {
        std::lock_guard<std::mutex> lock(mlock_send);
        while (!sendPackets.empty()) {
            auto packet = sendPackets.front();
            sendPackets.pop();
            RTMPPacket_Free(packet);
            free(packet);
        }
        mcond_send.notify_all();
    }
    if (send_t != nullptr) {
        send_t->join();
        send_t = nullptr;
    }
}

void RtmpDump::rtmpConnect() {
    rtmp = RTMP_Alloc();
    if (rtmp == nullptr) {
        FLOGE("RTMP_Alloc failed");
        return;
    }
    RTMP_Init(rtmp);
    rtmp->Link.timeout = 10;
    rtmp->Link.lFlags |= RTMP_LF_LIVE;

    char url[1024];
    sprintf(url, "%s", rtmp_url);
    FLOGE("RTMP_SetupURL url=%s", url);
    int ret = RTMP_SetupURL(rtmp, url);
    if (ret == FALSE) {
        RTMP_Free(rtmp);
        FLOGE("RTMP_SetupURL %s---%s ret=%d", rtmp_url, url, ret);
        return;
    }

    RTMP_EnableWrite(rtmp);

    ret = RTMP_Connect(rtmp, nullptr);
    if (!ret) {
        RTMP_Free(rtmp);
        FLOGE("RTMP_Connect ret=%d", ret);
        return;
    }

    ret = RTMP_ConnectStream(rtmp, 0);
    if (!ret) {
        ret = RTMP_ConnectStream(rtmp, 0);
        RTMP_Close(rtmp);
        RTMP_Free(rtmp);
        FLOGE("RTMP_ConnectStream ret=%s", ret);
        return;
    }
    is_connect = true;
}

void RtmpDump::rtmpDisconnect() {
    RTMP_Close(rtmp);
    RTMP_Free(rtmp);
    rtmp = nullptr;
    is_connect = false;
}

void RtmpDump::sendThread() {
    int ret;
    bool is_sendheader;

    while (!is_stop) {
        if (!is_connect) {
            rtmpConnect();
            is_sendheader = false;
        }
        if (!is_connect) {
            FLOGE("Rtmp connect faile, wait one second and try again!");
            usleep(1000000);
            continue;
        }
        {
            std::unique_lock<std::mutex> lock(mlock_send);
            while (!is_stop && sendPackets.empty()) {
                mcond_send.wait(lock);
            }
        }
        if (is_stop) break;

        if (!is_sendheader) {
            if (_vps == nullptr) {
                ret = _sendSpsPps(_sps, spsLen, _pps, ppsLen);
            } else {
                ret = _sendVpsSpsPps(_vps, vpsLen, _sps, spsLen, _pps, ppsLen);
            }
            if (ret == TRUE) is_sendheader = true;
        }

        auto packet = sendPackets.front();
        sendPackets.pop();
        ret = RTMP_SendPacket(rtmp, packet, 0);
        RTMPPacket_Free(packet);
        free(packet);
        if (ret == TRUE) {
            FLOGD("RTMP_SendPacket ok, ret = %d", ret);
            continue;
        }

        FLOGE("RTMP_SendPacket failed, ret = %d", ret);
        rtmpDisconnect();
        while (!sendPackets.empty()) {
            auto packet = sendPackets.front();
            sendPackets.pop();
            RTMPPacket_Free(packet);
            free(packet);
        }
    }
}

void RtmpDump::sendSpsPps(const char *sps, int sps_len, const char *pps, int pps_len) {
    _sps = static_cast<char *>(malloc(sps_len * sizeof(char)));
    spsLen = sps_len;
    memcpy(_sps, sps, spsLen);
    _pps = static_cast<char *>(malloc(pps_len * sizeof(char)));
    ppsLen = pps_len;
    memcpy(_pps, pps, ppsLen);
}

int RtmpDump::_sendSpsPps(const char *sps, int sps_len, const char *pps, int pps_len) {
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Reset(packet);
    int ret = RTMPPacket_Alloc(packet, 2 + 3 + 5 + 1 + 2 + sps_len + 1 + 2 + pps_len);
    if (!ret) {
        callBack->javaOnError(-1);
        return FALSE;
    }
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = 2 + 3 + 5 + 1 + 2 + sps_len + 1 + 2 + pps_len;
    packet->m_nChannel = 0x04;
    packet->m_nTimeStamp = 0;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    //packet->m_nInfoField2 = rtmp->m_stream_id;
    unsigned char *body = (unsigned char *) packet->m_body;
    int i = 0;
    body[i++] = 0x17;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    //AVCDecoderConfigurationRecord
    body[i++] = 0x01;
    body[i++] = sps[1];
    body[i++] = sps[2];
    body[i++] = sps[3];
    body[i++] = 0xff;
    //sps
    body[i++] = 0xe1;
    body[i++] = (sps_len >> 8) & 0xff;
    body[i++] = (sps_len) & 0xff;
    memcpy(body + i, sps, sps_len);
    i += sps_len;
    //pps
    body[i++] = 0x01;
    body[i++] = (pps_len >> 8) & 0xff;
    body[i++] = (pps_len) & 0xff;
    memcpy(body + i, pps, pps_len);
    ret = RTMP_SendPacket(rtmp, packet, 0);
    RTMPPacket_Free(packet);
    free(packet);
    return ret;
}

void RtmpDump::sendVpsSpsPps(const char *vps, int vps_len, const char *sps, int sps_len,
                             const char *pps, int pps_len) {
    _vps = static_cast<char *>(malloc(vps_len * sizeof(char)));
    vpsLen = vps_len;
    memcpy(_vps, vps, vpsLen);
    _sps = static_cast<char *>(malloc(sps_len * sizeof(char)));
    spsLen = sps_len;
    memcpy(_sps, sps, spsLen);
    _pps = static_cast<char *>(malloc(pps_len * sizeof(char)));
    ppsLen = pps_len;
    memcpy(_pps, pps, ppsLen);
}

int RtmpDump::_sendVpsSpsPps(const char *vps, int vps_len, const char *sps, int sps_len,
                             const char *pps, int pps_len) {
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Reset(packet);
    int ret = RTMPPacket_Alloc(packet, 43 + vps_len + sps_len + pps_len);
    if (!ret) {
        callBack->javaOnError(-1);
        return FALSE;
    }
    //packet->m_nInfoField2 = rtmp->m_stream_id;
    char *body = (char *) packet->m_body;
    int i = 0;
    body[i++] = 0x1c;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x01;
    body[i++] = sps[6];
    body[i++] = sps[7];
    body[i++] = sps[8];
    body[i++] = sps[9];
    body[i++] = sps[12];
    body[i++] = sps[13];
    body[i++] = sps[14];
    //48 bit nothing deal in rtmp
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    //bit(16) avgFrameRate
    /* bit(2) constantFrameRate; */
    /* bit(3) numTemporalLayers; */
    /* bit(1) temporalIdNested; */
    body[i++] = 0x83;
    /*unsigned int(8) numOfArrays; 03*/
    body[i++] = 0x03;
    //vps 32
    body[i++] = 0x20;
    body[i++] = 0x00;
    body[i++] = 0x01;
    body[i++] = (vps_len >> 8) & 0xff;
    body[i++] = (vps_len) & 0xff;
    memcpy(&body[i], vps, vps_len);
    i += vps_len;
    //sps
    body[i++] = 0x21;
    body[i++] = 0x00;
    body[i++] = 0x01;
    body[i++] = (sps_len >> 8) & 0xff;
    body[i++] = (sps_len) & 0xff;
    memcpy(&body[i], sps, sps_len);
    i += sps_len;
    //pps
    body[i++] = 0x22;
    body[i++] = 0x00;
    body[i++] = 0x01;
    body[i++] = (pps_len >> 8) & 0xff;
    body[i++] = (pps_len) & 0xff;
    memcpy(&body[i], pps, pps_len);
    i += pps_len;
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = i;
    packet->m_nChannel = 0x04;
    packet->m_nTimeStamp = 0;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    ret = RTMP_SendPacket(rtmp, packet, 0);
    RTMPPacket_Free(packet);
    free(packet);
    return ret;
}

void RtmpDump::sendAvc(const char *data, int size, long pts) {
    unsigned long long rpts = pts;
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Reset(packet);
    int ret = RTMPPacket_Alloc(packet, 9 + size);
    if (!ret) {
        callBack->javaOnError(-2);
        return;
    }
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = 9 + size;
    packet->m_nChannel = 0x04;
    packet->m_nTimeStamp = rpts / 1000;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_MEDIUM;
    //packet->m_nInfoField2 	= rtmp->m_stream_id;
    memcpy(packet->m_body, (data[0] & 0x1f) != 1 ? "\x17\x01" : "\x27\x01", 2);
    memcpy(packet->m_body + 2, "\x00\x00\x00", 3);
    AMF_EncodeInt32(packet->m_body + 5, packet->m_body + 9, size);
    memcpy(packet->m_body + 9, data, size);
    {
        std::lock_guard<std::mutex> lock(mlock_send);
        sendPackets.push(packet);
        mcond_send.notify_one();
    }
}

void RtmpDump::sendHevc(const char *data, int size, long pts) {
    unsigned long long rpts = pts;
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Reset(packet);
    int ret = RTMPPacket_Alloc(packet, 9 + size);
    if (!ret) {
        callBack->javaOnError(-2);
        return;
    }
    int i = 0;
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = 9 + size;
    packet->m_nChannel = 0x04;
    packet->m_nTimeStamp = rpts / 1000;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_MEDIUM;
    //packet->m_nInfoField2 = rtmp->m_stream_id;
    if (((data[0] & 0x7e) >> 1) == 19) {
        packet->m_body[i++] = 0x1c;
    } else {
        packet->m_body[i++] = 0x2c;
    }
    packet->m_body[i++] = 0x01;//AVC NALU
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    //NALU size
    packet->m_body[i++] = (size >> 24) & 0xff;
    packet->m_body[i++] = (size >> 16) & 0xff;
    packet->m_body[i++] = (size >> 8) & 0xff;
    packet->m_body[i++] = (size) & 0xff;
    memcpy(&packet->m_body[i], data, size);

    {
        std::lock_guard<std::mutex> lock(mlock_send);
        sendPackets.push(packet);
        mcond_send.notify_one();
    }
}
