//
// Created by FlyZebra on 2018/11/8.
//

#include "jni.h"
#include "FlyLog.h"
#include "libyuv.h"
#include "string.h"

JavaVM *javaVM = nullptr;

extern "C" jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    javaVM = vm;
    JNIEnv *env = nullptr;
    jint result = -1;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGE("JNI OnLoad failed\n");
        return result;
    }
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT void JNICALL Java_com_flyzebra_libyuv_LibYuvTools_NV21ToI420
        (JNIEnv *env, jclass thiz, jbyteArray src_nv21, jbyteArray obj_420p, jint width, jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(src_nv21, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(obj_420p, JNI_FALSE);
    libyuv::NV21ToI420(
            src,
            width,
            src + width * height,
            width,
            dst,
            width,
            dst + width * height,
            (width + 1) / 2,
            dst + width * height + width * height / 4,
            (width + 1) / 2,
            width,
            height);
    env->ReleaseByteArrayElements(src_nv21, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(obj_420p, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL Java_com_flyzebra_libyuv_LibYuvTools_NV12ToI420
        (JNIEnv *env, jclass thiz, jbyteArray src_nv21, jbyteArray obj_420p, jint width, jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(src_nv21, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(obj_420p, JNI_FALSE);
    libyuv::NV12ToI420(
            src,
            width,
            src + width * height,
            width,
            dst,
            width,
            dst + width * height,
            (width + 1) / 2,
            dst + width * height + width * height / 4,
            (width + 1) / 2,
            width,
            height);
    env->ReleaseByteArrayElements(src_nv21, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(obj_420p, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL Java_com_flyzebra_libyuv_LibYuvTools_I422ToI420
        (JNIEnv *env, jclass thiz, jbyteArray src_nv21, jbyteArray obj_420p, jint width, jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(src_nv21, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(obj_420p, JNI_FALSE);
    memset(src + width * height,128,width * height);
    libyuv::I422ToI420(
            src,
            width,
            src + width * height,
            width,
            src + width * height +  width * height / 2,
            width,
            dst,
            width,
            dst + width * height,
            (width + 1) / 2,
            dst + width * height + width * height / 4,
            (width + 1) / 2,
            width,
            height);
    env->ReleaseByteArrayElements(src_nv21, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(obj_420p, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}

