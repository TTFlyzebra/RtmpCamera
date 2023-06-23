package com.flyzebra.camera.media;

public interface VideoEncoderCB {
   /**
    * H264 sps-pps
    * @param sps
    * @param spsLen
    * @param pps
    * @param ppsLen
    */
   void notifyAvcSpsPps(byte[] sps, int spsLen, byte[] pps, int ppsLen);

   /**
    * H265 vps-sps-pps
    * @param vsp
    * @param vspLen
    */
   void notifyAvcVpsSpsPps(byte[] vsp, int vspLen);

   /**
    * h264/h265
    * @param data
    * @param size
    * @param pts
    */
   void notifyVideoData(byte[] data, int size, long pts);
}
