package com.intfocus.yhdev.subject.template_v1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.intfocus.yhdev.R;
import com.intfocus.yhdev.base.BaseModeFragment;
import com.intfocus.yhdev.subject.template_v1.entity.MDRPUnitCurveChartEntity;
import com.intfocus.yhdev.subject.template_v1.entity.MDRPUnitSeries;
import com.intfocus.yhdev.subject.template_v1.mode.MDRPUnitCurveChartMode;
import com.intfocus.yhdev.view.CustomCurveChartV2;
import com.intfocus.yhdev.view.RateCursor;
import com.zbl.lib.baseframe.core.Subject;
import com.zbl.lib.baseframe.utils.StringUtil;

import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 仪表盘-详情页面-根页签-曲线图单元
 */
public class ModularTwo_UnitCurveChartModeFragment extends BaseModeFragment<MDRPUnitCurveChartMode> implements CustomCurveChartV2.PointClickListener {
    private static final String ARG_PARAM1 = "param1";
    public static String mCurrentParam;
    private String targetID;
    private String mParam1;
    private View rootView;

    @ViewInject(R.id.ll_mdrpUnit_curvechart)
    private LinearLayout ll_curvechart;
    private String[] xLabel;
    private String[] yLabel;
    private ArrayList<Float[]> seriesLables;
    private int[] color;
    private MDRPUnitCurveChartEntity curveChartEntity;

    @ViewInject(R.id.tv_chart_table_xlabel)
    TextView tv_xlabel;

    @ViewInject(R.id.tv_chart_table_target1)
    TextView tv_target1;
    @ViewInject(R.id.tv_chart_table_target1name)
    TextView tv_target1name;
    @ViewInject(R.id.tv_chart_table_target2)
    TextView tv_target2;
    @ViewInject(R.id.tv_chart_table_target2name)
    TextView tv_target2name;
    @ViewInject(R.id.tv_chart_table_target3name)
    TextView tv_target3name;
    @ViewInject(R.id.tv_chart_table_rate)
    TextView tv_rate;
    @ViewInject(R.id.img_RateCursor)
    RateCursor rateCursor;

    DecimalFormat df = new DecimalFormat(",###.##");
    DecimalFormat df_rate = new DecimalFormat("#.##%");

    int[] coGroup;
    int[] coCursor;
    private String chartType;
    private CustomCurveChartV2 chart;

    @Override
    public Subject setSubject() {
        targetID = getTag();
        return new MDRPUnitCurveChartMode(ctx, targetID);
    }

    public static ModularTwo_UnitCurveChartModeFragment newInstance(String param1) {
        ModularTwo_UnitCurveChartModeFragment fragment = new ModularTwo_UnitCurveChartModeFragment();
        mCurrentParam = param1;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParam1 = mCurrentParam;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_mdunit_curve_chart, container, false);
            x.view().inject(this, rootView);
            coGroup = getResources().getIntArray(R.array.co_order);
            coCursor = getResources().getIntArray(R.array.co_cursor);
            getModel().analysisData(mParam1);
        }
        return rootView;
    }

    /**
     * 图表点击事件统一处理方法
     */
    public void onMessageEvent(final MDRPUnitCurveChartEntity entity) {
        if (entity != null && entity.stateCode == 200) {
            dealData(entity);
        }
    }

    /**
     * 绑定数据
     */
    private void dealData(MDRPUnitCurveChartEntity result) {
        this.curveChartEntity = result;
        xLabel = result.xAxis;
        yLabel = new String[5];
        int YMaxValue;
        seriesLables = new ArrayList<>();
        ArrayList<Float> seriesA = new ArrayList<>();

        ArrayList<MDRPUnitCurveChartEntity.SeriesEntity> arrays = result.series;
        for (MDRPUnitCurveChartEntity.SeriesEntity array : arrays) {
            String datas = array.data;
            chartType = array.type;
            if (datas.contains("{")) {
                ArrayList<MDRPUnitSeries> list = (ArrayList<MDRPUnitSeries>) JSON.parseArray(datas, MDRPUnitSeries.class);
                color = new int[list.size()];
                Float[] lables = new Float[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    MDRPUnitSeries seriesEntity = list.get(i);
                    color[i] = seriesEntity.color;
                    Float lableV = seriesEntity.value;
                    lables[i] = lableV;
                    seriesA.add(lableV);
                }
                seriesLables.add(lables);
            } else {
                datas = datas.trim().substring(1, datas.length() - 1).trim();
                String[] topW = datas.trim().split(",");
                int dataLength = topW.length;
                Float[] lables = new Float[dataLength];
                for (int i = 0; i < dataLength; i++) {
                    String strValue = topW[i].trim();
                    strValue = strValue.replace("\"", "");
                    if (StringUtil.isEmpty(strValue)) {
                        strValue = "0";
                    }
                    Float lableV = Float.valueOf(strValue);
                    lables[i] = lableV;
                    seriesA.add(lableV);
                }
                seriesLables.add(lables);
            }
        }

        Collections.sort(seriesA);
        YMaxValue = seriesA.get(seriesA.size() - 1).intValue();
        while (YMaxValue % 4 != 0) {
            YMaxValue++;
        }

        int part = YMaxValue / 4;
        for (int i = 0; i < 5; i++) {
            yLabel[i] = String.valueOf(part * i);
        }
        act.runOnUiThread(new UIRunnable());

        Log.d("TAG", seriesA.get(0) + ":" + seriesA.get(seriesA.size() - 1));
    }

    class UIRunnable implements Runnable {

        @Override
        public void run() {
            try {
                float margin = getResources().getDimension(R.dimen.space_default);
                org.json.JSONArray array = new org.json.JSONArray(curveChartEntity.yAxis);
                JSONObject jsonObject = array.getJSONObject(0);
                String unit = jsonObject.getString("name");
                chart = new CustomCurveChartV2(act);
                chart.setBarWidth(40);
                chart.setxLabel(xLabel);
                chart.setyLabel(yLabel);
                chart.setUnit(unit);
                chart.setColorList(color);
                int selectItem = chart.setDataList(seriesLables);
                chart.setDefauteolor(getResources().getColor(R.color.co9));
                chart.setDefauteMargin((int) margin);
                chart.setPointClickListener(ModularTwo_UnitCurveChartModeFragment.this);
                int chartStytle;
                switch (chartType) {
                    case "line":
                        chartStytle = CustomCurveChartV2.ChartStyle.LINE;
                        break;
                    case "bar":
                        chartStytle = CustomCurveChartV2.ChartStyle.BAR;
                        break;
                    default:
                        chartStytle = CustomCurveChartV2.ChartStyle.LINE;
                }
                chart.setCharStytle(chartStytle);
                onPointClick(selectItem);
                ll_curvechart.addView(chart);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPointClick(int index) {
            String xlabel = xLabel[index];
            String name1 = curveChartEntity.legend[0];
            Float[] values1 = seriesLables.get(0);
            Float target1 = 0f;
            if (values1.length > index) {
                target1 = values1[index];
            }
            tv_xlabel.setText(xlabel);

            if (0.0f == target1) {
                tv_target1.setTextColor(0x73737373);
                tv_target1.setText("暂无数据");
                tv_target1name.setText(name1);
            }
            else {
                tv_target1.setTextColor(coGroup[0]);
                tv_target1.setText(df.format(target1));
                tv_target1name.setText(name1);
            }


            if (seriesLables.size() > 1) {
                String name2 = curveChartEntity.legend[1];
                Float[] values2 = seriesLables.get(1);
                Float target2 = 0f;

                if (values2.length > index) {
                    target2 = values2[index];
                }

                tv_target2.setText(df.format(target2));
                tv_target2.setTextColor(coGroup[1]);
                tv_target2name.setText(name2);

                if (seriesLables.size() == 2) {
                    int baseColor;
                    int cursorIndex = -1;
                    int colorSize = color.length;
                    if (index < colorSize) {
                        switch (color[index]) {
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
                    }

                    String strRate;
                    float rate = 0;
                    if (target1 == 0 || target2 == 0) {
                        strRate = "暂无数据";
                        cursorIndex = -1;
                    } else {
                        rate = (target1 - target2) / target2;
                        strRate = df_rate.format(rate);
                    }

                    boolean isPlus;
                    if (rate > 0) {
                        isPlus = true;
                    } else {
                        isPlus = false;
                    }

                    if (cursorIndex == -1) {
                        baseColor = 0x73737373;
                    } else {
                        baseColor = coCursor[cursorIndex];
                    }

                    tv_rate.setTextColor(baseColor);
                    rateCursor.setCursorState(cursorIndex, !isPlus);

                    tv_rate.setText(strRate);
                    chart.setBarSelectColor(baseColor);
                    tv_target3name.setText("变化率");
                }
            }

            if (seriesLables.size() > 2) {
                String name3 = curveChartEntity.legend[2];
                Float[] values3 = seriesLables.get(2);
                Float target3 = 0f;
                if (values3.length > index) {
                    target3 = values3[index];
                }
                tv_rate.setText(df.format(target3));
                tv_rate.setTextColor(coGroup[1]);
                tv_target3name.setText(name3);
            }
    }
}

