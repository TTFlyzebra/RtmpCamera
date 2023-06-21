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
        rtmpdump_jni.cpp
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

#project 2
project("rtmpffmpeg")
include_directories(
        ${CMAKE_SOURCE_DIR}
        ${CMAKE_SOURCE_DIR}/include)

add_library(
        rtmpffmpeg
        SHARED
        ffmpeg_jni.cpp
        ffmpeg/FFRtmp.cpp)

add_library(
        avcodec
        SHARED
        IMPORTED)

set_target_properties(
        avcodec
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libavcodec.so)

add_library(
        avfilter
        SHARED
        IMPORTED)

set_target_properties(
        avfilter
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libavfilter.so)

add_library(
        avformat
        SHARED
        IMPORTED)

set_target_properties(
        avformat
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libavformat.so)

add_library(
        avutil
        SHARED
        IMPORTED)

set_target_properties(
        avutil
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libavutil.so)

add_library(
        swresample
        SHARED
        IMPORTED)

set_target_properties(
        swresample
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libswresample.so)

add_library(
        swscale
        SHARED
        IMPORTED)

set_target_properties(
        swscale
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libswscale.so)

target_link_libraries(
        rtmpffmpeg
        avcodec
        avfilter
        avformat
        avutil
        swresample
        swscale
        log)