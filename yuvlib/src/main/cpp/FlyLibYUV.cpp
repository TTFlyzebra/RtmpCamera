//
// Created by FlyZebra on 2018/11/8.
//

#include "jni.h"
#include "FlyLog.h"

JavaVM* javaVM = nullptr;

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
JNIEXPORT void JNICALL Java_com_flyzebra_libyuv_LibYUVHelper_NV21TOYUV420P
(JNIEnv * env, jclass thiz, jbyteArray src_nv21, jbyteArray y,jbyteArray uv,jbyteArray obj_420p,jint width, jint height) {
    return;
}

