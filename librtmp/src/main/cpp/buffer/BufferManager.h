//
// Created by Administrator on 2022/4/25.
//

#ifndef F_ZEBRA_BUFFERMANAGER_H
#define F_ZEBRA_BUFFERMANAGER_H

#include <thread>
#include <mutex>
#include <list>

class LoopBuf;

class BufferManager {
public:
    static void init();
    static void release();
    static BufferManager* instance();
    ~BufferManager();
    LoopBuf* createBuffer(size_t capacity, size_t itemsize, const char* tag);
    void releaseBuffer(LoopBuf* buffer);

private:
    BufferManager();
    void selfFixedThread();

private:
    static BufferManager* m_pInstance;
    bool is_stop;
    std::thread* fixed_t;
    std::mutex mlock_list;
    std::list<LoopBuf*> bufferList;
};

#endif //F_ZEBRA_BUFFERMANAGER_H
