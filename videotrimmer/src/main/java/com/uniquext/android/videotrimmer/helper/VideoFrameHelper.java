package com.uniquext.android.videotrimmer.helper;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;

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
 * @date 2021-03-09  14:46
 */
public class VideoFrameHelper {

    private MediaMetadataRetriever retriever;
    @Size(2)
    private int[] targetSize;
    private OnFrameBitmapListener listener;

    public VideoFrameHelper(int[] targetSize, @NonNull OnFrameBitmapListener listener) {
        this.retriever = new MediaMetadataRetriever();
        this.targetSize = targetSize;
        this.listener = listener;
    }


    public void getFrames(String videoPath) {
        Observable.just(videoPath)
                .map(new Function<String, Long>() {
                    @Override
                    public Long apply(String s) throws Exception {
                        retriever.setDataSource(s);
                        return Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                    }
                })
                .flatMap(new Function<Long, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(Long aLong) throws Exception {
                        return Observable.rangeLong(0, aLong);
                    }
                })
                .filter(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        return aLong % 1000 == 0;
                    }
                })
                .map(new Function<Long, Bitmap>() {
                    @Override
                    public Bitmap apply(Long aLong) throws Exception {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                            return retriever.getScaledFrameAtTime(aLong * 1000, MediaMetadataRetriever.OPTION_CLOSEST, targetSize[0], targetSize[1]);
                        } else {
                            return retriever.getFrameAtTime(aLong * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResourceObserver<Bitmap>() {

                    @Override
                    protected void onStart() {
                        listener.onFrameObtainStart();
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        listener.onFrameBitmap(bitmap);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        listener.onFrameObtainComplete();
                    }
                });

    }

    @Size(2)
    private int[] getVideoSizeForUrl(@Size(2) int[] dstSize) {
        int[] size = new int[]{
                Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)),
                Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
        };
        float ratio = Math.min(dstSize[0] / (1f * size[0]), dstSize[1] / (1f * size[1]));
        return new int[]{(int) (size[0] * ratio), (int) (size[1] * ratio)};
    }

    public interface OnFrameBitmapListener {
        void onFrameObtainStart();

        void onFrameBitmap(Bitmap bitmap);

        void onFrameObtainComplete();
    }


}
