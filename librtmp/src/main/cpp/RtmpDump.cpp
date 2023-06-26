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

RtmpDump::RtmpDump(JavaVM *jvm, JNIEnv *env, jobject thiz, const char *url)
        : send_t(nullptr), rtmp(nullptr), is_connect(false), _vps(nullptr), _sps(nullptr),
          _pps(nullptr), _head(nullptr) {
    FLOGD("%s()", __func__);

    memset(rtmp_url, 0, sizeof(rtmp_url));
    memcpy(rtmp_url, url, strlen(url));

    callBack = new CallBack(jvm, env, thiz);
    {
        std::lock_guard<std::mutex> lock_stop(mlock_stop);
        is_stop = false;
    }
    send_t = new std::thread(&RtmpDump::sendThread, this);
}

RtmpDump::~RtmpDump() {
    {
        std::lock_guard<std::mutex> lock_stop(mlock_stop);
        is_stop = true;
    }
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
    if (_head) {
        free(_head);
        _head = nullptr;
    }
    FLOGD("%s()", __func__);
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
    int ret = RTMP_SetupURL(rtmp, url);
    if (ret == FALSE) {
        RTMP_Free(rtmp);
        FLOGE("RTMP_SetupURL %s-%s failed. ret=%d", rtmp_url, url, ret);
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
    bool is_send_video_head;
    bool is_send_audio_head;

    while (!is_stop) {
        if (!is_connect) {
            rtmpConnect();
            is_send_video_head = false;
            is_send_audio_head = false;
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

        if (!is_send_video_head) {
            int ret1 = FALSE;
            std::lock_guard<std::mutex> lock_stop(mlock_stop);
            if (_vps != nullptr && _sps != nullptr && _pps != nullptr) {
                ret1 = _sendVpsSpsPps(_vps, vpsLen, _sps, spsLen, _pps, ppsLen);
            } else if (_sps != nullptr && _pps != nullptr) {
                ret1 = _sendSpsPps(_sps, spsLen, _pps, ppsLen);
            }
            if (ret1 == TRUE) is_send_video_head = true;
        }

        if (!is_send_audio_head && _head != nullptr) {
            int ret2 = _sendAacHead(_head, headLen);
            if (ret2 == TRUE) is_send_audio_head = true;
        }

        RTMPPacket *packet = nullptr;
        {
            std::lock_guard<std::mutex> lock(mlock_send);
            packet = sendPackets.front();
            sendPackets.pop();
        }
        ret = RTMP_SendPacket(rtmp, packet, 0);
        RTMPPacket_Free(packet);
        free(packet);

        if (ret == TRUE) {
            continue;
        }

        FLOGE("RTMP_SendPacket failed, ret = %d", ret);
        rtmpDisconnect();
        {
            std::lock_guard<std::mutex> lock(mlock_send);
            while (!sendPackets.empty()) {
                auto packet = sendPackets.front();
                sendPackets.pop();
                RTMPPacket_Free(packet);
                free(packet);
            }
        }
    }
}

void RtmpDump::sendSpsPps(const char *sps, int sps_len, const char *pps, int pps_len) {
    std::lock_guard<std::mutex> lock_stop(mlock_stop);
    if (is_stop) return;
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
    int i = 0;
    packet->m_body[i++] = 0x17;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    //AVCDecoderConfigurationRecord
    packet->m_body[i++] = 0x01;
    packet->m_body[i++] = sps[1];
    packet->m_body[i++] = sps[2];
    packet->m_body[i++] = sps[3];
    packet->m_body[i++] = 0xff;
    //sps
    packet->m_body[i++] = 0xe1;
    packet->m_body[i++] = (sps_len >> 8) & 0xff;
    packet->m_body[i++] = (sps_len) & 0xff;
    memcpy(packet->m_body + i, sps, sps_len);
    i += sps_len;
    //pps
    packet->m_body[i++] = 0x01;
    packet->m_body[i++] = (pps_len >> 8) & 0xff;
    packet->m_body[i++] = (pps_len) & 0xff;
    memcpy(packet->m_body + i, pps, pps_len);
    ret = RTMP_SendPacket(rtmp, packet, 0);
    if(ret == FALSE){
        FLOGE("_sendSpsPps failed!");
    }
    RTMPPacket_Free(packet);
    free(packet);
    return ret;
}

void RtmpDump::sendVpsSpsPps(const char *vps, int vps_len, const char *sps, int sps_len,
                             const char *pps, int pps_len) {
    std::lock_guard<std::mutex> lock_stop(mlock_stop);
    if (is_stop) return;
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
    int i = 0;
    packet->m_body[i++] = 0x1c;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x01;
    packet->m_body[i++] = sps[6];
    packet->m_body[i++] = sps[7];
    packet->m_body[i++] = sps[8];
    packet->m_body[i++] = sps[9];
    packet->m_body[i++] = sps[12];
    packet->m_body[i++] = sps[13];
    packet->m_body[i++] = sps[14];
    //48 bit nothing deal in rtmp
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    //bit(16) avgFrameRate
    /* bit(2) constantFrameRate; */
    /* bit(3) numTemporalLayers; */
    /* bit(1) temporalIdNested; */
    packet->m_body[i++] = 0x83;
    /*unsigned int(8) numOfArrays; 03*/
    packet->m_body[i++] = 0x03;
    //vps 32
    packet->m_body[i++] = 0x20;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x01;
    packet->m_body[i++] = (vps_len >> 8) & 0xff;
    packet->m_body[i++] = (vps_len) & 0xff;
    memcpy(&packet->m_body[i], vps, vps_len);
    i += vps_len;
    //sps
    packet->m_body[i++] = 0x21;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x01;
    packet->m_body[i++] = (sps_len >> 8) & 0xff;
    packet->m_body[i++] = (sps_len) & 0xff;
    memcpy(&packet->m_body[i], sps, sps_len);
    i += sps_len;
    //pps
    packet->m_body[i++] = 0x22;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x01;
    packet->m_body[i++] = (pps_len >> 8) & 0xff;
    packet->m_body[i++] = (pps_len) & 0xff;
    memcpy(&packet->m_body[i], pps, pps_len);
    i += pps_len;
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = i;
    packet->m_nChannel = 0x04;
    packet->m_nTimeStamp = 0;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    ret = RTMP_SendPacket(rtmp, packet, 0);
    if(ret == FALSE){
        FLOGE("_sendVpsSpsPps failed!");
    }
    RTMPPacket_Free(packet);
    free(packet);
    return ret;
}

void RtmpDump::sendAvc(const char *data, int size, long pts) {
    std::lock_guard<std::mutex> lock_stop(mlock_stop);
    if (is_stop) return;
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
    packet->m_nTimeStamp = pts / 1000;
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
    std::lock_guard<std::mutex> lock_stop(mlock_stop);
    if (is_stop) return;
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
    packet->m_nTimeStamp = pts / 1000;
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

void RtmpDump::sendAacHead(const char *head, int size) {
    std::lock_guard<std::mutex> lock_stop(mlock_stop);
    if (is_stop) return;
    _head = static_cast<char *>(malloc(size * sizeof(char)));
    headLen = size;
    memcpy(_head, head, headLen);
}

int RtmpDump::_sendAacHead(const char *head, int headLen) {
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Reset(packet);
    int bodySize = 2 + headLen;
    int ret = RTMPPacket_Alloc(packet, bodySize);
    if (!ret) {
        callBack->javaOnError(-2);
        return FALSE;
    }
    // SoundFormat(4bits):10=AAC；
    // SoundRate(2bits):3=44kHz；
    // SoundSize(1bit):1=16-bit samples；
    // SoundType(1bit):1=Stereo sound；
    packet->m_body[0] = 0xAE;
    // 1表示AAC raw，
    // 0表示AAC sequence header
    packet->m_body[1] = 0x00;
    memcpy(&packet->m_body[2], head, headLen);

    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nBodySize = bodySize;
    packet->m_nChannel = 0x05;
    packet->m_hasAbsTimestamp = 0;
    packet->m_nTimeStamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_MEDIUM;
    ret = RTMP_SendPacket(rtmp, packet, 0);
    if(ret == FALSE){
        FLOGE("_sendAacHead failed!");
    }
    RTMPPacket_Free(packet);
    free(packet);
    return ret;
}

void RtmpDump::sendAac(const char *data, int size, long pts) {
    std::lock_guard<std::mutex> lock_stop(mlock_stop);
    if (is_stop) return;
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Reset(packet);
    int bodySize = 2 + size;
    int ret = RTMPPacket_Alloc(packet, bodySize);
    if (!ret) {
        callBack->javaOnError(-2);
        return;
    }
    // SoundFormat(4bits):10=AAC；
    // SoundRate(2bits):3=44kHz；
    // SoundSize(1bit):1=16-bit samples；
    // SoundType(1bit):1=Stereo sound；
    packet->m_body[0] = 0xAE;
    // 1表示AAC raw，
    // 0表示AAC sequence header
    packet->m_body[1] = 0x01;
    memcpy(&packet->m_body[2], data, size);

    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nBodySize = bodySize;
    packet->m_nChannel = 0x05;
    packet->m_hasAbsTimestamp = pts / 1000;
    packet->m_nTimeStamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;

    {
        std::lock_guard<std::mutex> lock(mlock_send);
        sendPackets.push(packet);
        mcond_send.notify_one();
    }
}
