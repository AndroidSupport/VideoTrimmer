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
    private float mOffsetX = 0;

    /**
     * 初始值阈
     */
    private int[] initialRange = new int[2];

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
        initialRange[0] = Math.max(startBitmap.getWidth(), initialRange[0]);
        initialRange[1] = Math.max(startBitmap.getWidth(), initialRange[1]);
        mRangeRectF.set(initialRange[0], 1, getWidth() - initialRange[1], getHeight());
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
        @MotionAction final int motionAction = computerMotionAction(coordinateX);
        if (motionAction == MotionAction.NONE) {
            return super.onTouchEvent(event);
        } else if (mRangeRectF.width() <= startBitmap.getWidth() + endBitmap.getWidth()) {
            return true;
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mOffsetX =
                    mAnchorMotionAction = motionAction;
                case MotionEvent.ACTION_MOVE:
                    if (mAnchorMotionAction == MotionAction.ANCHOR_START) {
                        float left = Math.max(startBitmap.getWidth(), coordinateX);
                        if (mRangeRectF.right - left > startBitmap.getWidth() + endBitmap.getWidth()) {
                            mRangeRectF.left = left;
                            onRangeSeekBarChangeListener.onRangeSeekBarChanged(this, mRangeRectF.left, mRangeRectF.right, MotionAction.ANCHOR_START);
                            invalidate();
                        }
                    } else if (mAnchorMotionAction == MotionAction.ANCHOR_END) {
                        float right = Math.min(getWidth() - endBitmap.getWidth(), coordinateX);
                        if (right - mRangeRectF.left > startBitmap.getWidth() + endBitmap.getWidth()) {
                            mRangeRectF.right = right;
                            onRangeSeekBarChangeListener.onRangeSeekBarChanged(this, mRangeRectF.left, mRangeRectF.right, MotionAction.ANCHOR_END);
                            invalidate();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mAnchorMotionAction = MotionAction.NONE;
                    break;
            }
            return true;
        }
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        mFramePaint.setColor(Color.WHITE);
        mFramePaint.setStyle(Paint.Style.STROKE);
        float density = context.getResources().getDisplayMetrics().density;
        mFramePaint.setStrokeWidth(FRAME_LINE_WIDTH_PX * density);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RangeSeekBar);
        initialRange[0] = typedArray.getDimensionPixelOffset(R.styleable.RangeSeekBar_start, 1);
        initialRange[1] = typedArray.getDimensionPixelOffset(R.styleable.RangeSeekBar_end, 0);
        typedArray.recycle();
    }

    private void initRangeBitmap() {
        startBitmap = scaleRangeAnchorBitmap(R.drawable.range_seek_bar_anchor_start);
        endBitmap = scaleRangeAnchorBitmap(R.drawable.range_seek_bar_anchor_end);

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

    public void setOnRangeSeekBarChangeListener(OnRangeSeekBarChangeListener onRangeSeekBarChangeListener) {
        this.onRangeSeekBarChangeListener = onRangeSeekBarChangeListener;
    }

    public void recycle() {
        startBitmap.recycle();
        endBitmap.recycle();
    }

    public interface OnRangeSeekBarChangeListener {
        void onRangeSeekBarChanged(RangeSeekBar seekBar, float start, float end, @MotionAction int anchor);
    }


    @IntDef({MotionAction.NONE, MotionAction.ANCHOR_START, MotionAction.ANCHOR_END})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MotionAction {
        int NONE = 0b00;
        int ANCHOR_START = 0b01;
        int ANCHOR_END = 0b10;
    }

}
