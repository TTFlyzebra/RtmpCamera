package com.flyzebra.libyuv;

/**
 * Author FlyZebra
 * 2020/11/21 17:20
 * Describ:
 **/
public class LibYuvTools {

    static {
        System.loadLibrary("flylibyuv");
    }

    static public native void NV21ToI420(byte[] src, byte[] dst, int widht, int height);

    static public native void NV12ToI420(byte[] src, byte[] dst, int widht, int height);

    static public native void I422ToI420(byte[] src, byte[] dst, int widht, int height);

}
