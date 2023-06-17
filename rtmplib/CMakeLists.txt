cmake_minimum_required(VERSION 3.10.2)

include_directories(src/main/cpp/include)

add_library(
        zebra_rtmp
        SHARED
        "src/main/cpp/rtmp/libresrtmp.c"
        )

add_library(
        rtmp
        SHARED
        IMPORTED
)

set_target_properties(
        rtmp
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/cpp/libs/${ANDROID_ABI}/librtmp.so
)

find_library(
        log-lib
        log)

target_link_libraries(
        zebra_rtmp
        rtmp
        z
        ${log-lib}
        )
