
cmake_minimum_required(VERSION 3.10.2)

project("fyuv")
include_directories(
        ${CMAKE_SOURCE_DIR}/include
)

add_library(
        fyuv
        SHARED
        FlyYuv.cpp
        FlyYuv_jni.cpp)

add_library(
        yuv
        STATIC
        IMPORTED)

set_target_properties(
        yuv
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libyuv.a)

target_link_libraries( # Specifies the target library.
        fyuv
        yuv
        log)