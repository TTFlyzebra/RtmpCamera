//
// Created by FlyZebra on 2018/11/8.
//

#include <jni.h>
#include <libyuv.h>
#include <cstring>
#include "FlyYuv.h"

JavaVM *javaVM = nullptr;

extern "C" jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    javaVM = vm;
    JNIEnv *env = nullptr;
    jint result = -1;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_NV12ToI420(JNIEnv *env, jclass thiz, jbyteArray jsrc,
                                           jbyteArray jdst, jint dst_offset, jint width,
                                           jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(jsrc, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(jdst, JNI_FALSE);
    FlyYuv::NV12ToI420(src, dst, dst_offset, width, height);
    env->ReleaseByteArrayElements(jsrc, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(jdst, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_NV12ToARGB(JNIEnv *env, jclass thiz, jbyteArray jsrc,
                                           jbyteArray jdst, jint dst_offset, jint width,
                                           jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(jsrc, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(jdst, JNI_FALSE);
    FlyYuv::NV12ToARGB(src, dst, dst_offset, width, height);
    env->ReleaseByteArrayElements(jsrc, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(jdst, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_I420ToNV12(JNIEnv *env, jclass thiz, jbyteArray jsrc,
                                           jbyteArray jdst, jint dst_offset, jint width,
                                           jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(jsrc, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(jdst, JNI_FALSE);
    FlyYuv::I420ToNV12(src, dst, dst_offset, width, height);
    env->ReleaseByteArrayElements(jsrc, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(jdst, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_ARGBToI420(JNIEnv *env, jclass clazz, jbyteArray jsrc,
                                           jbyteArray jdst, jint dst_offset, jint width,
                                           jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(jsrc, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(jdst, JNI_FALSE);
    FlyYuv::ARGBToI420(src, dst, dst_offset, width, height);
    env->ReleaseByteArrayElements(jsrc, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(jdst, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_ARGBToNV12(JNIEnv *env, jclass clazz, jbyteArray jsrc,
                                           jbyteArray jdst, jint dst_offset, jint width,
                                           jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(jsrc, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(jdst, JNI_FALSE);
    FlyYuv::ARGBToNV12(src, dst, dst_offset, width, height);
    env->ReleaseByteArrayElements(jsrc, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(jdst, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_ARGBToNV21(JNIEnv *env, jclass clazz, jbyteArray jsrc,
                                           jbyteArray jdst, jint dst_offset, jint width,
                                           jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(jsrc, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(jdst, JNI_FALSE);
    FlyYuv::ARGBToNV21(src, dst, dst_offset, width, height);
    env->ReleaseByteArrayElements(jsrc, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(jdst, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_I420ToARGB(JNIEnv *env, jclass clazz, jbyteArray jsrc,
                                           jbyteArray jdst, jint dst_offset, jint width,
                                           jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(jsrc, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(jdst, JNI_FALSE);
    FlyYuv::I420ToARGB(src, dst, dst_offset, width, height);
    env->ReleaseByteArrayElements(jsrc, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(jdst, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_RGB24ToI420(JNIEnv *env, jclass clazz, jbyteArray jsrc,
                                            jbyteArray jdst, jint dst_offset, jint width,
                                            jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(jsrc, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(jdst, JNI_FALSE);
    FlyYuv::RGB24ToI420(src, dst, dst_offset, width, height);
    env->ReleaseByteArrayElements(jsrc, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(jdst, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_I422ToI420(JNIEnv *env, jclass thiz, jbyteArray jsrc,
                                           jbyteArray jdst, jint dst_offset, jint width,
                                           jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(jsrc, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(jdst, JNI_FALSE);
    FlyYuv::I422ToI420(src, dst, dst_offset, width, height);
    env->ReleaseByteArrayElements(jsrc, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(jdst, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_I420Rotate(JNIEnv *env, jclass clazz, jbyteArray jsrc,
                                           jbyteArray jdst, jint dst_offset, jint width, jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(jsrc, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(jdst, JNI_FALSE);
    FlyYuv::I420Rotate(src, dst, dst_offset, width, height);
    env->ReleaseByteArrayElements(jsrc, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(jdst, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}