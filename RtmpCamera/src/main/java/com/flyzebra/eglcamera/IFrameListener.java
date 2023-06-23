package com.flyzebra.eglcamera;

public interface IFrameListener {
    public void notifyRGBFrame(byte[] data, int size, int width, int heigth);
}
