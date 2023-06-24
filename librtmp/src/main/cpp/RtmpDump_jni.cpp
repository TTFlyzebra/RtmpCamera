#include <jni.h>
#include <cstring>
#include <malloc.h>

extern "C" {
#include <librtmp/rtmp.h>
}

#include "utils/FlyLog.h"
#include "RtmpDump.h"

JavaVM *javaVM = nullptr;

extern "C" jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    javaVM = vm;
    JNIEnv *env = nullptr;
    jint result = -1;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        FLOGE("JNI OnLoad failed\n");
        return result;
    }
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_flyzebra_rtmp_RtmpDump__1init(JNIEnv *env, jobject clazz, jstring jurl) {
    const char *url = env->GetStringUTFChars(jurl, JNI_FALSE);
    auto *rtmpDump = new RtmpDump(javaVM, env, clazz, url);
    env->ReleaseStringUTFChars(jurl, url);
    return reinterpret_cast<jlong>(rtmpDump);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_rtmp_RtmpDump__1release(JNIEnv *env, jobject clazz, jlong p_obj) {
    RtmpDump *rtmpDump = reinterpret_cast<RtmpDump *>(p_obj);
    delete rtmpDump;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_rtmp_RtmpDump__1sendSpsPps(JNIEnv *env, jobject thiz, jlong p_obj,
                                             jbyteArray jsps, jint sps_len, jbyteArray jpps,
                                             jint pps_len) {
    const char *sps = reinterpret_cast<const char *>(env->GetByteArrayElements(jsps, JNI_FALSE));
    const char *pps = reinterpret_cast<const char *>(env->GetByteArrayElements(jpps, JNI_FALSE));
    RtmpDump *rtmpDump = reinterpret_cast<RtmpDump *>(p_obj);
    rtmpDump->sendSpsPps(sps, sps_len, pps, pps_len);
    env->ReleaseByteArrayElements(jsps, (jbyte *) sps, JNI_ABORT);
    env->ReleaseByteArrayElements(jpps, (jbyte *) pps, JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_rtmp_RtmpDump__1sendVpsSpsPps(JNIEnv *env, jobject thiz, jlong p_obj,
                                                jbyteArray jvps, jint vps_len, jbyteArray jsps,
                                                jint sps_len, jbyteArray jpps, jint pps_len) {
    const char *vps = reinterpret_cast<const char *>(env->GetByteArrayElements(jvps, JNI_FALSE));
    const char *sps = reinterpret_cast<const char *>(env->GetByteArrayElements(jsps, JNI_FALSE));
    const char *pps = reinterpret_cast<const char *>(env->GetByteArrayElements(jpps, JNI_FALSE));
    RtmpDump *rtmpDump = reinterpret_cast<RtmpDump *>(p_obj);
    rtmpDump->sendVpsSpsPps(vps, vps_len, sps, sps_len, pps, pps_len);
    env->ReleaseByteArrayElements(jvps, (jbyte *) vps, JNI_ABORT);
    env->ReleaseByteArrayElements(jsps, (jbyte *) sps, JNI_ABORT);
    env->ReleaseByteArrayElements(jpps, (jbyte *) pps, JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_rtmp_RtmpDump__1sendAvc(JNIEnv *env, jobject thiz, jlong p_obj, jbyteArray jdata,
                                          jint size, jlong pts) {
    const char *data = reinterpret_cast<const char *>(env->GetByteArrayElements(jdata, JNI_FALSE));
    RtmpDump *rtmpDump = reinterpret_cast<RtmpDump *>(p_obj);
    rtmpDump->sendAvc(data, size, pts);
    env->ReleaseByteArrayElements(jdata, (jbyte *) data, JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_rtmp_RtmpDump__1sendHevc(JNIEnv *env, jobject thiz, jlong p_obj, jbyteArray jdata,
                                           jint size, jlong pts) {
    const char *data = reinterpret_cast<const char *>(env->GetByteArrayElements(jdata, JNI_FALSE));
    RtmpDump *rtmpDump = reinterpret_cast<RtmpDump *>(p_obj);
    rtmpDump->sendHevc(data, size, pts);
    env->ReleaseByteArrayElements(jdata, (jbyte *) data, JNI_ABORT);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_rtmp_RtmpDump__1sendAacHead(JNIEnv *env, jobject thiz, jlong p_obj,
                                              jbyteArray jhead, jint headLen) {
    const char *head = reinterpret_cast<const char *>(env->GetByteArrayElements(jhead, JNI_FALSE));
    RtmpDump *rtmpDump = reinterpret_cast<RtmpDump *>(p_obj);
    rtmpDump->sendAacHead(head, headLen);
    env->ReleaseByteArrayElements(jhead, (jbyte *) head, JNI_ABORT);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_rtmp_RtmpDump__1sendAac(JNIEnv *env, jobject thiz, jlong p_obj, jbyteArray jdata,
                                          jint size, jlong pts) {
    const char *data = reinterpret_cast<const char *>(env->GetByteArrayElements(jdata, JNI_FALSE));
    RtmpDump *rtmpDump = reinterpret_cast<RtmpDump *>(p_obj);
    rtmpDump->sendAac(data, size, pts);
    env->ReleaseByteArrayElements(jdata, (jbyte *) data, JNI_ABORT);
}