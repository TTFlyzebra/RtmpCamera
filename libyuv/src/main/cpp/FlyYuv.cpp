//
// Created by FlyZebra on 2021/7/3 0003.
//
#include "FlyYuv.h"

#include <cstring>
#include <cmath>
#include <libyuv.h>

void FlyYuv::NV12ToI420(unsigned char *src, unsigned char *dst, int width, int height) {
    libyuv::NV12ToI420(src,
        width,
        src + width * height,
        width,
        dst,
        width,
        dst + width * height,
        (width + 1) / 2,
        dst + width * height + width * height / 4,
        (width + 1) / 2,
        width,
        height);
}

void FlyYuv::NV12ToARGB(unsigned char *src, unsigned char *dst, int width, int height) {
    libyuv::NV12ToARGB(src,
        width,
        src + width * height,
        width,
        dst,
        width * 4,
        width,
        height);
}

void FlyYuv::I420ToNV12(unsigned char *src, unsigned char *dst, int width, int height) {
    libyuv::I420ToNV12(src,
        width,
        src + width * height,
        (width + 1) / 2,
        src + width * height + width * height / 4,
        (width + 1) / 2,
        dst,
        width,
        dst + width * height,
        width,
        width,
        height);
}

void FlyYuv::ARGBToI420(unsigned char *src, unsigned char *dst, int offset,  int width, int height) {
    libyuv::ARGBToI420(src,
        width * 4,
        dst + offset,
        width,
        dst + offset + width * height + width * height / 4,
        (width + 1) / 2,
        dst + offset + width * height,
        (width + 1) / 2,
        width,
        height);
}

void FlyYuv::I420ToARGB(unsigned char *src, unsigned char *dst, int width, int height) {
    libyuv::I420ToARGB(src,
        width,
        src + width * height,
        (width + 1) / 2,
        src + width * height + width * height / 4,
        (width + 1) / 2,
        dst,
        width * 4,
        width,
        height);
}

void FlyYuv::RGB24ToI420(unsigned char *src, unsigned char *dst, int offset, int width, int height) {
    libyuv::RGB24ToI420(src,
        width * 3,
        dst + offset,
        width,
        dst + offset + width * height + width * height / 4,
        (width + 1) / 2,
        dst + offset + width * height,
        (width + 1) / 2,
        width,
        height);
}

void FlyYuv::I422ToI420(unsigned char *src, unsigned char *dst, int width, int height) {
    libyuv::I422ToI420(src,
        width,
        src + width * height,
        width,
        src + width * height + width * height / 2,
        width,
        dst,
        width,
        dst + width * height,
        (width + 1) / 2,
        dst + width * height + width * height / 4,
        (width + 1) / 2,
        width,
        height);
}

void FlyYuv::I420AddMark(unsigned char *i420, const unsigned char *water, int width, int height) {
    for (int h = 0; h < height; h = h + 2) {
        for (int w = 0; w < width; w = w + 2) {
            int y = width * h + w;
            int n = y / width / 2 * width / 2 + y % width / 2;
            int u = width * height + n;
            int v = width * height + width * height / 4 + n;
            if (water[u] != 0x80 && water[v] != 0x80) {
                i420[y] = water[y];
                i420[y + 1] = water[y + 1];
                i420[y + width] = water[y + width];
                i420[y + width + 1] = water[y + width + 1];
                i420[u] = water[u];
                i420[v] = water[v];
            }
        }
    }
}

//RGB转换
//Y=0.299R+0.587G+0.114B
//U=−0.169R−0.331G+0.500B+128
//V=0.500R−0.419G−0.081B+128
//RGB{255,  0,  0} = YUV{76,  85,  255} 0x4C, 0x55, 0xFF//红色
//RGB{  0,255,  0} = YUV{150, 21,   44} 0x96, 0x15, 0x2C//绿色
//RGB{  0,  0,255} = YUV{29,  157, 107} 0x1D, 0x9D, 0x68//蓝色
//透明度处理
//y = ( y0 * a + y1 * ( 255 - a ) ) / 255
//u = ( u0 * a + u1 * ( 255 - a ) ) / 255
//v = ( v0 * a + v1 * ( 255 - a ) ) / 255
void
FlyYuv::I420Compose(unsigned char *i420, unsigned char *back, const unsigned char *water, int width,
                    int height,
                    const unsigned char *mapFilter) {
    const unsigned char *mp[3] = {i420, back, water};
    for (int h = 0; h < height; h = h + 2) {
        for (int w = 0; w < width; w = w + 2) {
            int y = width * h + w;
            int n = y / width / 2 * width / 2 + y % width / 2;
            int u = width * height + n;
            int v = width * height + width * height / 4 + n;
            //  water mark
            if (water[u] != 0x80 && water[v] != 0x80) {
                memcpy(i420 + y, water + y, 2);
                memcpy(i420 + y + width, water + y + width, 2);
                i420[u] = water[u];
                i420[v] = water[v];
            }
                //Transparent
            else {
                auto *temp = mp[mapFilter[i420[y]] & mapFilter[256 + i420[u]] &
                                mapFilter[512 + i420[v]]];
                memcpy(i420 + y, temp + y, 2);
                memcpy(i420 + y + width, temp + y + width, 2);
                i420[u] = temp[u];
                i420[v] = temp[v];

            }
//            int sum = 0;
//            if ((abs(i420[y] - back[y]) < 0x04)) {
//                sum++;
//            }
//            if ((abs(i420[y + 1] - back[y + 1]) < 0x04)) {
//                sum++;
//            }
//            if ((abs(i420[y + width] - back[y + width]) < 0x04)) {
//                sum++;
//            }
//            if ((abs(i420[y + width + 1] - back[y + width + 1]) < 0x04)) {
//                sum++;
//            }
//            if (sum > 1) {
//                memset(i420 + y, 0x00, 2);
//                memset(i420 + y + width, 0x00, 2);
//                i420[u] = 0x80;
//                i420[v] = 0x80;
//            } else {
////                memset(i420 + y, 0xFF, 2);
////                memset(i420 + y + width, 0xFF, 2);
//            }
        }
    }

}

void FlyYuv::I420Filter(unsigned char *i420, const unsigned char *back, int width, int height,
                        const unsigned char *mapFilter) {
    const unsigned char *mp[2] = {i420, back};
    for (int h = 0; h < height; h = h + 2) {
        for (int w = 0; w < width; w = w + 2) {
            int y = width * h + w;
            int n = y / width / 2 * width / 2 + y % width / 2;
            int u = width * height + n;
            int v = width * height + width * height / 4 + n;
            auto *temp = mp[mapFilter[i420[y]] & mapFilter[256 + i420[u]] &
                            mapFilter[512 + i420[v]]];
            memcpy(i420 + y, temp + y, 2);
            memcpy(i420 + y + width, temp + y + width, 2);
            i420[u] = temp[u];
            i420[v] = temp[v];
        }
    }

}
