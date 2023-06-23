//
// Created by Administrator on 2023/6/23.
//

#ifndef RTMPCAMERA_CALLBACK_H
#define RTMPCAMERA_CALLBACK_H

#include <jni.h>

class CallBack {
public:
    CallBack(JavaVM* jvm, JNIEnv *env, jobject thiz);
    ~CallBack();
    void javaOnError(int error);

private:
    JavaVM* javeVM ;
    JNIEnv *jniEnv ;
    jobject jObject;
    jmethodID onError;
};


#endif //RTMPCAMERA_CALLBACK_H
