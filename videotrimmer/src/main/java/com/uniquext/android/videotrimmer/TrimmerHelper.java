package com.uniquext.android.videotrimmer;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.io.File;
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
public class TrimmerHelper {

    /**
     * 只支持mp4
     *
     * @param source
     * @param outputPath
     * @param startTime
     * @param endTime
     * @throws IOException
     */
    public static void trim(File source, String outputPath, float startTime, float endTime) throws IOException {
        long startTimeUs = (long) (startTime * 1000L);
        long endTimeUs = (long) (endTime * 1000L);

        MediaMuxer mediaMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        MediaExtractor mediaExtractor = new MediaExtractor();
        mediaExtractor.setDataSource(source.getAbsolutePath());

        int[] mediaExtractorTracks = new int[2];
        int[] mediaMuxerTracks = new int[2];
        for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {
            MediaFormat currentMediaFormat = mediaExtractor.getTrackFormat(i);
            if (currentMediaFormat.getString(MediaFormat.KEY_MIME).startsWith("video/")) {
                mediaExtractorTracks[0] = i;
                mediaMuxerTracks[0] = mediaMuxer.addTrack(mediaExtractor.getTrackFormat(mediaExtractorTracks[0]));
            } else if (currentMediaFormat.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                mediaExtractorTracks[1] = i;
                mediaMuxerTracks[1] = mediaMuxer.addTrack(mediaExtractor.getTrackFormat(mediaExtractorTracks[1]));
            }
        }

        initOrientation(mediaMuxer, mediaExtractor, mediaExtractorTracks[0]);
        mediaMuxer.start();
        ByteBuffer inputBuffer = ByteBuffer.allocate((int) source.length());
        for (int i = 0; i < mediaExtractorTracks.length; i++) {
            writeSampleData(mediaMuxer, mediaExtractor, inputBuffer, startTimeUs, endTimeUs, mediaExtractorTracks[i], mediaMuxerTracks[i]);
        }
        mediaMuxer.stop();
        mediaMuxer.release();
        mediaExtractor.release();

    }

    private static void initOrientation(MediaMuxer mediaMuxer, MediaExtractor mediaExtractor, int videoTrack) {
        MediaFormat videoFormat = mediaExtractor.getTrackFormat(videoTrack);
        mediaMuxer.setOrientationHint(videoFormat.getInteger(MediaFormat.KEY_ROTATION));
    }

    private static void writeSampleData(MediaMuxer mediaMuxer, MediaExtractor mediaExtractor, ByteBuffer inputBuffer, long startTimeUs, long endTimeUs, int mediaExtractorTrack, int mediaMuxerTrack) {

        mediaExtractor.selectTrack(mediaExtractorTrack);
        mediaExtractor.seekTo(startTimeUs, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
        MediaCodec.BufferInfo videoInfo = new MediaCodec.BufferInfo();

        while (true) {
            int sampleSize = mediaExtractor.readSampleData(inputBuffer, 0);
            long presentationTimeUs = mediaExtractor.getSampleTime();
            if (sampleSize < 0 || presentationTimeUs > endTimeUs) {
                mediaExtractor.unselectTrack(mediaExtractorTrack);
                break;
            }
            videoInfo.offset = 0;
            videoInfo.size = sampleSize;
            videoInfo.flags = mediaExtractor.getSampleFlags();
            videoInfo.presentationTimeUs = presentationTimeUs;
            Log.e("####", "presentationTimeUs " + presentationTimeUs);

            mediaMuxer.writeSampleData(mediaMuxerTrack, inputBuffer, videoInfo);
            mediaExtractor.advance();

        }
    }
}
