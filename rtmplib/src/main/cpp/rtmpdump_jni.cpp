#include <malloc.h>
#include <jni.h>
#include <cstring>

extern "C" {
#include <librtmp/rtmp.h>
}

#include "utils/FlyLog.h"

/*
 * Class:     com_flyzebra_rtmp_RtmpClient
 * Method:    open
 * Signature: (Ljava/lang/String;)I
 */
extern "C"
JNIEXPORT jlong JNICALL
Java_com_flyzebra_rtmp_RtmpDump_open
        (JNIEnv *env, jclass thiz, jstring url_, jboolean isPublishMode) {
    const char *url = env->GetStringUTFChars(url_, 0);
    FLOGD("RTMP_OPENING:%s", url);
    RTMP *rtmp = RTMP_Alloc();
    if (rtmp == NULL) {
        FLOGD("RTMP_Alloc=NULL");
        return -1;
    }

    RTMP_Init(rtmp);
    int ret = RTMP_SetupURL(rtmp, (char *) url);

    if (!ret) {
        RTMP_Free(rtmp);
        FLOGD("RTMP_SetupURL ret=%d", ret);
        return -1;
    }
    if (isPublishMode) {
        RTMP_EnableWrite(rtmp);
    }

    ret = RTMP_Connect(rtmp, NULL);
    if (!ret) {
        RTMP_Free(rtmp);
        FLOGD("RTMP_Connect ret=%d", ret);
        return -1;
    }
    ret = RTMP_ConnectStream(rtmp, 0);

    if (!ret) {
        ret = RTMP_ConnectStream(rtmp, 0);
        RTMP_Close(rtmp);
        RTMP_Free(rtmp);
        FLOGD("RTMP_ConnectStream ret=%s", ret);
        return -1;
    }
    env->ReleaseStringUTFChars(url_, url);
    FLOGD("RTMP_OPENED");
    return reinterpret_cast<jlong>(rtmp);
}

/*
 * Class:     com_flyzebra_rtmp_RtmpClient
 * Method:    read
 * Signature: ([CI)I
 */
extern "C"
JNIEXPORT jint JNICALL
Java_com_flyzebra_rtmp_RtmpDump_read(JNIEnv *env, jclass thiz, jlong rtmp, jbyteArray data_,
                                     jint offset, jint size) {
    char *data = static_cast<char *>(malloc(size * sizeof(char)));
    int readCount = RTMP_Read((RTMP *) rtmp, data, size);
    if (readCount > 0) {
        env->SetByteArrayRegion(data_, offset, readCount,
                                reinterpret_cast<const jbyte *>(data));  // copy
    }
    free(data);

    return readCount;
}

/*
 * Class:     com_flyzebra_rtmp_RtmpClient
 * Method:    write
 * Signature: ([CI)I
 */
extern "C"
JNIEXPORT jint JNICALL
Java_com_flyzebra_rtmp_RtmpDump_write(JNIEnv *env, jclass thiz, jlong rtmp, jbyteArray data,
                                      jint size, jint type, jint ts) {
    //LOGD("start write");
    jbyte *buffer = env->GetByteArrayElements(data, NULL);
    RTMPPacket *packet = (RTMPPacket *) malloc(sizeof(RTMPPacket));
    RTMPPacket_Alloc(packet, size);
    RTMPPacket_Reset(packet);
    if (type == RTMP_PACKET_TYPE_INFO) { // metadata
        packet->m_nChannel = 0x03;
    } else if (type == RTMP_PACKET_TYPE_VIDEO) { // video
        packet->m_nChannel = 0x04;
    } else if (type == RTMP_PACKET_TYPE_AUDIO) { //audio
        packet->m_nChannel = 0x05;
    } else {
        packet->m_nChannel = -1;
    }

    packet->m_nInfoField2 = ((RTMP *) rtmp)->m_stream_id;
    memcpy(packet->m_body, buffer, size);
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_hasAbsTimestamp = FALSE;
    packet->m_nTimeStamp = ts;
    packet->m_packetType = type;
    packet->m_nBodySize = size;
    int ret = RTMP_SendPacket((RTMP *) rtmp, packet, 0);
    RTMPPacket_Free(packet);
    free(packet);
    env->ReleaseByteArrayElements(data, buffer, 0);
    if (!ret) {
        FLOGD("end write error %d", errno);
        return errno;
    } else {
        //LOGD("end write success");
        return 0;
    }
}

/*
 * Class:     com_flyzebra_rtmp_RtmpClient
 * Method:    close
 * Signature: ()I
 */
extern "C"
JNIEXPORT jint JNICALL
Java_com_flyzebra_rtmp_RtmpDump_close(JNIEnv *env, jclass thiz, jlong rtmp) {
    RTMP_Close((RTMP *) rtmp);
    RTMP_Free((RTMP *) rtmp);
    return 0;
}