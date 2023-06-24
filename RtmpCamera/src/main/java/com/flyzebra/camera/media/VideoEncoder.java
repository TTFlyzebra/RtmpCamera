/**
 * FileName: VideoEncoder
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2023/6/23 15:06
 * Description:
 */
package com.flyzebra.camera.media;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import com.flyzebra.utils.FlyLog;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class VideoEncoder implements Runnable {
    private MediaCodec codec = null;
    private final Object codecLock = new Object();
    private final AtomicBoolean is_codec_init = new AtomicBoolean(false);
    private Thread mOutThread = null;
    private VideoEncoderCB mCallBack;

    public VideoEncoder(VideoEncoderCB cb) {
        mCallBack = cb;
    }

    @Override
    public void run() {
        MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
        while (is_codec_init.get()) {
            int outputIndex = codec.dequeueOutputBuffer(mBufferInfo, 200000);
            switch (outputIndex) {
                case MediaCodec.INFO_TRY_AGAIN_LATER:
                    break;
                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                    try {
                        MediaFormat format = codec.getOutputFormat();
                        String mini = format.getString(MediaFormat.KEY_MIME);
                        if (MediaFormat.MIMETYPE_VIDEO_AVC.equals(mini)) {
                            ByteBuffer spsBuffer = format.getByteBuffer("csd-0");
                            spsBuffer.position(0);
                            int spsLen = spsBuffer.remaining();
                            byte[] sps = new byte[spsLen];
                            spsBuffer.get(sps, 0, spsLen);
                            ByteBuffer ppsBuffer = format.getByteBuffer("csd-1");
                            ppsBuffer.position(0);
                            int ppsLen = ppsBuffer.remaining();
                            byte[] pps = new byte[ppsLen];
                            ppsBuffer.get(pps, 0, ppsLen);
                            mCallBack.notifyAvcSpsPps(sps, spsLen, pps, ppsLen);
                        } else {
                            ByteBuffer bufer = format.getByteBuffer("csd-0");
                            bufer.position(0);
                            int vspLen = bufer.remaining();
                            byte[] vsp = new byte[vspLen];
                            bufer.get(vsp, 0, vspLen);
                            mCallBack.notifyAvcVpsSpsPps(vsp, vspLen);
                        }
                    } catch (Exception e) {
                        FlyLog.e(e.toString());
                    }
                    break;
                case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                    break;
                default:
                    if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) == 0 && mBufferInfo.size != 0) {
                        ByteBuffer outputBuffer = codec.getOutputBuffer(outputIndex);
                        outputBuffer.position(mBufferInfo.offset + 4);
                        int size = outputBuffer.remaining();
                        byte[] data = new byte[size];
                        outputBuffer.get(data, 0, size);
                        mCallBack.notifyVideoData(data, size, mBufferInfo.presentationTimeUs);
                    }
                    codec.releaseOutputBuffer(outputIndex, false);
                    break;
            }
        }
    }

    public void initCodec(String mimeType, int width, int height, int level) {
        synchronized (codecLock) {
            try {
                MediaFormat format = MediaFormat.createVideoFormat(mimeType, width, height);
                format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
                format.setInteger(MediaFormat.KEY_BIT_RATE, 2000000);
                format.setInteger(MediaFormat.KEY_FRAME_RATE, 25);
                format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 3);
                format.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);
                codec = MediaCodec.createEncoderByType(mimeType);
                codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                codec.start();
                is_codec_init.set(true);
            } catch (Exception e) {
                FlyLog.e(e.toString());
            }
        }
        mOutThread = new Thread(this);
        mOutThread.start();
    }

    public void inYuvData(byte[] data, int size, long pts) {
        synchronized (codecLock) {
            if (!is_codec_init.get() || data == null || size <= 0) return;
            int inIndex = codec.dequeueInputBuffer(200000);
            if (inIndex < 0) {
                FlyLog.e("VideoEncoder codec->dequeueInputBuffer inIdex=%zu error!", inIndex);
                return;
            }

            ByteBuffer buffer = codec.getInputBuffer(inIndex);
            if (buffer == null) {
                FlyLog.e("VideoEncoder codec->getInputBuffer inIdex=%zu error!", inIndex);
                return;
            }
            buffer.put(data, 0, size);
            codec.queueInputBuffer(inIndex, 0, size, pts, 0);
        }
    }

    public void releaseCodec() {
        synchronized (codecLock) {
            is_codec_init.set(false);
            try {
                mOutThread.join();
            } catch (InterruptedException e) {
                FlyLog.e(e.toString());
            }
            codec.stop();
            codec.release();
            codec = null;
        }
    }

}
