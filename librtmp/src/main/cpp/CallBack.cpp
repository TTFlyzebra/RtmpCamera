//
// Created by Administrator on 2023/6/23.
//

#include "CallBack.h"
#include "utils/FlyLog.h"

CallBack::CallBack(JavaVM *jvm, JNIEnv *env, jobject thiz) {
    FLOGI("%s()", __func__);
    javeVM = jvm;
    jniEnv = env;
    jObject = jniEnv->NewGlobalRef(thiz);
    jclass  cls = jniEnv->GetObjectClass(jObject);
    if(!cls) {
        FLOGE("find jclass faild");
        return;
    }
    onError = jniEnv->GetMethodID(cls, "onError", "(I)V");
    jniEnv->DeleteLocalRef(cls);
}

CallBack::~CallBack() {
    int status = javeVM->GetEnv((void **) &jniEnv, JNI_VERSION_1_4);
    bool isAttacked = false;
    if(status < 0) {
        status = javeVM->AttachCurrentThread(&jniEnv, nullptr);
        if(status < 0) {
            FLOGE("onVideoEncode: failed to attach current thread");
            return;
        }
        isAttacked = true;
    }
    jniEnv->DeleteGlobalRef(jObject);
    if(isAttacked){
        (javeVM)->DetachCurrentThread();
    }
    FLOGI("%s()", __func__);
}

void CallBack::javaOnError(int error) {
    int status = javeVM->GetEnv((void **) &jniEnv, JNI_VERSION_1_4);
    bool isAttacked = false;
    if(status < 0) {
        status = javeVM->AttachCurrentThread(&jniEnv, nullptr);
        if(status < 0) {
            FLOGE("onStop: failed to attach current thread");
            return;
        }
        isAttacked = true;
    }
    jniEnv->CallVoidMethod(jObject, onError, error);
    if(isAttacked){
        (javeVM)->DetachCurrentThread();
    }
}
