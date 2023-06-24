//
// Created by Administrator on 2023/6/23.
//

#ifndef RTMPCAMERA_RTMPDUMP_H
#define RTMPCAMERA_RTMPDUMP_H

#include <jni.h>
#include <queue>
#include <mutex>
#include <thread>
#include <condition_variable>
#include "CallBack.h"

class RTMP;
class RTMPPacket;

class RtmpDump {
public:
    RtmpDump(JavaVM *jvm, JNIEnv *env, jobject thiz, const char* url);

    ~RtmpDump();

    void sendSpsPps(const char *sps, int spsLen, const char *pps, int ppsLen);

    void sendVpsSpsPps(const char *vps, int vpsLen, const char *sps, int spsLen, const char *pps, int ppsLen);

    void sendAvc(const char *data, int size, long pts);

    void sendHevc(const char *data, int size, long pts);

    void sendAacHead(const char *head, int size);

    void sendAac(const char *data, int size, long pts);

private:
    void rtmpConnect();

    void rtmpDisconnect();

    void sendThread();

    int _sendSpsPps(const char *sps, int spsLen, const char *pps, int ppsLen);

    int _sendVpsSpsPps(const char *vps, int vpsLen, const char *sps, int spsLen, const char *pps, int ppsLen);

    int _sendAacHead(const char *head, int headLen);

private:
    bool is_stop;
    std::mutex mlock_stop;

    CallBack *callBack;

    char rtmp_url[1024];
    std::thread *send_t;
    std::mutex mlock_send;
    std::queue<RTMPPacket*> sendPackets;
    std::condition_variable mcond_send;

    RTMP *rtmp;
    bool is_connect;

    char* _vps;
    int vpsLen;
    char* _sps;
    int spsLen;
    char* _pps;
    int ppsLen;

    char* _head;
    int headLen;
};


#endif //RTMPCAMERA_RTMPDUMP_H
