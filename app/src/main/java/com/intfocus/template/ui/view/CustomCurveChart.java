package com.intfocus.template.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.intfocus.template.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 曲线图
 * Created by zbaoliang on 17-5-10.
 */
public class CustomCurveChart extends View implements ValueAnimator.AnimatorUpdateListener {
    private String unit;
    /**
     * 坐标单位
     */
    private String[] xLabel;
    private String[] yLabel;
    /**
     * 曲线数据
     */
    private List<Float[]> dataList;
    private int[] colorList;
    /**
     * 默认边距
     */
    private int margin = 60;
    /**
     * 默认边距
     */
    private float padding = 24;

    /**
     * 柱状图宽度
     */
    private float barWidth = 25;
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
    private Paint paintAxes;
    private Paint paintCoordinate;
    private Paint paintCurve;
    private Paint paint_broken;
    private Paint paint_circle;

    private int defaultColor = 0x73737373;
    private int blackColor = 0xff333333;
    private int barSelectColor = 0xff666666;
    private int textSize;
    /**
     * 条状图之间间隔 为 x 刻度单位长度的40%
     */
    private float mBarChartInterval = 0.4f;
    private List<Integer> mLineChart;
    private List<Integer> mBarChart;
    private int mBarCount;

    public interface ChartStyle {
        String BAR = "bar";
        String LINE = "line";
    }

    private List<String> mChartStyle;

    private int[] orderColors;

    /**
     * 动画
     */
    private float ratio = 1;
    private long animateTime = 1200;
    private ValueAnimator mVa;
    /**
     * 先加速 后减速
     */
    private Interpolator mInterpolator = new DecelerateInterpolator();

    private ArrayList<Float> xPoints = new ArrayList<>();
    private int selectItem;

    public CustomCurveChart(Context context) {
        super(context);
        init();
    }

    public CustomCurveChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        onMeasureScale();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = (int) (sizeWidth * 0.5);
        setMeasuredDimension(sizeWidth, sizeHeight);
    }

    /**
     * 测量比例
     */
    private void onMeasureScale() {
        int textW = 0;
        int textH = 0;
        if (yLabel != null && yLabel.length != 0) {
            String lable = yLabel[yLabel.length - 1];
            Rect rect = new Rect();
            paintAxes.getTextBounds(lable.toCharArray(), 0, lable.length(), rect);
            textW = rect.width();
            textH = rect.height();
        }
        // 柱图之间间隙不能超过 x轴单位长度的 40%
        mBarCount = 0;
        for (int i = 0; i < mChartStyle.size(); i++) {
            if (ChartStyle.BAR.equals(mChartStyle.get(i))) {
                mBarCount++;
            }
        }
        xPoint = margin + padding + textW;
        yPoint = getHeight() - margin - padding;
        xScale = (getWidth() - xPoint - padding) / xLabel.length;
//        xScale = (getWidth() - xPoint) / (xLabel.length + (1 - mBarChartInterval) / (2 * mBarCount));
        barWidth = (1 - mBarChartInterval) * xScale / mBarCount;
        yScale = (getHeight() - margin * 2 - padding * 2 - textH) / (Float.valueOf(yLabel[yLabel.length - 1]) - Float.valueOf(yLabel[0]));
    }

    /**
     * 初始化数据值和画笔
     */
    public void init() {
        paintAxes = new Paint();
        paintAxes.setStyle(Paint.Style.FILL);
        paintAxes.setAntiAlias(true);
        paintAxes.setDither(true);
        paintAxes.setColor(defaultColor);
        paintAxes.setTextSize(dipToPx(12));

        paintCoordinate = new Paint();
        paintCoordinate.setStyle(Paint.Style.FILL);
        paintCoordinate.setDither(true);
        paintCoordinate.setAntiAlias(true);
        paintCoordinate.setColor(defaultColor);
        textSize = dipToPx(12);
        paintCoordinate.setTextSize(textSize);

        paintCurve = new Paint();
        paintCurve.setStyle(Paint.Style.STROKE);
        paintCurve.setDither(true);
        paintCurve.setAntiAlias(true);
        paintCurve.setStrokeWidth(4);

        paint_broken = new Paint();
        paint_broken.setAntiAlias(true);
        paint_broken.setStyle(Paint.Style.FILL);
        paint_broken.setStrokeWidth(2);
        paint_broken.setColor(0xffffffff);

        paint_circle = new Paint();
        paint_circle.setAntiAlias(true);
        paint_circle.setStyle(Paint.Style.FILL);
        paint_circle.setStrokeWidth(4);
        paint_circle.setStrokeCap(Paint.Cap.ROUND);

        orderColors = getResources().getIntArray(R.array.co_order);
        blackColor = ContextCompat.getColor(getContext(), R.color.co3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        drawTopLine(canvas, paintAxes);
        drawAxesLine(canvas, paintAxes);
        drawCoordinate(canvas, paintCoordinate);
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        paintCurve.clearShadowLayer();
        mLineChart = new ArrayList<>();
        mBarChart = new ArrayList<>();
        int size = dataList.size();
        for (int i = 0; i < size; i++) {
            int color;
            if (orderColors == null || orderColors.length == 0) {
                color = defaultColor;
            } else {
                color = orderColors[i % orderColors.length];
            }
            switch (mChartStyle.get(i)) {
                case ChartStyle.LINE:
                    mLineChart.add(i);
                    drawLine(canvas, paintCurve, mLineChart.size() - 1, color);
                    break;
                case ChartStyle.BAR:
                    mBarChart.add(i);
                    drawBAR(canvas, mBarChart.size() - 1, color);
                    break;
//                case "line-bar":
//                    drawBAR(canvas, i, color);
//                    drawLine(canvas, paintCurve, i, color);
//                    break;
                default:
                    mLineChart.add(i);
                    drawLine(canvas, paintCurve, mLineChart.size() - 1, color);
            }
        }
    }

    private void drawTopLine(Canvas canvas, Paint paintAxes) {
        canvas.drawLine(0, 0, this.getWidth(), 0, paintAxes);
    }

    /**
     * 绘制坐标轴
     */
    private void drawAxesLine(Canvas canvas, Paint paint) {
        // X
        canvas.drawLine(0, yPoint, this.getWidth(), yPoint, paint);
        // 零刻度
        if (Float.parseFloat(yLabel[0]) < 0) {
            canvas.drawLine(xPoint, toY(-Float.parseFloat(yLabel[0])), this.getWidth(), toY(-Float.parseFloat(yLabel[0])), paint);
        }
    }

    /**
     * 绘制刻度
     *
     * @param canvas
     * @param paint
     */
    private void drawCoordinate(Canvas canvas, Paint paint) {
        // X轴坐标
        paint.setTextAlign(Paint.Align.CENTER);
        int xlength = xLabel.length;
        xPoints.clear();
        for (int i = 0; i < xlength; i++) {
            float startX = xPoint + xScale / 2;
            startX += i * xScale;

            int color;
            if (mChartStyle.contains(ChartStyle.LINE)) {
                if (colorList == null || i > colorList.length - 1) {
                    color = defaultColor;
                } else {
                    color = getRelativeColor(colorList[i]);
                }
            } else {
                if (selectItem == i) {
                    color = blackColor;
                } else {
                    color = defaultColor;
                }
            }
            paint.setColor(color);

            // 动态计算 X 刻度 文字大小 && 显示个数
            Rect rect;
            // X 小于等于12个值，刻度全显示
            if (xlength < 13) {
                while (true) {
                    rect = new Rect();
                    paint.getTextBounds(xLabel[i].toCharArray(), 0, xLabel[i].length(), rect);
                    if (i == xlength - 1) {
                        if (startX + rect.width() > getWidth()) {
                            paint.setTextSize(paint.getTextSize() * 0.95f);
                        } else {
                            break;
                        }
                    } else {
                        if (startX + rect.width() > startX + xScale) {
                            paint.setTextSize(paint.getTextSize() * 0.95f);
                        } else {
                            break;
                        }
                    }
                }
                canvas.drawText(xLabel[i], startX, yPoint - paint.getFontMetrics().top, paint);
            }
            // X 大于12个值，刻度显示头尾
            else {
                if (i == 0 || i == xlength - 1) {
                    while (true) {
                        rect = new Rect();
                        paint.getTextBounds(xLabel[i].toCharArray(), 0, xLabel[i].length(), rect);
                        if (startX + rect.width() / 2 > getWidth()) {
                            paint.setTextSize(paint.getTextSize() * 0.95f);
                        } else {
                            break;
                        }
                    }
                    canvas.drawText(xLabel[i], startX, yPoint - paint.getFontMetrics().top, paint);
                }
            }

            xPoints.add(startX);
        }

        // Y轴坐标
        paintAxes.setTextAlign(Paint.Align.LEFT);
        Rect rect = new Rect();
        String maxT = yLabel[yLabel.length - 1];
        paintAxes.getTextBounds(maxT, 0, maxT.length(), rect);
        float textW = rect.width();
        float textH = rect.height();
        int ylsize = yLabel.length;
        for (int i = 1; i < ylsize; i++) {
            Float ylabel = Float.parseFloat(yLabel[i]) - Float.parseFloat(yLabel[0]);
            float startY = toY(ylabel);
            canvas.drawText(yLabel[i], margin, startY + textH / 2, paintAxes);
        }
        float unitoffect = margin + padding;
        canvas.drawText(unit, unitoffect + textW / 2, margin + textH / 2, paintAxes);
    }

    /**
     * 获取线段相应颜色
     *
     * @param index
     * @return
     */
    private int getRelativeColor(int index) {
        if (orderColors == null || orderColors.length == 0 || orderColors.length < index - 1) {
            return defaultColor;
        }
        return orderColors[index];
    }

    /**
     * 绘制折线
     */
    private void drawLine(Canvas canvas, Paint paint, int dataIndex, int drawColor) {
        Float[] data = dataList.get(mLineChart.get(dataIndex));
        paint.setColor(drawColor);
        paintCurve.setStyle(Paint.Style.STROKE);
        paintCurve.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        Path path = new Path();
        int xSize = xLabel.length;
        int dSize = data.length;
        int loopingCount = xSize > dSize ? dSize : xSize;
        float xPointLine = xPoint + xScale / 2;
        for (int i = 0; i < loopingCount; i++) {

            if (!isDrawLine(data, loopingCount, i)) {
                continue;
            }
            float yPoint = toY(data[i] - Float.parseFloat(yLabel[0]));
            if (i == 0) {
                path.moveTo(xPointLine, yPoint);
            } else {
                path.lineTo(xPointLine + i * xScale * ratio, yPoint);
            }

            if (i == xLabel.length - 1) {
                path.lineTo(xPointLine + i * xScale * ratio, yPoint);
            }
        }
        canvas.drawPath(path, paint);
        if (ratio == 1) {
            for (int i = 0; i < loopingCount; i++) {
                float yPointCircle;
                if (i == 0) {
                    yPointCircle = toY(data[0] - Float.parseFloat(yLabel[0]));
                } else {
                    yPointCircle = toY(data[i] - Float.parseFloat(yLabel[0]));
                }

                if (i == selectItem && isDrawLine(data, loopingCount, i)) {
                    paint_circle.setColor(drawColor);
                    paint_circle.setAlpha(26);
                    canvas.drawCircle(xPointLine + i * xScale, yPointCircle, dipToPx(6f), paint_circle);
                    paint_circle.setColor(drawColor);
                    canvas.drawCircle(xPointLine + i * xScale, yPointCircle, dipToPx(3.6f), paint_circle);
                    canvas.drawCircle(xPointLine + i * xScale, yPointCircle, dipToPx(2f), paint_broken);
                }
            }
        }
    }

    /**
     * 数据不完整时，显示折线逻辑判断
     *
     * @param data
     * @param loopingcont
     * @param i
     * @return
     */
    private boolean isDrawLine(Float[] data, int loopingcont, int i) {
        boolean drawPathLine = true;
        if (data[i] == 0f) {
            drawPathLine = false;
            for (int j = i; j < loopingcont; j++) {
                if (j + 1 < loopingcont && data[j + 1] != 0f) {
                    drawPathLine = true;
                    break;
                }
            }
        }
        return drawPathLine;
    }

    /**
     * 绘制柱状图
     *
     * @param canvas
     * @param drawColor
     */
    private void drawBAR(Canvas canvas, int dataIndex, int drawColor) {
        Float[] data = dataList.get(mBarChart.get(dataIndex));
        paintCurve.setStyle(Paint.Style.FILL);
        paintCurve.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        int xsize = xLabel.length;
        int dsize = data.length;
        int loopingcont = xsize > dsize ? dsize : xsize;
        float offset = barWidth / 2;
        float left;
        float top;
        float right;
        float bottom;
        for (int i = 0; i < loopingcont; i++) {
            float yPoint = toY(-Float.parseFloat(yLabel[0]));

            float startX = xPoint + xScale / 2 + i * xScale - barWidth * (mBarCount - 1) / 2;
            left = startX - offset + (mBarChart.lastIndexOf(mBarChart.get(dataIndex)) * barWidth);
            right = startX + offset + (mBarChart.lastIndexOf(mBarChart.get(dataIndex)) * barWidth);

            if (data[i] < 0) {
                top = yPoint;
                bottom = toY(data[i] - Float.parseFloat(yLabel[0])) * ratio;
            } else {
                top = toY(data[i] - Float.parseFloat(yLabel[0])) * (1 + (1 - ratio));
                bottom = yPoint;
            }

            RectF rectF = new RectF(left, top, right, bottom);
            if (i == selectItem) {
                paintCurve.setColor(orderColors[mBarChart.get(dataIndex)]);
            } else {
                paintCurve.setColor(defaultColor);
            }
            canvas.drawRect(rectF, paintCurve);
        }
    }

    /**
     * 数据按比例转坐标
     */
    private float toY(float num) {
        float y;
        try {
            y = yPoint - num * yScale;
            if (num < 0) {
                y = Math.abs(y);
            }
        } catch (Exception e) {
            return 0;
        }
        return y;
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
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                onActionUpEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                onActionUpEvent(event);
                break;
            case MotionEvent.ACTION_CANCEL:
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
                listener.onPointClick(selectItem);
            }
            invalidate();
        }
    }

    /**
     * 是否是有效的触摸范围
     *
     * @param x
     * @param y
     * @return
     */
    private boolean validateTouch(float x, float y) {
        //曲线触摸区域
        int size = xPoints.size();
        for (int i = 0; i < size; i++) {
            if (x > (xPoints.get(i) - xScale / 2) && x < (xPoints.get(i) + xScale / 2)) {
                if (selectItem != i && listener != null) {
                    selectItem = i;
                    listener.onPointClick(selectItem);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * dip 转换成px
     *
     * @param dip
     * @return
     */
    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    private PointClickListener listener;

    public void setPointClickListener(PointClickListener listener) {
        this.listener = listener;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (dataList.size() > 0) {
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


    /**
     * 设置默认颜色
     *
     * @param defaultColor
     */
    public void setDefaultColor(@ColorInt int defaultColor) {
        this.defaultColor = defaultColor;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        ratio = (float) animation.getAnimatedValue();
        postInvalidate();
    }

    public interface PointClickListener {
        void onPointClick(int index);
    }

    public void setDefaultMargin(int defaultMargin) {
        this.margin = defaultMargin;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getBarSelectColor() {
        return barSelectColor;
    }

    public void setBarSelectColor(int barSelectColor) {
        this.barSelectColor = barSelectColor;
    }

    /**
     * 设置图标模式
     *
     * @param chartStyle
     */
    public void setCharStyle(List<String> chartStyle) {
        mChartStyle = chartStyle;
    }

    /**
     * 设置条状图之间间隔
     *
     * @param barChartInterval
     */
    public void setBarChartInterval(float barChartInterval) {
        this.mBarChartInterval = barChartInterval;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setXLabel(String[] xLabel) {
        this.xLabel = xLabel;
    }

    public void setYLabel(String[] yLabel) {
        this.yLabel = yLabel;
    }

    public int setDataList(@NonNull List<Float[]> dataList) {
        this.dataList = dataList;
        if (dataList.size() > 0) {
            selectItem = dataList.get(0).length - 1;
        }
        return selectItem;
    }

    public void setSelectItem(int index) {
        selectItem = index;
    }

    public void setColorList(int[] colorList) {
        this.colorList = colorList;
    }
}
