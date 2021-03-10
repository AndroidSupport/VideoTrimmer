package com.uniquext.android.application;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import androidx.annotation.Size;

import java.util.ArrayList;
import java.util.List;


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
 * @date 2021-03-08  19:16
 */
public class TestUtil {

    public static void test() {
//        Observable.just()
    }

    public static List<Bitmap> getVideoThumbFile(String path, @Size(2) int[] size) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);


        Log.e("#### size ", size[0] + " # " + size[1]);
        int[] targetSize = getVideoSizeForUrl(media, size);
        Log.e("#### targetSize ", targetSize[0] + " # " + targetSize[1]);

        List<Bitmap> bitmaps = new ArrayList<>();
        long duration = Long.parseLong(media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
//        long frameCount = Long.parseLong(media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT));
        Log.e("#### frameCount", media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT));

        long total = 0;
        for (int i = 0; i < duration; i += 1000) {
            Bitmap bitmap = media.getScaledFrameAtTime(i * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC, targetSize[0], targetSize[1]);
            bitmaps.add(bitmap);
            total += bitmap.getByteCount();
        }
        media.release();

        Log.e("#### duration ", duration + " # " + bitmaps.size() + " # " + total);

        return bitmaps;

    }

    @Size(2)
    public static int[] getVideoSizeForUrl(MediaMetadataRetriever retriever, @Size(2) int[] dstSize) {
        int[] size = new int[]{
                Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)),
                Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
        };
        Log.e("#### video ", size[0] + " # " + size[1]);
        float ratio = Math.min(dstSize[0] / (1f * size[0]), dstSize[1] / (1f * size[1]));
        return new int[]{(int) (size[0] * ratio), (int) (size[1] * ratio)};
    }

}
