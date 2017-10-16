package com.intfocus.yhdev.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.SimpleArrayMap;
import android.view.View;

import com.intfocus.yhdev.constant.Colors;
import com.intfocus.yhdev.util.DisplayUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zbaoliang on 17-5-21.
 */
public class TableValueView extends View {

    private Paint deviderPaint;
    private Paint textPaint;
    private int[] colors = Colors.INSTANCE.getColorsRGY();

    public int deviderHeight = 1;
    public int textSize = 14;
    public int textColor = 0x73737373;
    public int deviderColor = 0x73737373;

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public void setHeaderLenghts(ArrayList<Integer> headerLenghts) {
        this.headerLenghts.clear();
        this.headerLenghts.addAll(headerLenghts);
    }

    public void setTableValues(ArrayMap<Integer, String[]> lables) {
        this.tableValues.clear();
        tableValues.putAll((SimpleArrayMap<? extends Integer, ? extends String[]>) lables);
    }

    public void setDeviderHeight(int deviderHeight) {
        this.deviderHeight = deviderHeight;
        deviderPaint.setStrokeWidth(deviderHeight);
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        textPaint.setColor(textColor);
        invalidate();
    }

    public void setDeviderColor(int deviderColor) {
        this.deviderColor = deviderColor;
        deviderPaint.setColor(deviderColor);
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
        invalidate();
    }

    public TableValueView(Context context) {
        super(context);
        init();
    }

    private void init() {
        deviderPaint = new Paint();
        deviderPaint.setStyle(Paint.Style.FILL);
        deviderPaint.setAntiAlias(true);
        deviderPaint.setDither(true);
        deviderPaint.setColor(deviderColor);
        deviderPaint.setStrokeWidth(1f);

        textPaint = new Paint(deviderPaint);
        textPaint.setTextSize(DisplayUtil.sp2px(getContext(), textSize));
        textPaint.setTextAlign(Paint.Align.RIGHT);
//        textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        textPaint.setColor(textColor);
        textPaint.setStrokeWidth(8);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int totalWidth = 0;
        for (Integer headerLenght : headerLenghts) {
            totalWidth += headerLenght;
        }
        setMeasuredDimension(totalWidth, tableValues.size() * itemHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initXYAxesTVCenter();
    }

    /**
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            canvas.drawColor(Color.WHITE);
            int nlSize = tableValues.size();
            float endX = getWidth();
            for (int i = 0; i < nlSize; i++) {
                float starty = itemHeight * (i + deviderHeight / 2) + (i + deviderHeight / 2);
                canvas.drawLine(0, starty, endX, starty, deviderPaint);
            }

            for (int i = 0; i < nlSize; i++) {
                String[] tabrowValues = tableValues.get(i);
                int headerSize = headerLenghts.size();
                Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
                float y = (int) (YAxesCenterPopint.get(i) - fontMetrics.top / 2 - fontMetrics.bottom / 2);
                for (int n = 0; n < headerSize; n++) {
                    float x = XAxesCenterPopint.get(n);
                    JSONObject rowData = new JSONObject(tabrowValues[n + 1]);
                    if (!rowData.getString("color").equals("-1")) {
                        textPaint.setColor(colors[Integer.parseInt(rowData.getString("color"))]);
                    }
                    String value = rowData.getString("value");
                    if (value.contains(".") && !value.contains("%")) {
                        value = (value + "00").substring(0, value.indexOf(".") + 3);
                    } else if (value.contains("%")) {
                        value.replace("%", "");
                        value = (value + "00").substring(0, value.indexOf(".") + 3) + "%";
                    }
                    canvas.drawText(value, x, y, textPaint);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int itemHeight;
    public ArrayList<Integer> headerLenghts = new ArrayList<>();
    public ArrayMap<Integer, String[]> tableValues = new ArrayMap<>();

    /**
     * X轴文本中心点
     */
    private ArrayMap<Integer, Float> XAxesCenterPopint = new ArrayMap<>();

    /**
     * Y轴文本中心点
     */
    private ArrayMap<Integer, Float> YAxesCenterPopint = new ArrayMap<>();

    /**
     * 计算XY轴中心点坐标
     */
    private void initXYAxesTVCenter() {
        XAxesCenterPopint.clear();
        YAxesCenterPopint.clear();

        //TODO 计算文本对应的X轴中心点
        int hlSize = headerLenghts.size();
        float upxPointLs = 0;
        for (int i = 0; i < hlSize; i++) {
            float width = upxPointLs + headerLenghts.get(i);

            float centerPwidth;
//            float currValue = (float) (headerLenghts.get(i) / 2);
//            if (i == 0)
//                centerPwidth = currValue;
//            else
//                centerPwidth = width - currValue;
            centerPwidth = width - DisplayUtil.dip2px(getContext(), 10);
            XAxesCenterPopint.put(i, centerPwidth);

            upxPointLs = width;
        }

        //TODO 计算文本对应的Y轴中心点
        int nlSize = tableValues.size();
        float itemHeight2 = (float) (itemHeight / 2);
        for (int i = 0; i < nlSize; i++) {
            float height;
            height = itemHeight * (i + 1);
            float centerPHeight = height - itemHeight2;
            YAxesCenterPopint.put(i, centerPHeight);
        }
    }
}
