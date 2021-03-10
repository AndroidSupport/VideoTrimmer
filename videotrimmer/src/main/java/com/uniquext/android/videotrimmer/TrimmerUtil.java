package com.uniquext.android.videotrimmer;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 　 　　   へ　　　 　／|
 * 　　    /＼7　　　 ∠＿/
 * 　     /　│　　 ／　／
 * 　    │　Z ＿,＜　／　　   /`ヽ
 * 　    │　　　 　　ヽ　    /　　〉
 * 　     Y　　　　　   `　  /　　/
 * 　    ｲ●　､　●　　⊂⊃〈　　/
 * 　    ()　 へ　　　　|　＼〈
 * 　　    >ｰ ､_　 ィ　 │ ／／      去吧！
 * 　     / へ　　 /　ﾉ＜| ＼＼        比卡丘~
 * 　     ヽ_ﾉ　　(_／　 │／／           消灭代码BUG
 * 　　    7　　　　　　　|／
 * 　　    ＞―r￣￣`ｰ―＿
 * ━━━━━━━━━━感觉萌萌哒━━━━━━━━━━
 *
 * @author uniquext
 * @description $
 * @date 2021-03-10  16:34
 */
public class TrimmerUtil {

    /**
     * 只支持mp4
     *
     * @param inputPath
     * @param outputPath
     * @param startTime
     * @param endTime
     * @throws IOException
     */
    public static void trim(String inputPath, String outputPath, float startTime, float endTime) throws IOException {
        long startTimeUs = (long) (startTime * 1000L);
        long endTimeUs = (long) (endTime * 1000L);

        MediaMuxer mediaMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        MediaExtractor mediaExtractor = new MediaExtractor();
        mediaExtractor.setDataSource(inputPath);

        Log.e("####", "trim");

        for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {
            MediaFormat currentMediaFormat = mediaExtractor.getTrackFormat(i);
            Log.e("####", "KEY_MIME " + currentMediaFormat.getString(MediaFormat.KEY_MIME));
            if (currentMediaFormat.getString(MediaFormat.KEY_MIME).startsWith("video/")) {

                int sourceVideoTrack = i;
                MediaFormat videoFormat = mediaExtractor.getTrackFormat(sourceVideoTrack);
                int videoTrackIndex = mediaMuxer.addTrack(videoFormat);

                int rotation = videoFormat.getInteger(MediaFormat.KEY_ROTATION);
                mediaMuxer.setOrientationHint(rotation);

                int videoMaxInputSize = videoFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
                ByteBuffer inputBuffer = ByteBuffer.allocate(videoMaxInputSize);

                MediaCodec.BufferInfo videoInfo = new MediaCodec.BufferInfo();

                mediaExtractor.selectTrack(sourceVideoTrack);
                mediaExtractor.seekTo(startTimeUs, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);


                Log.e("####", "start");
                mediaMuxer.start();
                while (true) {
                    int sampleSize = mediaExtractor.readSampleData(inputBuffer, 0);
                    long presentationTimeUs = mediaExtractor.getSampleTime();
                    if (sampleSize < 0 || presentationTimeUs > endTimeUs) {
                        mediaExtractor.unselectTrack(sourceVideoTrack);
                        break;
                    }

                    videoInfo.offset = 0;
                    videoInfo.size = sampleSize;
                    videoInfo.flags = mediaExtractor.getSampleFlags();
                    videoInfo.presentationTimeUs = presentationTimeUs;
                    Log.e("####", "presentationTimeUs " + presentationTimeUs);

                    mediaMuxer.writeSampleData(videoTrackIndex, inputBuffer, videoInfo);
                    mediaExtractor.advance();
                }


            } else if (currentMediaFormat.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {




            }
        }

        mediaMuxer.stop();
        mediaMuxer.release();
        mediaExtractor.release();
        mediaExtractor = null;
        Log.e("####", "end");

//        MediaFormat audioFormat = mediaExtractor.getTrackFormat(sourceAudioTrack);
    }


//    private void

}
