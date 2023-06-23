#ifndef F_ZEBRA_LOOPER_H
#define F_ZEBRA_LOOPER_H

#include <atomic>

class LoopBuf
{
public:
    LoopBuf(size_t capacity, size_t itemsize, const char* tag);
    ~LoopBuf();

    size_t push(const char* data, size_t size);
    char* pushTemp(size_t size = 0);
    size_t pushFlash(size_t size = 0);

    char* pop(size_t size);
    char* popTemp(size_t size = 0);
    size_t popFlash(size_t size = 0);

    bool empty();
    size_t size();
    size_t capacity();
    size_t remaining();

    char at(int32_t pos);
    void clear();

private:
    size_t _capacity;
    size_t _itemsize;
    const char* _tag;
    char* _data;
    std::atomic<size_t> _spos;
    std::atomic<size_t> _epos;
    std::atomic<size_t> _size;
};

#endif //F_ZEBRA_LOOPER_H