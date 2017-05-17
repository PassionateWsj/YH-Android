package com.intfocus.yonghuitest.kpi.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.base.BaseFragment;
import com.intfocus.yonghuitest.base.BaseTableFragment;
import com.intfocus.yonghuitest.kpi.entity.MererEntity;
import com.intfocus.yonghuitest.kpi.entity.MeterClickEventEntity;
import com.intfocus.yonghuitest.kpi.view.MeterCursor;
import com.jonas.jgraph.graph.NChart;
import com.jonas.jgraph.models.NExcel;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 折线图
 */
public class BrokenLineFragment extends BaseTableFragment {
    private MererEntity entity;

    private View rootView;

    @ViewInject(R.id.ll_ragment_broken)
    private View ll_ragmentlayout;

    @ViewInject(R.id.tv_title_vpitem)
    private TextView tv_title;
    @ViewInject(R.id.tv_number_vpitem)
    private TextView tv_number;
    @ViewInject(R.id.tv_compare_vpitem)
    private TextView tv_compare;
    @ViewInject(R.id.tv_vpitem_unit)
    private TextView tv_unit;

    @ViewInject(R.id.img_vpitem)
    private MeterCursor img_cursor;

    @ViewInject(R.id.nchart_linefragment)
    private NChart mChart;

    DecimalFormat df = new DecimalFormat("#.00");

    public BrokenLineFragment() {
    }

    public static BrokenLineFragment newInstance(MererEntity entity) {
        BrokenLineFragment fragment = new BrokenLineFragment();
        Bundle args = new Bundle();
        args.putSerializable("Entity", entity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            entity = (MererEntity) getArguments().getSerializable("Entity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_brokenline, container, false);
            x.view().inject(this, rootView);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    init();
                }
            }, 100);
        }
        return rootView;
    }

    private void init() {
        if (entity.is_stick) {
            mChart.setInterceptAnimate(true);
//            rootView.post(new Runnable() {
//                @Override
//                public void run() {
//                    int vpWidth = rootView.getWidth();
//                    int height = vpWidth*2;
//                    ViewGroup.LayoutParams lp = ll_ragmentlayout.getLayoutParams();
//                    lp.height = height;
//                    ll_ragmentlayout.setLayoutParams(lp);
//                }
//            });
        } else {

        }


        tv_title.setText(entity.title);
        double number = entity.data.high_light.number;
        if (number==0)
            tv_number.setText("0");
        else
            tv_number.setText(String.valueOf(df.format(number)));
        tv_unit.setText(entity.unit);


        MererEntity.LineEntity.HighLight high_light = entity.data.high_light;
        if (high_light.percentage) {//显示百分比
            float compare = (float) (high_light.number / high_light.compare * 100);
            if (high_light.number-high_light.compare>0) {//上箭头
                tv_compare.setText("+" + df.format(compare) + "%");
            } else {
                tv_compare.setText("-" + df.format(compare) + "%");
            }
        }

        if (high_light.arrow >= 0)
            img_cursor.setVisibility(View.VISIBLE);
        else
            img_cursor.setVisibility(View.GONE);
        img_cursor.setCursorState(high_light.arrow);

        int[] chart_data = entity.data.chart_data;
        int size = chart_data.length;

        if (size > 0) {
            List<NExcel> nExcelList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                nExcelList.add(new NExcel(chart_data[i], ""));
            }
            mChart.setScrollAble(false);
            mChart.setFixedWidth(7);
            mChart.setNormalColor(Color.parseColor("#4EB5D7"));
            mChart.setTextPrintColor(Color.parseColor("#4EB5D7"));
            mChart.setDrawTextUnit(entity.unit);
            mChart.setBarStanded(7);
            mChart.setBarAniStyle(NChart.ChartAniStyle.BAR_UP);//TODO 动画效果
            mChart.setChartStyle(NChart.ChartStyle.CLOSELINE);//TODO 曲线图
            mChart.setLinePointRadio(16);//设置折线转则点圆点半径
            mChart.setHCoordinate(10);
            mChart.setAbove(10);
            mChart.setInterceptTouchEvent(true);
            mChart.cmdFill(nExcelList);
        }

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MeterClickEventEntity(entity));
            }
        });

    }
}
