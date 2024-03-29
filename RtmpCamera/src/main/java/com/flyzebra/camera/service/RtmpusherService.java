/**
 * FileName: RtmpPushService
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2023/6/23 11:07
 * Description:
 */
package com.flyzebra.camera.service;

import com.flyzebra.camera.Config;
import com.flyzebra.camera.media.AudioEncoder;
import com.flyzebra.camera.media.AudioEncoderCB;
import com.flyzebra.camera.media.VideoEncoder;
import com.flyzebra.camera.media.VideoEncoderCB;
import com.flyzebra.notify.INotify;
import com.flyzebra.notify.Notify;
import com.flyzebra.notify.NotifyType;
import com.flyzebra.rtmp.RtmpDump;
import com.flyzebra.utils.ByteUtil;
import com.flyzebra.utils.FlyLog;

public class RtmpusherService implements VideoEncoderCB, AudioEncoderCB, INotify {
    private VideoEncoder videoEncoder;
    private AudioEncoder audioEncoder;
    private RtmpDump rtmpDump;

    public RtmpusherService() {
        rtmpDump = new RtmpDump();
    }

    public void start(String rtmp_url) {
        rtmpDump.init(rtmp_url);
        videoEncoder = new VideoEncoder(this);
        audioEncoder = new AudioEncoder(this);
        Notify.get().registerListener(this);
    }

    public void stop() {
        Notify.get().unregisterListener(this);
        rtmpDump.release();
        videoEncoder.releaseCodec();
        audioEncoder.releaseCodec();
    }

    @Override
    public void notifySpsPps(byte[] sps, int spsLen, byte[] pps, int ppsLen) {
        if (sps[0] == 0x00 && sps[1] == 0x00 && sps[2] == 0x00 && sps[3] == 0x01) {
            int sps_len = spsLen - 4;
            byte[] _sps = new byte[sps_len];
            System.arraycopy(sps, 4, _sps, 0, sps_len);
            sps = _sps;
            spsLen = sps_len;
        }
        if (pps[0] == 0x00 && pps[1] == 0x00 && pps[2] == 0x00 && pps[3] == 0x01) {
            int pps_len = ppsLen - 4;
            byte[] _pps = new byte[pps_len];
            System.arraycopy(pps, 4, _pps, 0, pps_len);
            pps = _pps;
            ppsLen = pps_len;
        }
        rtmpDump.sendSpsPps(sps, spsLen, pps, ppsLen);
    }

    @Override
    public void notifyVpsSpsPps(byte[] vsp, int vspLen) {
        try {
            int vps_p = -1;
            int sps_p = -1;
            int pps_p = -1;
            for (int i = 0; i < vspLen; i++) {
                if (vsp[i] == 0x00 && vsp[i + 1] == 0x00 && vsp[i + 2] == 0x00 && vsp[i + 3] == 0x01) {
                    if (vps_p == -1) {
                        vps_p = i;
                        i += 3;
                    } else if (sps_p == -1) {
                        sps_p = i;
                        i += 3;
                    } else if (pps_p == -1) {
                        pps_p = i;
                        break;
                    }
                }
            }
            if (vps_p == -1 || sps_p == -1 || pps_p == -1) {
                FlyLog.e("Get vps sps pps error!");
                return;
            }
            int vpsLen = sps_p - 4;
            byte[] vps = new byte[vpsLen];
            System.arraycopy(vsp, 4, vps, 0, vpsLen);
            int spsLen = pps_p - sps_p - 4;
            byte[] sps = new byte[spsLen];
            System.arraycopy(vsp, sps_p + 4, sps, 0, spsLen);
            int ppsLen = vspLen - pps_p - 4;
            byte[] pps = new byte[ppsLen];
            System.arraycopy(vsp, pps_p + 4, pps, 0, ppsLen);
            rtmpDump.sendVpsSpsPps(vps, vpsLen, sps, spsLen, pps, ppsLen);
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
    }

    @Override
    public void notifyAvcData(byte[] data, int size, long pts) {
        rtmpDump.sendAvc(data, size, pts);
    }

    @Override
    public void notifyHevcData(byte[] data, int size, long pts) {
        rtmpDump.sendHevc(data, size, pts);
    }

    @Override
    public void notifyAacHead(byte[] head, int headLen) {
        rtmpDump.sendAacHead(head, headLen);
    }

    @Override
    public void notifyAacData(byte[] data, int size, long pts) {
        rtmpDump.sendAac(data, size, pts);
    }

    @Override
    public void notify(byte[] data, int size) {
    }

    private static final Object codecLocked = new Object();

    @Override
    public void handle(int type, byte[] data, int size, byte[] params) {
        if (NotifyType.NOTI_CAMFIX_YUV == type) {
            synchronized (codecLocked) {
                if (!videoEncoder.isCodecInit()) {
                    int width = ByteUtil.bytes2Short(params, 0, true);
                    int height = ByteUtil.bytes2Short(params, 2, true);
                    videoEncoder.initCodec(Config.CAM_MIME_TYPE, width, height, Config.CAM_BIT_RATE);
                }
            }
            videoEncoder.inYuvData(data, size, System.nanoTime() / 1000);
        } else if (NotifyType.NOTI_MICOUT_PCM == type) {
            synchronized (codecLocked) {
                if (!audioEncoder.isCodecInit()) {
                    int sample = ByteUtil.bytes2Int(params, 0, true);
                    int channels = ByteUtil.bytes2Short(params, 4, true);
                    int bitrate = ByteUtil.bytes2Int(params, 6, true);
                    audioEncoder.initCodec(Config.MIC_MIME_TYPE, sample, channels, bitrate);
                }
            }
            audioEncoder.inPumData(data, size, System.nanoTime() / 1000);
        }
    }
}
