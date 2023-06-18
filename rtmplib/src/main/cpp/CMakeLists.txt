cmake_minimum_required(VERSION 3.10.2)

project("zebra_rtmps")
include_directories(${CMAKE_SOURCE_DIR})
#set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DNO_CRYPTO")

add_library(
        zebra_rtmps
        SHARED
        libresrtmp.c)

add_library(
        c++
        SHARED
        IMPORTED)

set_target_properties(
        c++
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libc++.so)

add_library(
        rtmps
        SHARED
        IMPORTED)

set_target_properties(
        rtmps
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/librtmps.so)

target_link_libraries(
        zebra_rtmps
        c++
        rtmps
        z
        log)
