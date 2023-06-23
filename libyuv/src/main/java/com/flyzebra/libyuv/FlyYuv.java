package com.flyzebra.libyuv;

/**
 * Author FlyZebra
 * 2020/11/21 17:20
 * Describ:
 **/
public class FlyYuv {

    static {
        System.loadLibrary("fyuv");
    }

    static public native void NV12ToI420(byte[] src, byte[] dst, int width, int height);

    static public native void NV12ToARGB(byte[] src, byte[] dst, int width, int height);

    static public native void I420ToNV12(byte[] src, byte[] dst, int width, int height);

    static public native void ARGBToI420(byte[] src, byte[] dst, int offset, int width, int height);

    static public native void I420ToARGB(byte[] src, byte[] dst, int width, int height);

    static public native void RGB24ToI420(byte[] src, byte[] dst, int offset, int width, int height);

    static public native void I422ToI420(byte[] src, byte[] dst, int width, int height);

    static public native void I420AddMark(byte[] i420, byte[] mark, int width, int height);

    static public native void I420Compose(byte[] i420, byte[] back, byte[] water, int width, int height, byte[] mapFilter);

    static public native void I420Filter(byte[] i420, byte[] back, int width, int height, byte[] mapFilter);

    static public native void FillFilter(byte[] mapFilter,int size1, byte[] yuvFilter, int size2);

}
