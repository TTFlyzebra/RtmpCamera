cmake_minimum_required(VERSION 3.10.2)

project("zebra_rtmps")
include_directories(${CMAKE_SOURCE_DIR})
#set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DNO_CRYPTO")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DUSE_OPENSSL")

add_library(
        zebra_rtmps
        SHARED
        libresrtmp.c
        librtmp/amf.c
        librtmp/hashswf.c
        librtmp/log.c
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
        zebra_rtmps
        ssl
        crypto
        z
        log)
