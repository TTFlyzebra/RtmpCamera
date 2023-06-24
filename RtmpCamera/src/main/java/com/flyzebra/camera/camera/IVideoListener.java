package com.flyzebra.camera.camera;

public interface IVideoListener {
    void notifyRGBFrame(byte[] data, int size, int width, int heigth);
}
