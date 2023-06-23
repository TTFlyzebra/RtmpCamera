package com.flyzebra.rtmp;

import android.media.MediaCodec;
import android.media.MediaFormat;

import com.flyzebra.utils.ByteUtil;
import com.flyzebra.utils.FlyLog;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Author FlyZebra
 * 2019/6/26 15:15
 * Describ:
 **/
public class FlvRtmpClient {

    public static final int VIDEO_WIDTH = 1280;
    public static final int VIDEO_HEIGHT = 720;
    public static final int VIDEO_BITRATE = 2000000; // 500Kbps
    public static final int VIDEO_IFRAME_INTERVAL = 5; // 2 seconds between I-frames
    public static final int VIDEO_FPS = 24;
    public static final int AAC_SAMPLE_RATE = 16000;
    public static final int AAC_BITRATE = 32 * 1000 * 4;

    public static final int FLV_RTMP_PACKET_TYPE_VIDEO = 9;
    public static final int FLV_RTMP_PACKET_TYPE_AUDIO = 8;
    public static final int FLV_RTMP_PACKET_TYPE_INFO = 18;

    public static final int FLV_TAG_LENGTH = 11;
    public static final int FLV_VIDEO_TAG_LENGTH = 5;
    public static final int FLV_AUDIO_TAG_LENGTH = 2;
    public static final int FLV_TAG_FOOTER_LENGTH = 4;
    public static final int NALU_HEADER_LENGTH = 4;

    private static final Object lock = new Object();
    private AtomicLong jniRtmpPointer = new AtomicLong(-1);

    public static FlvRtmpClient getInstance() {
        return FlvRtmpClient.RtmpSendTaskHolder.sInstance;
    }

    private static class RtmpSendTaskHolder {
        public static final FlvRtmpClient sInstance = new FlvRtmpClient();
    }

    private void sendData(byte[] data, int type, int ts) {
        int ret = 0;
        synchronized (lock) {
            if (jniRtmpPointer.get() != -1) {
//                ret = RtmpDump.write(jniRtmpPointer.get(), data, data.length, type, ts);
            }
        }
        if (data[0] == (byte) 0x17) {
            FlyLog.d("rtmp send:%s[%d][ok]", ByteUtil.bytes2HexString(data, 16), data.length);
        }
        if (ret != 0) {
            FlyLog.e("rtmp send:%s[%d][errer]", ByteUtil.bytes2HexString(data, 16), data.length);
        }else{
            //FlyLog.d("rtmp send:%s[%d]", ByteUtil.bytes2String(data, 16), data.length);
        }
    }

    public void open(final String url) {
        synchronized (lock) {
            if (jniRtmpPointer.get() == -1) {
            }
        }
        //sendMetaData();
    }

    public void sendMetaData() {
        if (jniRtmpPointer.get() == -1) return;
        FLvMetaData fLvMetaData = new FLvMetaData(AAC_BITRATE, AAC_SAMPLE_RATE, VIDEO_WIDTH, VIDEO_HEIGHT, VIDEO_FPS);
        byte[] metaData = fLvMetaData.getMetaData();
        sendData(metaData, FLV_RTMP_PACKET_TYPE_INFO, 0);
    }

    public void sendVideoSpsPps(MediaFormat mediaFormat) {
        if (jniRtmpPointer.get() == -1) return;
        ByteBuffer SPSByteBuff = mediaFormat.getByteBuffer("csd-0");
        SPSByteBuff.position(4);
        ByteBuffer PPSByteBuff = mediaFormat.getByteBuffer("csd-1");
        PPSByteBuff.position(4);
        int spslength = SPSByteBuff.remaining();
        int ppslength = PPSByteBuff.remaining();
        int packetLen = FLV_VIDEO_TAG_LENGTH + 11 + spslength + ppslength;
        final byte[] sps_pps = new byte[packetLen];
        sps_pps[0] = 0x17;
        sps_pps[1] = 0x00;
        sps_pps[2] = 0x00;
        sps_pps[3] = 0x00;
        sps_pps[4] = 0x00;
        SPSByteBuff.get(sps_pps, 13, spslength);
        PPSByteBuff.get(sps_pps, 13 + spslength + 3, ppslength);
        sps_pps[5] = 0x01;
        sps_pps[6] = sps_pps[14];
        sps_pps[7] = sps_pps[15];
        sps_pps[8] = sps_pps[16];
        sps_pps[9] = (byte) 0xFF;
        sps_pps[10] = (byte) 0xE1;
        sps_pps[11] = (byte) ((spslength >> 8) & 0xFF);
        sps_pps[12] = (byte) ((spslength) & 0xFF);
        int pos = 13 + spslength;
        sps_pps[pos] = (byte) 0x01;
        sps_pps[pos + 1] = (byte) ((ppslength >> 8) & 0xFF);
        sps_pps[pos + 2] = (byte) ((ppslength) & 0xFF);
        sendData(sps_pps, FLV_RTMP_PACKET_TYPE_VIDEO, 0);
    }

    public void sendVideoFrame(ByteBuffer outputBuffer, MediaCodec.BufferInfo mBufferInfo, final int ts) {
        if (jniRtmpPointer.get() == -1) return;
        outputBuffer.mark();
        byte type = outputBuffer.get(4);
        int frameType = type & 0x1F;
        outputBuffer.reset();
        outputBuffer.mark();
        outputBuffer.position(mBufferInfo.offset + 4);
        int realDataLength = outputBuffer.remaining();
        int packetLen = FLV_VIDEO_TAG_LENGTH + NALU_HEADER_LENGTH + realDataLength;
        byte[] frame = new byte[packetLen];
        outputBuffer.get(frame, FLV_VIDEO_TAG_LENGTH + NALU_HEADER_LENGTH, realDataLength);
        frame[0] = (frameType == 5) ? (byte) 0x17 : (byte) 0x27;
        frame[1] = 0x01;
        frame[2] = 0x00;
        frame[3] = 0x00;
        frame[4] = 0x00;
        frame[5] = (byte) ((realDataLength >> 24) & 0xFF);
        frame[6] = (byte) ((realDataLength >> 16) & 0xFF);
        frame[7] = (byte) ((realDataLength >> 8) & 0xFF);
        frame[8] = (byte) ((realDataLength) & 0xFF);
        outputBuffer.reset();
        sendData(frame, FLV_RTMP_PACKET_TYPE_VIDEO, ts);
    }

    public void sendAudioHeader(MediaFormat format) {
        if (jniRtmpPointer.get() == -1) return;
        ByteBuffer realData = format.getByteBuffer("csd-0");
        int packetLen = FLV_AUDIO_TAG_LENGTH + realData.remaining();
        byte[] sps = new byte[packetLen];
        realData.get(sps, FLV_AUDIO_TAG_LENGTH, realData.remaining());
        sps[0] = (byte) 0xAE;
        sps[1] = (byte) 0x00;
        sendData(sps, FLV_RTMP_PACKET_TYPE_AUDIO, 0);
    }

    public void sendAudioFrame(ByteBuffer realData, final int ts) {
        if (jniRtmpPointer.get() == -1) return;
        //获取帧类型
        int packetLen = FLV_AUDIO_TAG_LENGTH + realData.remaining();
        byte[] frame = new byte[packetLen];
        realData.get(frame, FLV_AUDIO_TAG_LENGTH, realData.remaining());
        frame[0] = (byte) 0xAE;
        frame[1] = (byte) 0x01;
        sendData(frame, FLV_RTMP_PACKET_TYPE_AUDIO, ts);
    }

    public void close() {
        synchronized (lock) {
            if (jniRtmpPointer.get() == -1) return;
//            RtmpDump.close(jniRtmpPointer.get());
            jniRtmpPointer.set(-1);
        }
    }

}
