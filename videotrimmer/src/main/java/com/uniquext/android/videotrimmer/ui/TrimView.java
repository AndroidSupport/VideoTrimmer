package com.uniquext.android.videotrimmer.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/24 16:18
 * @description
 */
public class TrimView extends View {

    /**
     * 自由裁剪
     */
    public static final float FREE_RATIO = 0f;

    //  region 裁剪框基线坐标相关
    /**
     * 裁剪辅助行数
     */
    private static final int ROW_COUNT = 3;
    /**
     * 裁剪辅助列数
     */
    private static final int COLUMN_COUNT = 3;
    /**
     * 裁剪框基线颜色值
     */
    private static final int CLIP_FRAME_LINE_COLOR = 0xFFFFFFFF;
    /**
     * 裁剪框外部蒙版颜色值
     */
    private static final int CLIP_FRAME_OUT_COLOR = 0xB2000000;
    /**
     * 缩放锚点单位长度
     */
    private static final float UNIT_ANCHOR_LENGTH = 10f;
    //  endregion

    //  region 颜色值
    /**
     * 最小可裁剪宽度
     */
    private static final float MIN_GRID_WIDTH = 200f;
    /**
     * 辅助线宽度
     */
    private static final int GRID_LINE_WIDTH_PX = 1;
    //  endregion

    //  region 宽度设点
    /**
     * 表格边框宽度
     */
    private static final int FRAME_LINE_WIDTH_PX = 2;
    /**
     * 锚点宽度
     */
    private static final int ANCHOR_LINE_WIDTH_PX = 4;
    /**
     * 无效操作
     */
    private static final int MOTION_ACTION_NONE = 0x0000;
    /**
     * 左锚点
     */
    private static final int MOTION_ACTION_LEFT = 0x0001;

    //  endregion

    //  region 操作事件
    /**
     * 右锚点
     */
    private static final int MOTION_ACTION_RIGHT = 0x0004;


    /**
     * 移动操作
     */
    private static final int MOTION_ACTION_MOVE = 0x000F;
    /**
     * 表格边线坐标数组大小
     */
    private int FRAME_LINE_COUNT = 2 * 2 * 4;
    /**
     * 锚点线坐标数组大小
     */
    private int ANCHOR_LINE_COUNT = (ROW_COUNT + COLUMN_COUNT) * 2 * 4;
    /**
     * 辅助线坐标数组大小
     */
    private int GRID_LINE_COUNT = (ROW_COUNT - 1) * (COLUMN_COUNT - 1) * 4;
    //  endregion

    /**
     * 图像裁剪比
     */
    private float mClipRatio = FREE_RATIO;
    /**
     * 触摸事件
     */
    private int mMotionActions = MOTION_ACTION_NONE;

    /**
     * 锚点单位长
     * 单位dp
     */
    private float mUnitAnchorLength = 0f;


    /**
     * 裁剪矩阵
     */
    private RectF mClipRect = new RectF();
    /**
     * 图像矩阵
     */
    private RectF mDrawableRectF = new RectF();
    /**
     * 最后移动点
     */
    private PointF mLastMotionPoint = new PointF();

    //  region 画笔

    /**
     * 边线画笔
     */
    private Paint mFrameLinePaint = new Paint();
    /**
     * 锚点画笔
     */
    private Paint mAnchorLinePaint = new Paint();
    //  endregion

    //  region 坐标
    /**
     * 边线坐标
     */
    private float[] mFrameLines = new float[FRAME_LINE_COUNT];
    /**
     * 锚点坐标
     */
    private float[] mAnchorLines = new float[ANCHOR_LINE_COUNT];
    //  endregion

    public TrimView(Context context) {
        this(context, null);
    }

    public TrimView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFrameLinePaint.setColor(CLIP_FRAME_LINE_COLOR);
        mAnchorLinePaint.setColor(CLIP_FRAME_LINE_COLOR);
        float density = getContext().getResources().getDisplayMetrics().density;
        mUnitAnchorLength = UNIT_ANCHOR_LENGTH * density;
        mFrameLinePaint.setStrokeWidth(FRAME_LINE_WIDTH_PX * density);
        mAnchorLinePaint.setStrokeWidth(ANCHOR_LINE_WIDTH_PX * density);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mDrawableRectF.set(left, top, right, bottom);
        initClipGrid();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.drawColor(CLIP_FRAME_OUT_COLOR);
        canvas.restoreToCount(layerId);
        canvas.drawLines(mFrameLines, 0, mFrameLines.length, mFrameLinePaint);
        canvas.drawLines(mAnchorLines, 0, mAnchorLines.length, mAnchorLinePaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float coordinateX = event.getX();
        float coordinateY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionPoint.set(coordinateX, coordinateY);
                computerMotionAction(coordinateX, coordinateY);
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetX = coordinateX - mLastMotionPoint.x;

                if (mMotionActions == MOTION_ACTION_MOVE) {
                    if (mClipRect.left + offsetX < mDrawableRectF.left
                            || mClipRect.right + offsetX > mDrawableRectF.right) {
                        offsetX = 0;
                    }
                    mClipRect.offset(offsetX, 0);
                } else {
                    scaleOnAxis(offsetX);
                }
                mLastMotionPoint.set(coordinateX, coordinateY);
                break;
            case MotionEvent.ACTION_UP:
                autoReplace();
                break;
            default:
                break;
        }
        drawClipGrid();
        return true;
    }


    /**
     * 初始化定位
     */
    private void initClipGrid() {
        mClipRect.set(mDrawableRectF);
        float deltaWidth = Math.abs(mClipRect.width() - mClipRect.height()) * 0.5f;
        if (mClipRect.width() > mClipRect.height()) {
            mClipRect.inset(deltaWidth, 0);
        } else {
            mClipRect.inset(0, deltaWidth);
        }
        mClipRect.inset(ANCHOR_LINE_WIDTH_PX, ANCHOR_LINE_WIDTH_PX);
        drawClipGrid();
    }

    /**
     * 自动调整
     */
    private void autoReplace() {
        if (!mDrawableRectF.contains(mClipRect)) {
            autoScale();
            springBack();
        }
    }

    /**
     * 绘制裁剪表格
     */
    private void drawClipGrid() {
        drawFrameLines();
        drawAnchorLines();
        invalidate();
    }

    /**
     * 绘制边框
     */
    private void drawFrameLines() {
        // Column frame line
        for (int column = 0; column < 2; column++) {
            float pointX = column == 0 ? mClipRect.left : mClipRect.right;
            mFrameLines[4 * column + 0] = pointX;
            mFrameLines[4 * column + 1] = mClipRect.top;
            mFrameLines[4 * column + 2] = pointX;
            mFrameLines[4 * column + 3] = mClipRect.bottom;
        }
        // Row frame line
        int pointOffset = 2 * 4;
        for (int row = 0; row < 2; row++) {
            float pointY = row == 0 ? mClipRect.top : mClipRect.bottom;
            mFrameLines[4 * row + pointOffset + 0] = mClipRect.left;
            mFrameLines[4 * row + pointOffset + 1] = pointY;
            mFrameLines[4 * row + pointOffset + 2] = mClipRect.right;
            mFrameLines[4 * row + pointOffset + 3] = pointY;
        }
    }

    /**
     * 绘制锚点
     */
    private void drawAnchorLines() {
        // Column frame line
        int pointOffset = 0;
        for (int column = 0; column < 2; column++) {
            float pointX = column == 0 ? mClipRect.left : mClipRect.right;
            for (int section = 0; section < COLUMN_COUNT; section++) {
                float top, bottom;
                if (section == 0) {
                    top = mClipRect.top - mAnchorLinePaint.getStrokeWidth() * 0.5f;
                    bottom = mClipRect.top + mUnitAnchorLength;
                } else if (section == 1) {
                    top = (mClipRect.top + mClipRect.bottom) * 0.5f - mUnitAnchorLength;
                    bottom = (mClipRect.top + mClipRect.bottom) * 0.5f + mUnitAnchorLength;
                } else {
                    top = mClipRect.bottom - mUnitAnchorLength;
                    bottom = mClipRect.bottom + mAnchorLinePaint.getStrokeWidth() * 0.5f;
                }
                mAnchorLines[4 * pointOffset + 0] = pointX;
                mAnchorLines[4 * pointOffset + 1] = top;
                mAnchorLines[4 * pointOffset + 2] = pointX;
                mAnchorLines[4 * pointOffset + 3] = bottom;
                pointOffset++;
            }
        }
        // Row frame line
        for (int row = 0; row < 2; row++) {
            float pointY = row == 0 ? mClipRect.top : mClipRect.bottom;
            for (int section = 0; section < COLUMN_COUNT; section++) {
                float left, right;
                if (section == 0) {
                    left = mClipRect.left - mAnchorLinePaint.getStrokeWidth() * 0.5f;
                    right = mClipRect.left + mUnitAnchorLength;
                } else if (section == 1) {
                    left = (mClipRect.left + mClipRect.right) * 0.5f - mUnitAnchorLength;
                    right = (mClipRect.left + mClipRect.right) * 0.5f + mUnitAnchorLength;
                } else {
                    left = mClipRect.right - mUnitAnchorLength;
                    right = mClipRect.right + mAnchorLinePaint.getStrokeWidth() * 0.5f;
                }
                mAnchorLines[4 * pointOffset + 0] = left;
                mAnchorLines[4 * pointOffset + 1] = pointY;
                mAnchorLines[4 * pointOffset + 2] = right;
                mAnchorLines[4 * pointOffset + 3] = pointY;
                pointOffset++;
            }
        }
    }

    /**
     * 计算动作类型
     *
     * @param x x坐标
     * @param y y坐标
     */
    private void computerMotionAction(float x, float y) {
        float deltaUnit = 2 * mUnitAnchorLength;
        RectF touch = new RectF(x, y, x, y);
        touch.inset(-deltaUnit, -deltaUnit);
        if (!RectF.intersects(mClipRect, touch)) {
            mMotionActions = MOTION_ACTION_NONE;
            return;
        }
        int verticalAction;
        if (x > mClipRect.left - deltaUnit && x < mClipRect.left + deltaUnit) {
            verticalAction = MOTION_ACTION_LEFT;
        } else if (x > mClipRect.right - deltaUnit && x < mClipRect.right + deltaUnit) {
            verticalAction = MOTION_ACTION_RIGHT;
        } else {
            verticalAction = MOTION_ACTION_NONE;
        }
        mMotionActions = verticalAction;
        if (mMotionActions == MOTION_ACTION_NONE) {
            mMotionActions = MOTION_ACTION_MOVE;
        }
    }

    /**
     * 在X/Y轴上缩放
     *
     * @param offsetX x偏移量
     */
    private void scaleOnAxis(float offsetX) {
        boolean subScale;
        boolean subScaleX = mClipRect.width() > MIN_GRID_WIDTH;
        boolean subScaleY = mClipRect.height() > MIN_GRID_WIDTH;
        boolean addScale = mClipRatio == FREE_RATIO || mDrawableRectF.contains(mClipRect);
        switch (mMotionActions) {
            case MOTION_ACTION_LEFT:
                subScale = subScaleX && (mClipRatio == FREE_RATIO || subScaleY);
                if (offsetX > 0 && subScale || offsetX < 0 && addScale) {
                    mClipRect.left += offsetX;
                    scaleClipGridHeight(mClipRatio == FREE_RATIO ? 0 : offsetX / mClipRatio);
                }
                break;

            case MOTION_ACTION_RIGHT:
                subScale = subScaleX && (mClipRatio == FREE_RATIO || subScaleY);
                if (offsetX < 0 && subScale || offsetX > 0 && addScale) {
                    mClipRect.right += offsetX;
                    scaleClipGridHeight(mClipRatio == FREE_RATIO ? 0 : -offsetX / mClipRatio);
                }
                break;
            default:
                break;
        }
    }


    /**
     * 缩放裁剪框Y轴
     *
     * @param dy y偏移量
     */
    private void scaleClipGridHeight(float dy) {
        mClipRect.top += dy * 0.5f;
        mClipRect.bottom -= dy * 0.5f;
    }

    /**
     * 回弹
     */
    private void springBack() {
        float deltaLeft = mDrawableRectF.left - mClipRect.left;
        float deltaTop = mDrawableRectF.top - mClipRect.top;
        float deltaRight = mDrawableRectF.right - mClipRect.right;
        float deltaBottom = mDrawableRectF.bottom - mClipRect.bottom;
        if (deltaLeft > 0) {
            mClipRect.offset(deltaLeft, 0);
        } else if (deltaRight < 0) {
            mClipRect.offset(deltaRight, 0);
        }
        if (deltaTop > 0) {
            mClipRect.offset(0, deltaTop);
        } else if (deltaBottom < 0) {
            mClipRect.offset(0, deltaBottom);
        }
    }

    /**
     * 自动缩放
     */
    private void autoScale() {
        float deltaWidth = Math.max(0, mClipRect.width() - mDrawableRectF.width());
        float deltaHeight = Math.max(0, mClipRect.height() - mDrawableRectF.height());
        float deltaMax = Math.max(deltaWidth, deltaHeight);
        if (deltaMax > 0) {
            if (mClipRatio == FREE_RATIO) {
                mClipRect.inset(deltaWidth * 0.5f, deltaHeight * 0.5f);
            } else {
                mClipRect.inset(deltaMax * 0.5f, deltaMax * 0.5f);
            }
        }
    }
}
