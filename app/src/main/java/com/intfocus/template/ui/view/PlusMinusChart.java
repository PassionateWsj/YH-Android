package com.intfocus.template.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.intfocus.template.R;
import com.intfocus.template.subject.one.entity.BargraphComparator;
import com.intfocus.template.subject.one.module.bargraph.BargraphDataComparator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 正负表
 * Created by zbaoliang on 17-5-13.
 */
public class PlusMinusChart extends View implements ValueAnimator.AnimatorUpdateListener {

    private final String TAG = PlusMinusChart.class.getSimpleName();
    /**
     * 曲线数据
     */
    private LinkedList<BargraphComparator> lt_data = new LinkedList<>();
    /**
     * 默认外边距
     */
    private int margin = 60;
    /**
     * 默认内边距
     */
    private int padding = 24;

    /**
     * 柱状图宽度
     */
    private int barWidth = 50;
    /**
     * 原点坐标
     */
    private float xPoint;
    private float yPoint;
    /**
     * X,Y轴的单位长度
     */
    private float xScale;
    private float yScale;

    // 画笔
    /**
     * 轴、Y坐标
     */
    private Paint paint;

    private int defauteolor = 0x73737373;

    private int[] baseColor3;

    //---------动画--------
    private float ratio = 1;
    private long animateTime = 1600;
    private ValueAnimator mVa;
    private Interpolator mInterpolator = new DecelerateInterpolator();//先加速 后减速
    /**
     * 设置笔触宽度
     */
    private float paintStrokeW = 2;
    /**
     * 轴线
     */
    private float xCenter;
    private int mSelectItem;
    private int mDsize;
    private float mBarOffset;

    public PlusMinusChart(Context context) {
        super(context);
        init();
    }

    public PlusMinusChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化数据值和画笔
     */
    public void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(paintStrokeW);
        paint.setAntiAlias(true);
        paint.setDither(true);

        baseColor3 = getResources().getIntArray(R.array.co_cursor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(defauteolor);
        canvas.drawLine(xCenter, 0, xCenter, getHeight(), paint);
        int size = lt_data.size();
        for (int i = 0; i < size; i++) {
            drawBAR(canvas, paint);
        }
    }

    /**
     * 绘制柱状图
     *
     * @param canvas
     * @param paint
     */
    private void drawBAR(Canvas canvas, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        mDsize = lt_data.size();
        mBarOffset = yScale / 3;
        float yOffset = yScale / 2;
        float pOffset = paintStrokeW / 2;

        float left;
        float top;
        float right;
        float bottom;
        float selectXIncrement = 16;
        float selectYIncrement = 8;
        for (int i = 0; i < mDsize; i++) {
            float value = Float.parseFloat(lt_data.get(i).data.replace("%", ""));

            float length = (value * (1 + (ratio - 1))) * xScale;
            float y = yPoint + yScale * (i + 1) - yOffset;
            if (value < 0) {
                left = xCenter + length;
                top = y - mBarOffset;
                right = xCenter - pOffset;
                bottom = y + mBarOffset;
                if (mSelectItem == i) {
                    left -= selectXIncrement;
                }
            } else {
                left = xCenter + pOffset;
                top = y - mBarOffset;
                right = xCenter + length;
                bottom = y + mBarOffset;
                if (mSelectItem == i) {
                    right += selectXIncrement;
                }
            }
            if (mSelectItem == i) {
                top -= selectYIncrement;
                bottom += selectYIncrement;
            }
            RectF rectF = new RectF(left, top, right, bottom);


/*            if (Math.abs(value) <= 0.1f) {
                cursorIndex = 1;
            } else if (value < -0.1f) {
                cursorIndex = 0;
            } else {
                cursorIndex = 2;
            }*/

            int cursorIndex;
            int color = lt_data.get(i).color;
            switch (color) {
                case 0:
                case 3:
                    cursorIndex = 0;
                    break;

                case 1:
                case 4:
                    cursorIndex = 1;
                    break;

                case 2:
                case 5:
                    cursorIndex = 2;
                    break;

                default:
                    cursorIndex = 0;
            }
            paint.setColor(baseColor3[cursorIndex]);
            canvas.drawRect(rectF, paint);
        }
    }

    /**
     * 数据按比例转坐标
     */
    private float toX(float num) {
        float y;
        try {
            y = num * xScale;
        } catch (Exception e) {
            return 0;
        }
        return y;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (lt_data.size() > 0 || ratio == 1) {
            animateExcels();
        }
    }

    public void animateExcels() {
        if (mVa == null) {
            mVa = ValueAnimator.ofFloat(0, 1).setDuration(animateTime);
            mVa.addUpdateListener(this);
            mVa.setInterpolator(mInterpolator);
        }
        mVa.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        ratio = (float) animation.getAnimatedValue();
        postInvalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        onMeasureScale();
    }

    /**
     * 测量比例
     */
    private void onMeasureScale() {
        int datasize = lt_data.size();
        if (datasize <= 0) {
            return;
        }

        LinkedList<BargraphComparator> datas = new LinkedList<>();
        datas.addAll(lt_data);
        Collections.sort(datas, new BargraphDataComparator());


        float minData = Float.parseFloat(datas.get(0).data.replace("%", ""));
        float maxData = Float.parseFloat(datas.get(datasize - 1).data.replace("%", ""));

        float count;
        if (minData > 0 || maxData < 0) {
            count = Math.max(Math.abs(minData), Math.abs(maxData));
        } else {
            count = Math.abs(maxData) + Math.abs(minData);
        }

        xPoint = 0;
        yPoint = 0;
        xScale = (getWidth() - paintStrokeW) / count;
        yScale = getHeight() / datasize;
        if (minData > 0 && maxData > 0) {
            xCenter = xPoint;
        } else {
            xCenter = xPoint + Math.abs(minData) * xScale;
        }

    }

    /**
     * 设置默认颜色
     *
     * @param defauteolor
     */
    public void setDefauteolor(@ColorInt int defauteolor) {
        this.defauteolor = defauteolor;
    }

    public void setDefauteMargin(int defauteMargin) {
        this.margin = defauteMargin;
    }

    /**
     * 设置数据值集合
     *
     * @param datas
     */
    public void setDataValues(@NonNull List<BargraphComparator> datas) {
        lt_data.clear();
        lt_data.addAll(datas);
    }

    /**
     * 更行数据值集合
     *
     * @param datas
     */
    public void updateData(@NonNull List<BargraphComparator> datas) {
        setDataValues(datas);
        animateExcels();
    }

    /**
     * 图表触摸事件回调方法
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                onActionUpEvent(event);
                break;
            default:
                break;
        }
        return true;
    }

    private void onActionUpEvent(MotionEvent event) {
        boolean isValidTouch = validateTouch(event.getX(), event.getY());
        if (isValidTouch) {
            if (listener != null) {
                listener.onPointClick(mSelectItem);
            }
            invalidate();
        }
    }

    /**
     * 是否是有效的触摸范围
     *
     * @param xTouchPoint
     * @param yTouchPoint
     * @return
     */
    private boolean validateTouch(float xTouchPoint, float yTouchPoint) {
        //触摸区域
        int dsize = lt_data.size();
        float barOffset = yScale / 3;
        float yOffset = yScale / 2;
        float top;
        float bottom;
        for (int i = 0; i < dsize; i++) {
            float y = yPoint + yScale * (i + 1) - yOffset;
            top = y - barOffset;
            bottom = y + barOffset;
            if (yTouchPoint < bottom && yTouchPoint > top) {

                mSelectItem = i;
                if (listener != null) {
                    listener.onPointClick(mSelectItem);
                }
                return true;
            }
        }
        return false;
    }

    public void onClickItem(int index) {
        mSelectItem = index;
        invalidate();
    }

    private PlusMinusOnItemClickListener listener;

    public void setPointClickListener(PlusMinusOnItemClickListener listener) {
        this.listener = listener;
    }

    public interface PlusMinusOnItemClickListener {
        void onPointClick(int index);
    }
}