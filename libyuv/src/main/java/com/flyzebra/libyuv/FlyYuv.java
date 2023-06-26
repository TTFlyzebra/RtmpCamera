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

    static public native void NV12ToI420(byte[] src, byte[] dst, int dst_offset, int width, int height);

    static public native void NV12ToARGB(byte[] src, byte[] dst, int dst_offset, int width, int height);

    static public native void I420ToNV12(byte[] src, byte[] dst, int dst_offset, int width, int height);

    static public native void ARGBToI420(byte[] src, byte[] dst, int dst_offset, int width, int height);

    static public native void ARGBToNV12(byte[] src, byte[] dst, int dst_offset, int width, int height);

    static public native void ARGBToNV21(byte[] src, byte[] dst, int dst_offset, int width, int height);

    static public native void I420ToARGB(byte[] src, byte[] dst, int dst_offset, int width, int height);

    static public native void RGB24ToI420(byte[] src, byte[] dst, int dst_offset, int width, int height);

    static public native void I422ToI420(byte[] src, byte[] dst, int dst_offset, int width, int height);

    static public native void I420Rotate(byte[] src, byte[] dst, int dst_offset, int width, int height);

}
