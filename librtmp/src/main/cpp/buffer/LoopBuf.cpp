#include "buffer/LoopBuf.h"
#include <stdlib.h>
#include <string.h>
#include <algorithm>
#include "utils/FlyLog.h"

LoopBuf::LoopBuf(size_t capacity, size_t itemsize, const char* tag)
    : _capacity(capacity)
    , _itemsize(itemsize)
    , _tag(tag)
    , _data(nullptr)
    , _spos(0)
    , _epos(0)
    , _size(0)
{
    _data = (char*)malloc((capacity + itemsize) * sizeof(char));
}

LoopBuf::~LoopBuf()
{
    free(_data);
}

size_t LoopBuf::push(const char* data, size_t size)
{
    if ((_size + size) > _capacity) {
        FLOGE("[%s]LoopBuf push is full, [%zu][%zu]", _tag, size, _capacity-_size);
        return 0;
    }

    memcpy(_data + _epos, data, size);

    if(_epos < _itemsize){
        int32_t c_size = std::min(size, _itemsize - _epos);
        memcpy(_data + _capacity + _epos, _data + _epos, c_size);
    }else if((_epos + size) > _capacity){
        int32_t c_size = _epos + size -_capacity;
        memcpy(_data, _data + _capacity, c_size);
    }
    _epos = (_epos + size) % _capacity;
    _size += size;
    return size;
}

char* LoopBuf::pushTemp(size_t size)
{
    if ((_size + size) > _capacity) {
        FLOGE("[%s]LoopBuf pushTemp is full, [%zu][%zu]", _tag, size, _capacity-_size);
        return nullptr;
    }
    return _data + _epos;
}

size_t LoopBuf::pushFlash(size_t size)
{
    if ((_size + size) > _capacity) {
        FLOGE("[%s]LoopBuf pushFlash is full, [%zu][%zu]", _tag, size, _capacity-_size);
        return 0;
    }

    if(_epos < _itemsize){
        int32_t c_size = std::min(size, _itemsize - _epos);
        memcpy(_data + _capacity + _epos, _data + _epos, c_size);
    }else if((_epos + size) > _capacity){
        int32_t c_size = _epos + size -_capacity;
        memcpy(_data, _data + _capacity, c_size);
    }
    _epos = (_epos + size) % _capacity;
    _size += size;
    return size;
}

char* LoopBuf::pop(size_t size)
{
    if (size > _size) {
        FLOGE("[%s]LoopBuf pop is not has enough size, [%zu][%zu]", _tag, _size+0, size);
        return nullptr;
    }
    char* data = _data + _spos;
    _spos = (_spos + size) % _capacity;
    _size -= size;
    return data;
}

char* LoopBuf::popTemp(size_t size)
{
    if (size > _size) {
        FLOGE("[%s]LoopBuf popTemp is not has enough size, [%zu][%zu]", _tag, _size+0, size);
        return nullptr;
    }
    return _data + _spos;
}

size_t LoopBuf::popFlash(size_t size)
{
    if (size > _size) {
        FLOGE("[%s]LoopBuf popFlash is not has enough size, [%zu][%zu]", _tag, _size+0, size);
        return 0;
    }
    _size -= size;
    _spos = (_spos + size) % _capacity;
    return size;
}

bool LoopBuf::empty()
{
    return _size <= 0;
}

size_t LoopBuf::size()
{
    return _size;
}

size_t LoopBuf::capacity()
{
    return _capacity;
}

size_t LoopBuf::remaining()
{
    return _capacity-_size;
}

char LoopBuf::at(int32_t pos) {
    return _data[_spos + pos];
}

void LoopBuf::clear()
{   
    _spos = 0;
    _epos = 0;
    _size = 0;
}
