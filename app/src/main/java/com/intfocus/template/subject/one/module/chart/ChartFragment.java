package com.intfocus.template.subject.one.module.chart;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.intfocus.template.R;
import com.intfocus.template.subject.one.entity.Chart;
import com.intfocus.template.subject.one.entity.Series;
import com.intfocus.template.subject.templateone.curvechart.ChartContract;
import com.intfocus.template.subject.templateone.curvechart.ChartImpl;
import com.intfocus.template.ui.view.CustomCurveChart;
import com.intfocus.template.ui.view.RateCursor;
import com.intfocus.template.util.LogUtil;
import com.zbl.lib.baseframe.utils.StringUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 仪表盘-详情页面-根页签-曲线图单元
 */
public class ChartFragment extends Fragment implements CustomCurveChart.PointClickListener, ChartContract.View {
    private static final String ARG_INDEX = "index";
    private static final String ARG_ROOT_ID = "rootId";

    private Context ctx;
    private View rootView;
    private int index;
    private int rootId;

    @ViewInject(R.id.ll_mdrpUnit_curvechart)
    private LinearLayout mLlCurvechart;
    private String[] xLabel;
    private String[] yLabel;
    private ArrayList<Float[]> seriesLables;
    private int[] color;
    private Chart curveChartEntity;

    @ViewInject(R.id.tv_chart_table_xlabel)
    TextView mTvXlabel;

    @ViewInject(R.id.tv_chart_table_target1)
    TextView mTvTarget1;
    @ViewInject(R.id.tv_chart_table_target1name)
    TextView mTvTarget1Name;
    @ViewInject(R.id.tv_chart_table_target2)
    TextView mTvTarget2;
    @ViewInject(R.id.tv_chart_table_target2name)
    TextView mTvTarget2Name;
    @ViewInject(R.id.tv_chart_table_target3name)
    TextView mTvTarget3Name;
    @ViewInject(R.id.tv_chart_table_rate)
    TextView mTvRate;
    @ViewInject(R.id.img_RateCursor)
    RateCursor rateCursor;

    DecimalFormat df = new DecimalFormat(",###.##");
    DecimalFormat mDfRate = new DecimalFormat("#.##%");

    int[] coGroup;
    int[] coCursor;
    private List<String> chartType;
    private CustomCurveChart chart;
    private int YCOORDINATEVALENUM;
    private ChartContract.Presenter mPresenter;

    public static ChartFragment newInstance(int rootId, int index) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        args.putInt(ARG_ROOT_ID, rootId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_INDEX);
            rootId = getArguments().getInt(ARG_ROOT_ID);
        }
        ctx = getContext();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ChartImpl.destroyInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_mdunit_curve_chart, container, false);
            x.view().inject(this, rootView);
            coGroup = getResources().getIntArray(R.array.co_order);
            coCursor = getResources().getIntArray(R.array.co_cursor);
            mPresenter.loadData(rootId, index);
        }
        return rootView;
    }

    class UIRunnable implements Runnable {
        @Override
        public void run() {
            try {
                float margin = getResources().getDimension(R.dimen.space_default);
                org.json.JSONArray array = new org.json.JSONArray(curveChartEntity.getyAxis());
                JSONObject jsonObject = array.getJSONObject(0);
                String unit = jsonObject.getString("name");
                chart = new CustomCurveChart(getActivity());
                chart.setDrawingCacheEnabled(true);
                //设置柱形图之间间隔
                chart.setBarChartInterval(0.4f);
                chart.setXLabel(xLabel);
                chart.setYLabel(yLabel);
                chart.setUnit(unit);
                chart.setColorList(color);
                int selectItem = chart.setDataList(seriesLables);
                chart.setDefaultColor(ContextCompat.getColor(ctx, R.color.co9));
                chart.setDefaultMargin((int) margin);
                chart.setPointClickListener(ChartFragment.this);
                chart.setCharStyle(chartType);
                onPointClick(selectItem);
                mLlCurvechart.addView(chart);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPointClick(int index) {
        String xlabel = xLabel[index];
        String name1 = curveChartEntity.getLegend()[0];
        Float[] values1 = seriesLables.get(0);
        Float target1 = 0f;
        if (values1.length > index) {
            target1 = values1[index];
        }
        mTvXlabel.setText(xlabel);

        if (0.0f == target1) {
            mTvTarget1.setTextColor(0x73737373);
            mTvTarget1.setText("暂无数据");
            mTvTarget1Name.setText(name1);
        } else {
            mTvTarget1.setTextColor(coGroup[0]);
            mTvTarget1.setText(df.format(target1));
            mTvTarget1Name.setText(name1);
        }

        if (seriesLables.size() > 1) {
            String name2 = curveChartEntity.getLegend()[1];
            Float[] values2 = seriesLables.get(1);
            Float target2 = 0f;

            if (values2.length > index) {
                target2 = values2[index];
            }

            mTvTarget2.setText(df.format(target2));
            mTvTarget2.setTextColor(coGroup[1]);
            mTvTarget2Name.setText(name2);

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
                    strRate = mDfRate.format(rate);
                }

                boolean isPlus = rate > 0;

                if (cursorIndex == -1) {
                    baseColor = 0x73737373;
                } else {
                    baseColor = coCursor[cursorIndex];
                }

                mTvRate.setTextColor(baseColor);
                rateCursor.setCursorState(cursorIndex, !isPlus);

                mTvRate.setText(strRate);
                chart.setBarSelectColor(baseColor);
                mTvTarget3Name.setText("变化率");
            } else if (seriesLables.size() > 2) {
                String name3 = curveChartEntity.getLegend()[2];
                Float[] values3 = seriesLables.get(2);
                Float target3 = 0f;
                if (values3.length > index) {
                    target3 = values3[index];
                }
                mTvRate.setText(df.format(target3));
                mTvRate.setTextColor(coGroup[2]);
                mTvTarget3Name.setText(name3);
            }
        } else {
            mTvTarget2Name.setVisibility(View.GONE);
            mTvTarget3Name.setVisibility(View.GONE);
            mTvRate.setVisibility(View.GONE);
            mTvTarget2.setVisibility(View.GONE);
        }


    }


    // ----------------------------------------------------------------
    // -------------------------- 重构代码起始 --------------------------
    // ----------------------------------------------------------------

    @Override
    public ChartContract.Presenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void setPresenter(ChartContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showData(@NotNull Chart result) {
        this.curveChartEntity = result;
        xLabel = result.getxAxis();
        yLabel = new String[5];
        int yMaxValue;
        int yMinValue;
        int yIntervalValue;
        seriesLables = new ArrayList<>();
        ArrayList<Float> seriesA = new ArrayList<>();
        chartType = new ArrayList<>();

        ArrayList<Chart.SeriesEntity> arrays = result.getSeries();
        for (Chart.SeriesEntity array : arrays) {
            String datas = array.getData();
            chartType.add(array.getType());
            if (datas.contains("{")) {
                ArrayList<Series> list = (ArrayList<Series>) JSON.parseArray(datas, Series.class);
                color = new int[list.size()];
                Float[] lables = new Float[list.size()];
                int dataSize = list.size();
                for (int i = 0; i < dataSize; i++) {
                    Series seriesEntity = list.get(i);
                    color[i] = seriesEntity.getColor();
                    Float lableV = seriesEntity.getValue();
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
        yMaxValue = seriesA.get(seriesA.size() - 1).intValue();
        yMinValue = seriesA.get(0).intValue();
        if (yMinValue > 0) {
            yMinValue = 0;
        }
        yIntervalValue = Math.abs(yMaxValue - yMinValue);
        YCOORDINATEVALENUM = 4;
        while (yIntervalValue % YCOORDINATEVALENUM != 0) {
            yIntervalValue++;
        }

        int part = yIntervalValue / 4;
        for (int i = 0; i < 5; i++) {
            yLabel[i] = String.valueOf(yMinValue + part * i);
        }
        getActivity().runOnUiThread(new UIRunnable());
        LogUtil.d("TAG", seriesA.get(0) + ":" + seriesA.get(seriesA.size() - 1));
    }

    // ----------------------------------------------------------------
    // ----------------------- 我是 到此为止分割线 ------------------------
    // ----------------------------------------------------------------

}

