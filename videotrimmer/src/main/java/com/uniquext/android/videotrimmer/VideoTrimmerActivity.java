package com.uniquext.android.videotrimmer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uniquext.android.videotrimmer.adapter.VideoTrimmerAdapter;
import com.uniquext.android.videotrimmer.ui.RangeSeekBar;
import com.uniquext.android.widget.util.Convert;
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

    private VideoTrimmerAdapter adapter;
    private VideoFrameHelper trimmerHelper;

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

        int[] screen = new int[2];
        ViewMeasure.getScreenSize(getWindowManager(), screen);
        float itemWidth = (screen[0] - recyclerView.getPaddingStart() - recyclerView.getPaddingEnd()) * 0.1f;
        adapter = new VideoTrimmerAdapter((int) itemWidth);
        recyclerView.setAdapter(adapter);

        rangeSeekBar.setOnRangeSeekBarChangeListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (manager != null && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //  前面有多少个
                    int firstVisibleItem = manager.findFirstVisibleItemPosition();
                    int lastVisibleItem = manager.findLastVisibleItemPosition();

                   View start = manager.findViewByPosition(firstVisibleItem);

                    Log.e("####", firstVisibleItem + " # " + start.getWidth() + " # " + (recyclerView.getPaddingStart() - start.getLeft())+ " # " + start.getRight());

//                    int itemWidth = start.getWidth();
//                    int itemRight = start.getRight();

//                    //找到即将移出屏幕Item的position,position是移出屏幕item的数量
//                    int position = linearLayoutManager.findFirstVisibleItemPosition();
////根据position找到这个Item
//                    View firstVisiableChildView = linearLayoutManager.findViewByPosition(position);
////获取Item的高
//                    int itemHeight = firstVisiableChildView.getHeight();
////算出该Item还未移出屏幕的高度
//                    int itemTop = firstVisiableChildView.getTop();
////position移出屏幕的数量*高度得出移动的距离
//                    int iposition = position * itemHeight;
////减去该Item还未移出屏幕的部分可得出滑动的距离
//                    iResult = iposition - itemTop;



                    //  HORIZONTAL
                    //找到即将移出屏幕Item的position,position是移出屏幕item的数量
//                    int position = linearLayoutManager.findFirstVisibleItemPosition();
////根据position找到这个Item
//                    View firstVisiableChildView = linearLayoutManager.findViewByPosition(position);
////获取Item的宽
//                    int itemWidth = firstVisiableChildView.getWidth();
////算出该Item还未移出屏幕的高度
//                    int itemRight = firstVisiableChildView.getRight();
////position移出屏幕的数量*高度得出移动的距离
//                    int iposition = position * itemWidth;
////因为横着的RecyclerV第一个取到的Item position为零所以计算时需要加一个宽
//                    iResult = iposition - itemRight + itemWidth;

                }
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
        videoView.start();
    }

    @Override
    public void onFrameBitmap(Bitmap bitmap) {
        adapter.addBitmap(bitmap);
    }

    @Override
    public void onFrameObtainComplete() {
    }

    @Override
    public void onRangeSeekBarChanged(RangeSeekBar seekBar, float start, float end, int anchor) {
//        recyclerView.get()
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
}
