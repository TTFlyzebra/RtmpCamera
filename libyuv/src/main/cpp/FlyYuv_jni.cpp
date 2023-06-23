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
Java_com_flyzebra_libyuv_FlyYuv_NV12ToI420(JNIEnv *env, jclass thiz, jbyteArray src_nv21, jbyteArray obj_420p,
                                             jint width, jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(src_nv21, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(obj_420p, JNI_FALSE);
    FlyYuv::NV12ToI420(src, dst, width, height);
    env->ReleaseByteArrayElements(src_nv21, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(obj_420p, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_NV12ToARGB(JNIEnv *env, jclass thiz, jbyteArray src_nv21, jbyteArray obj_argb,
                                             jint width, jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(src_nv21, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(obj_argb, JNI_FALSE);
    FlyYuv::NV12ToARGB(src, dst, width, height);
    env->ReleaseByteArrayElements(src_nv21, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(obj_argb, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_I420ToNV12(JNIEnv *env, jclass thiz, jbyteArray src_420, jbyteArray obj_nv12,
                                             jint width, jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(src_420, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(obj_nv12, JNI_FALSE);
    FlyYuv::I420ToNV12(src, dst, width, height);
    env->ReleaseByteArrayElements(src_420, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(obj_nv12, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_ARGBToI420(JNIEnv *env, jclass clazz, jbyteArray src_rgba, jbyteArray obj_420p,
                                             jint offset, jint width, jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(src_rgba, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(obj_420p, JNI_FALSE);
    FlyYuv::ARGBToI420(src, dst, offset, width, height);
    env->ReleaseByteArrayElements(src_rgba, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(obj_420p, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_I420ToARGB(JNIEnv *env, jclass clazz, jbyteArray src_420p, jbyteArray dst_rgba,
                                             jint width, jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(src_420p, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(dst_rgba, JNI_FALSE);
    FlyYuv::I420ToARGB(src, dst, width, height);
    env->ReleaseByteArrayElements(src_420p, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(dst_rgba, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_RGB24ToI420(JNIEnv *env, jclass clazz, jbyteArray src_rgb24, jbyteArray obj_420p,
                                              jint offset, jint width, jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(src_rgb24, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(obj_420p, JNI_FALSE);
    FlyYuv::RGB24ToI420(src, dst, offset, width, height);
    env->ReleaseByteArrayElements(src_rgb24, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(obj_420p, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_I422ToI420(JNIEnv *env, jclass thiz, jbyteArray src_nv21, jbyteArray obj_420p,
                                             jint width, jint height) {
    auto *src = (unsigned char *) env->GetByteArrayElements(src_nv21, JNI_FALSE);
    auto *dst = (unsigned char *) env->GetByteArrayElements(obj_420p, JNI_FALSE);
    FlyYuv::I422ToI420(src, dst, width, height);
    env->ReleaseByteArrayElements(src_nv21, reinterpret_cast<jbyte *>(src), JNI_ABORT);
    env->ReleaseByteArrayElements(obj_420p, reinterpret_cast<jbyte *>(dst), JNI_ABORT);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_I420AddMark(JNIEnv *env, jclass clazz, jbyteArray obj_420p, jbyteArray yuv_mark,
                                              jint width, jint height) {
    auto *i420 = (unsigned char *) env->GetByteArrayElements(obj_420p, JNI_FALSE);
    auto *mark = (unsigned char *) env->GetByteArrayElements(yuv_mark, JNI_FALSE);
    FlyYuv::I420AddMark(i420, mark, width, height);
    env->ReleaseByteArrayElements(obj_420p, reinterpret_cast<jbyte *>(i420), JNI_ABORT);
    env->ReleaseByteArrayElements(yuv_mark, reinterpret_cast<jbyte *>(mark), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_I420Compose(JNIEnv *env, jclass clazz, jbyteArray ji420, jbyteArray jback,
                                              jbyteArray jwater, jint width, jint height, jbyteArray jmapFilter) {
    auto *i420 = (unsigned char *) env->GetByteArrayElements(ji420, JNI_FALSE);
    auto *back = (unsigned char *) env->GetByteArrayElements(jback, JNI_FALSE);
    auto *water = (unsigned char *) env->GetByteArrayElements(jwater, JNI_FALSE);
    auto *mapFilter = (unsigned char *) env->GetByteArrayElements(jmapFilter, JNI_FALSE);
    FlyYuv::I420Compose(i420, back, water, width, height, mapFilter);
    env->ReleaseByteArrayElements(ji420, reinterpret_cast<jbyte *>(i420), JNI_ABORT);
    env->ReleaseByteArrayElements(jback, reinterpret_cast<jbyte *>(back), JNI_ABORT);
    env->ReleaseByteArrayElements(jwater, reinterpret_cast<jbyte *>(water), JNI_ABORT);
    env->ReleaseByteArrayElements(jmapFilter, reinterpret_cast<jbyte *>(mapFilter), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_I420Filter(JNIEnv *env, jclass clazz, jbyteArray ji420, jbyteArray jback,
                                             jint width, jint height, jbyteArray jmapFilter) {
    auto *i420 = (unsigned char *) env->GetByteArrayElements(ji420, JNI_FALSE);
    auto *back = (unsigned char *) env->GetByteArrayElements(jback, JNI_FALSE);
    auto *mapFilter = (unsigned char *) env->GetByteArrayElements(jmapFilter, JNI_FALSE);
    FlyYuv::I420Filter(i420, back, width, height, mapFilter);
    env->ReleaseByteArrayElements(ji420, reinterpret_cast<jbyte *>(i420), JNI_ABORT);
    env->ReleaseByteArrayElements(jback, reinterpret_cast<jbyte *>(back), JNI_ABORT);
    env->ReleaseByteArrayElements(jmapFilter, reinterpret_cast<jbyte *>(mapFilter), JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_libyuv_FlyYuv_FillFilter(JNIEnv *env, jclass clazz, jbyteArray jmap_filter,
                                             jint size1, jbyteArray jyuv_filter, jint size2) {
    auto *map_filter = (unsigned char *) env->GetByteArrayElements(jmap_filter, JNI_FALSE);
    auto *yuv_filter = (unsigned char *) env->GetByteArrayElements(jyuv_filter, JNI_FALSE);
    memset(map_filter, 0, size1);
    for (int i = 0; i <= yuv_filter[1]; i++) {
        map_filter[(unsigned char) (yuv_filter[0] + i)] = 1;
        map_filter[(unsigned char) (yuv_filter[0] - i)] = 1;
    }
    for (int i = 0; i <= yuv_filter[3]; i++) {
        map_filter[256 + (unsigned char) (yuv_filter[2] + i)] = 1;
        map_filter[256 + (unsigned char) (yuv_filter[2] - i)] = 1;
    }
    for (int i = 0; i <= yuv_filter[5]; i++) {
        map_filter[512 + (unsigned char) (yuv_filter[4] + i)] = 1;
        map_filter[512 + (unsigned char) (yuv_filter[4] - i)] = 1;
    }
    env->ReleaseByteArrayElements(jmap_filter, reinterpret_cast<jbyte *>(map_filter), JNI_ABORT);
    env->ReleaseByteArrayElements(jyuv_filter, reinterpret_cast<jbyte *>(yuv_filter), JNI_ABORT);
}