
cmake_minimum_required(VERSION 3.10.2)

include_directories(src/main/cpp/include)

add_library(
        flylibyuv
        SHARED
        src/main/cpp/FlyLibYUV.cpp
)

#添加libavcodec-57.so
add_library(libyuv
        STATIC
        IMPORTED)
set_target_properties(libyuv
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libavcodec-57.so)

target_link_libraries( # Specifies the target library.
        flylibyuv
        libyuv
        )