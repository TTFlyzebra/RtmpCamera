//
// Created by FlyZebra on 2021/7/3 0003.
//

#ifndef TRANSCODE_FLYYUV_H
#define TRANSCODE_FLYYUV_H

class FlyYuv {
public:
    static void NV12ToI420(unsigned char *src, unsigned char *dst, int width, int heigh);

    static void NV12ToARGB(unsigned char *src, unsigned char *dst, int width, int height);

    static void I420ToNV12(unsigned char *src, unsigned char *dst, int width, int height);

    static void ARGBToI420(unsigned char *src, unsigned char *dst, int offset, int width, int height);

    static void I420ToARGB(unsigned char *src, unsigned char *dst, int width, int height);

    static void RGB24ToI420(unsigned char *src, unsigned char *dst, int offset, int width, int height);

    static void I422ToI420(unsigned char *src, unsigned char *dst, int width, int height);

    static void I420AddMark(unsigned char *i420, const unsigned char *mark, int width, int height);

    static void I420Compose(unsigned char *i420, unsigned char *back, const unsigned char *water, int width, int height, const unsigned char *mapFilter);

    static void I420Filter(unsigned char *i420, const unsigned char *back, int width, int height, const unsigned char *mapFilter);

};


#endif //TRANSCODE_FLYYUV_H
