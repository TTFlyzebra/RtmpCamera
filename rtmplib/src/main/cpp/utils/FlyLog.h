#ifndef __FLYLOG_H__
#define __FLYLOG_H__

#include <android/log.h>

#define LOG_TAG "RESRTMP"
#define FLOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define FLOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__)

#endif//__FLYLOG_H__