//
// Created by Administrator on 2023/6/21.
//

#include "FFRtmp.h"
#include "utils/FlyLog.h"

extern "C" {
#include <libavformat/avformat.h>
}

FFRtmp::FFRtmp() {

}

FFRtmp::~FFRtmp() {

}

int FFRtmp::openRtmpUrl(char *url) {
    AVFormatContext *octx = nullptr;
    int ret = avformat_alloc_output_context2(&octx, nullptr, "flv", url);
    if (ret < 0) {
        return ret;
    }
    FLOGE("avformat_alloc_output_context2 ret=%d", ret);
    //输出视频流
    const AVCodec *v_codec = avcodec_find_decoder_by_name("h264");
    AVStream *v_outstream = avformat_new_stream(octx, v_codec);
    //输出音频流
    const AVCodec *a_codec = avcodec_find_decoder_by_name("aac");
    AVStream *a_outstream = avformat_new_stream(octx, a_codec);

    av_dump_format(octx, 0, url, 1);
}

void FFRtmp::close() {
    FLOGE("ffmpeg rtmp close!");
}
