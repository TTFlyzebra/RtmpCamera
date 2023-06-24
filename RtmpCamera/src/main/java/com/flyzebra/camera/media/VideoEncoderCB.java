package com.flyzebra.camera.media;

public interface VideoEncoderCB {
   /**
    * H264 sps-pps
    * @param sps
    * @param spsLen
    * @param pps
    * @param ppsLen
    */
   void notifySpsPps(byte[] sps, int spsLen, byte[] pps, int ppsLen);

   /**
    * H265 vps-sps-pps
    * @param vsp
    * @param vspLen
    */
   void notifyVpsSpsPps(byte[] vsp, int vspLen);

   /**
    * h264
    * @param data
    * @param size
    * @param pts
    */
   void notifyAvcData(byte[] data, int size, long pts);

   /**
    * h265
    * @param data
    * @param size
    * @param pts
    */
   void notifyHevcData(byte[] data, int size, long pts);
}
