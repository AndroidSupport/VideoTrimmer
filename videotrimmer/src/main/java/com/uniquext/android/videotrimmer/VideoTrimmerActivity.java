package com.uniquext.android.videotrimmer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uniquext.android.videotrimmer.adapter.VideoTrimmerAdapter;
import com.uniquext.android.videotrimmer.ui.RangeSeekBar;
import com.uniquext.android.widget.util.ViewMeasure;

import java.io.File;

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
 * @date 2021-03-09  14:36
 */
public class VideoTrimmerActivity extends AppCompatActivity implements VideoFrameHelper.OnFrameBitmapListener , RangeSeekBar.OnRangeSeekBarChangeListener {

    private static final String INTENT_VIDEO_PATH = "INTENT_VIDEO_PATH";

    private VideoView videoView;
    private RecyclerView recyclerView;
    private RangeSeekBar rangeSeekBar;
    private View vIndicator;

    private VideoTrimmerAdapter adapter;
    private VideoFrameHelper trimmerHelper;
    private LinearLayoutManager linearLayoutManager;

    private float startTime = 0L;
    private float endTime = 10 * 1000L;
    private ObjectAnimator mIndicatorAnim;

    public static void startVideoTrimmerActivity(Activity activity, @NonNull String path, int requestCode) {
        Intent intent = new Intent(activity, VideoTrimmerActivity.class);
        intent.putExtra(INTENT_VIDEO_PATH, path);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_trimmer);

        initView();
        initData();
    }

    private void initView() {
        videoView = findViewById(R.id.view_player);
        recyclerView = findViewById(R.id.recycleview);
        rangeSeekBar = findViewById(R.id.range_seek);
        vIndicator = findViewById(R.id.view_indicator);
        linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        int[] screen = new int[2];
        ViewMeasure.getScreenSize(getWindowManager(), screen);
        float itemWidth = (screen[0] - recyclerView.getPaddingStart() - recyclerView.getPaddingEnd()) * 0.1f;
        adapter = new VideoTrimmerAdapter((int) itemWidth);
        recyclerView.setAdapter(adapter);

        rangeSeekBar.setOnRangeSeekBarChangeListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    videoPlay();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                changeVideoRange(rangeSeekBar.getStartOffset(), rangeSeekBar.getRangeLength());
            }
        });
    }

    private void initData() {
        final String videoPath = getIntent().getStringExtra(INTENT_VIDEO_PATH);
        if (TextUtils.isEmpty(videoPath)) {
            onBackPressed();
        } else {
            videoView.setVideoURI(Uri.fromFile(new File(videoPath)));
            trimmerHelper = new VideoFrameHelper(new int[]{100, 100}, this);
            trimmerHelper.getFrames(videoPath);
        }
    }

    @Override
    public void onFrameObtainStart() {

    }

    @Override
    public void onFrameBitmap(Bitmap bitmap) {
        adapter.addBitmap(bitmap);
    }

    @Override
    public void onFrameObtainComplete() {
        videoPlay();
    }

    @Override
    public void onRangeSeekBarChanged(RangeSeekBar seekBar, float start, float end, float length, int anchor) {
        changeVideoRange(start, length);
    }

    @Override
    public void onRangeSeekBarComplete() {
        videoPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rangeSeekBar.recycle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //        Uri.fromFile
    }

    private void changeVideoRange(float start, float length) {
        videoView.pause();
        if (mIndicatorAnim != null && mIndicatorAnim.isRunning()) {
            mIndicatorAnim.pause();
            vIndicator.setAlpha(0f);
        }

        int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
        View startView = linearLayoutManager.findViewByPosition(firstVisibleItem);
        int left = firstVisibleItem * startView.getWidth() + recyclerView.getPaddingStart() - startView.getLeft();

        startTime = transformLengthTime(left + start, startView.getWidth());
        endTime =  transformLengthTime(left + start + length, startView.getWidth());
        videoView.seekTo((int) startTime);
    }

    private void videoPlay() {
        if (videoView.isPlaying()) return;
        videoView.start();
        startIndicatorAnimation();
    }

    private float transformLengthTime(float length, float unitLength) {
        return length * 1000L / unitLength;
    }

    private void startIndicatorAnimation() {
        vIndicator.setAlpha(1f);
        mIndicatorAnim = ObjectAnimator.ofFloat(
                vIndicator,
                "translationX",
                rangeSeekBar.getStartOffset(),
                rangeSeekBar.getStartOffset() + rangeSeekBar.getRangeLength()
        ).setDuration((long) (endTime - startTime));
        mIndicatorAnim.setRepeatCount(ValueAnimator.INFINITE);
        mIndicatorAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                videoView.seekTo((int) startTime);
            }
        });
        mIndicatorAnim.start();
    }
}
