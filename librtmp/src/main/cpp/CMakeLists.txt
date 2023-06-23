cmake_minimum_required(VERSION 3.10.2)

#project 1
project("rtmpdump")
include_directories(
        ${CMAKE_SOURCE_DIR}
        ${CMAKE_SOURCE_DIR}/include)
#set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DNO_CRYPTO")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DUSE_OPENSSL")

add_library(
        rtmpdump
        SHARED
        RtmpDump_jni.cpp
        RtmpDump.cpp
        CallBack.cpp
        buffer/BufferManager.cpp
        buffer/LoopBuf.cpp
        librtmp/amf.c
        librtmp/hashswf.c
        librtmp/FlyLog.c
        librtmp/parseurl.c
        librtmp/rtmp.c)

add_library(
        ssl
        SHARED
        IMPORTED)

set_target_properties(
        ssl
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libssl.so)

add_library(
        crypto
        SHARED
        IMPORTED)

set_target_properties(
        crypto
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libcrypto.so)

target_link_libraries(
        rtmpdump
        ssl
        crypto
        z
        log)