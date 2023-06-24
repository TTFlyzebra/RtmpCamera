package com.flyzebra.camera.media;

public interface AudioEncoderCB {

    void notifyAacHead(byte[] head, int size);

    void notifyAacData(byte[] data, int size, long pts);
}
