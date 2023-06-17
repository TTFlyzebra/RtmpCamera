
cmake_minimum_required(VERSION 3.10.2)

include_directories(src/main/cpp/include)

add_library(
        flylibyuv
        SHARED
        src/main/cpp/flylibyuv.cpp)

add_library(libyuv
        STATIC
        IMPORTED)

set_target_properties(
        libyuv
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/cpp/libs/${ANDROID_ABI}/libyuv.a)

target_link_libraries( # Specifies the target library.
        flylibyuv
        libyuv
        android
        log
        )