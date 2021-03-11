package com.uniquext.android.videotrimmer.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import com.uniquext.android.videotrimmer.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
 * @date 2021-03-09  17:10
 */
public class RangeSeekBar extends View {

    private static final int FRAME_LINE_WIDTH_PX = 3;

    private RectF mRangeRectF = new RectF();
    private Paint mFramePaint = new Paint();
    /**
     * 当前操作的锚点
     */
    @MotionAction
    private int mAnchorMotionAction = MotionAction.NONE;
    /**
     * 触摸坐标与当前操作锚点偏移量
     */
    private float mOffsetX = 0;

    /**
     * 两端锚点位移量
     */
    private int[] anchorOffset = new int[2];

    /**
     * 最小距离
     */
    private float minRange = 0;
    /**
     * 默认最短距离
     * 左右锚点图片宽度
     */
    private float defaultMinRange = 0;

    private Bitmap startBitmap;
    private Bitmap endBitmap;

    private OnRangeSeekBarChangeListener onRangeSeekBarChangeListener;


    public RangeSeekBar(Context context) {
        super(context);
        this.initView(context, null);
    }

    public RangeSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs);
    }

    public RangeSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs);
    }

    public RangeSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        initRangeBitmap();
        anchorOffset[0] = Math.max(startBitmap.getWidth(), anchorOffset[0]);
        anchorOffset[1] = getWidth() - Math.max(startBitmap.getWidth(), anchorOffset[1]);
        mRangeRectF.set(anchorOffset[0], 1, anchorOffset[1], getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(mRangeRectF, mFramePaint);
        canvas.drawBitmap(startBitmap, mRangeRectF.left - startBitmap.getWidth(), 0, null);
        canvas.drawBitmap(endBitmap, mRangeRectF.right, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float coordinateX = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mAnchorMotionAction = computerMotionAction(coordinateX);
                if (mAnchorMotionAction == MotionAction.ANCHOR_START) {
                    mOffsetX = mRangeRectF.left - coordinateX;
                } else if (mAnchorMotionAction == MotionAction.ANCHOR_END) {
                    mOffsetX = mRangeRectF.right - coordinateX;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mAnchorMotionAction == MotionAction.ANCHOR_START) {
                    float left = Math.max(anchorOffset[0], coordinateX + mOffsetX);
                    if (mRangeRectF.left != left && left >= anchorOffset[0] && mRangeRectF.right - left > minRange) {
                        mRangeRectF.left = left;
                        changeRangeSeekBar(MotionAction.ANCHOR_START);
                        invalidate();
                    }
                } else if (mAnchorMotionAction == MotionAction.ANCHOR_END) {
                    float right = Math.min(anchorOffset[1], coordinateX + mOffsetX);
                    if (mRangeRectF.right != right && right <= anchorOffset[1] && right - mRangeRectF.left > minRange) {
                        mRangeRectF.right = right;
                        changeRangeSeekBar(MotionAction.ANCHOR_END);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mAnchorMotionAction = MotionAction.NONE;
                onRangeSeekBarChangeListener.onRangeSeekBarComplete();
                break;
        }
        return mAnchorMotionAction != MotionAction.NONE || super.onTouchEvent(event);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        mFramePaint.setColor(Color.WHITE);
        mFramePaint.setStyle(Paint.Style.STROKE);
        float density = context.getResources().getDisplayMetrics().density;
        mFramePaint.setStrokeWidth(FRAME_LINE_WIDTH_PX * density);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RangeSeekBar);
        anchorOffset[0] = typedArray.getDimensionPixelOffset(R.styleable.RangeSeekBar_start, 1);
        anchorOffset[1] = typedArray.getDimensionPixelOffset(R.styleable.RangeSeekBar_end, 0);
        typedArray.recycle();
    }

    private void initRangeBitmap() {
        startBitmap = scaleRangeAnchorBitmap(R.drawable.range_seek_bar_anchor_start);
        endBitmap = scaleRangeAnchorBitmap(R.drawable.range_seek_bar_anchor_end);
        defaultMinRange = startBitmap.getWidth() + endBitmap.getWidth();
        setMinRange(minRange);
    }

    private Bitmap scaleRangeAnchorBitmap(@DrawableRes int drawableId) {
        Bitmap source = BitmapFactory.decodeResource(getResources(), drawableId);
        float scale = getHeight() / (1f * source.getHeight());
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap target = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        source.recycle();
        return target;
    }

    @MotionAction
    private int computerMotionAction(float coordinateX) {
        if (coordinateX >= mRangeRectF.left - startBitmap.getWidth() && coordinateX <= mRangeRectF.left + startBitmap.getWidth()) {
            return MotionAction.ANCHOR_START;
        } else if (coordinateX >= mRangeRectF.right - endBitmap.getWidth() && coordinateX <= mRangeRectF.right + endBitmap.getWidth()) {
            return MotionAction.ANCHOR_END;
        } else {
            return MotionAction.NONE;
        }
    }

    private void changeRangeSeekBar(@MotionAction int motionAction) {
        onRangeSeekBarChangeListener.onRangeSeekBarChanged(this, getStartOffset(), getEndOffset(), getRangeLength(), motionAction);
    }

    public void setOnRangeSeekBarChangeListener(OnRangeSeekBarChangeListener onRangeSeekBarChangeListener) {
        this.onRangeSeekBarChangeListener = onRangeSeekBarChangeListener;
    }

    /**
     * 设置最短阈值
     * @param minRange
     */
    public void setMinRange(float minRange) {
        this.minRange = Math.max(minRange, defaultMinRange);
    }

    public float getStartOffset() {
        return mRangeRectF.left - anchorOffset[0];
    }

    public float getEndOffset() {
        return mRangeRectF.right - anchorOffset[1];
    }

    public float getRangeLength() {
        return mRangeRectF.width();
    }

    public void recycle() {
        if(startBitmap != null) {
            startBitmap.recycle();
        }
        if (endBitmap != null) {
            endBitmap.recycle();
        }
    }

    public interface OnRangeSeekBarChangeListener {
        void onRangeSeekBarChanged(RangeSeekBar seekBar, float start, float end, float length, @MotionAction int anchor);
        void onRangeSeekBarComplete();
    }


    @IntDef({MotionAction.NONE, MotionAction.ANCHOR_START, MotionAction.ANCHOR_END})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MotionAction {
        int NONE = 0b00;
        int ANCHOR_START = 0b01;
        int ANCHOR_END = 0b10;
    }

}
