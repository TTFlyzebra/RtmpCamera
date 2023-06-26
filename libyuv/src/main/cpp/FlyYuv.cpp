//
// Created by FlyZebra on 2021/7/3 0003.
//
#include "FlyYuv.h"

#include <cstring>
#include <cmath>
#include <libyuv.h>

void
FlyYuv::NV12ToI420(unsigned char *src, unsigned char *dst, int dst_offset, int width, int height) {
    libyuv::NV12ToI420(src,
                       width,
                       src + width * height,
                       width,
                       dst + dst_offset,
                       width,
                       dst + dst_offset + width * height,
                       (width + 1) / 2,
                       dst + dst_offset + width * height + width * height / 4,
                       (width + 1) / 2,
                       width,
                       height);
}

void
FlyYuv::NV12ToARGB(unsigned char *src, unsigned char *dst, int dst_offset, int width, int height) {
    libyuv::NV12ToARGB(src,
                       width,
                       src + width * height,
                       width,
                       dst + dst_offset,
                       width * 4,
                       width,
                       height);
}

void
FlyYuv::I420ToNV12(unsigned char *src, unsigned char *dst, int dst_offset, int width, int height) {
    libyuv::I420ToNV12(src,
                       width,
                       src + width * height,
                       (width + 1) / 2,
                       src + width * height + width * height / 4,
                       (width + 1) / 2,
                       dst + dst_offset,
                       width,
                       dst + dst_offset + width * height,
                       width,
                       width,
                       height);
}

void
FlyYuv::ARGBToI420(unsigned char *src, unsigned char *dst, int dst_offset, int width, int height) {
    libyuv::ARGBToI420(src,
                       width * 4,
                       dst + dst_offset,
                       width,
                       dst + dst_offset + width * height,
                       (width + 1) / 2,
                       dst + dst_offset + width * height + width * height / 4,
                       (width + 1) / 2,
                       width,
                       height);
}

void
FlyYuv::ARGBToNV12(unsigned char *src, unsigned char *dst, int dst_offset, int width, int height) {
    libyuv::ARGBToNV12(src,
                       width * 4,
                       dst + dst_offset,
                       width,
                       dst + dst_offset + width * height,
                       width,
                       width,
                       height);
}

void
FlyYuv::ARGBToNV21(unsigned char *src, unsigned char *dst, int dst_offset, int width, int height) {
    libyuv::ARGBToNV21(src,
                       width * 4,
                       dst + dst_offset,
                       width,
                       dst + dst_offset + width * height,
                       width,
                       width,
                       height);
}

void
FlyYuv::I420ToARGB(unsigned char *src, unsigned char *dst, int dst_offset, int width, int height) {
    libyuv::I420ToARGB(src,
                       width,
                       src + width * height,
                       (width + 1) / 2,
                       src + width * height + width * height / 4,
                       (width + 1) / 2,
                       dst + dst_offset,
                       width * 4,
                       width,
                       height);
}

void
FlyYuv::RGB24ToI420(unsigned char *src, unsigned char *dst, int dst_offset, int width, int height) {
    libyuv::RGB24ToI420(src,
                        width * 3,
                        dst + dst_offset,
                        width,
                        dst + dst_offset + width * height,
                        (width + 1) / 2,
                        dst + dst_offset + width * height + width * height / 4,
                        (width + 1) / 2,
                        width,
                        height);
}

void
FlyYuv::I422ToI420(unsigned char *src, unsigned char *dst, int dst_offset, int width, int height) {
    libyuv::I422ToI420(src,
                       width,
                       src + width * height,
                       width,
                       src + width * height + width * height / 2,
                       width,
                       dst + dst_offset,
                       width,
                       dst + dst_offset + width * height,
                       (width + 1) / 2,
                       dst + dst_offset + width * height + width * height / 4,
                       (width + 1) / 2,
                       width,
                       height);
}

void
FlyYuv::I420Rotate(unsigned char *src, unsigned char *dst, int dst_offset, int width, int height) {
    libyuv::I420Rotate(src,
                       width,
                       src + width * height,
                       (width + 1) / 2,
                       src + width * height + width * height / 4,
                       (width + 1) / 2,
                       dst + dst_offset,
                       width,
                       dst + dst_offset + width * height,
                       (width + 1) / 2,
                       dst + dst_offset + width * height + width * height / 4,
                       (width + 1) / 2,
                       width,
                       height,
                       libyuv::kRotate90);
}

