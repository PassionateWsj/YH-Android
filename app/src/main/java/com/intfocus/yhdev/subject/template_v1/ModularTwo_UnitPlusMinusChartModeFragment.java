package com.intfocus.yhdev.subject.template_v1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSON;
import com.intfocus.yhdev.R;
import com.intfocus.yhdev.base.BaseModeFragment;
import com.intfocus.yhdev.subject.template_v1.adapter.BargraptAdapter;
import com.intfocus.yhdev.subject.template_v1.entity.BargraphComparator;
import com.intfocus.yhdev.subject.template_v1.entity.MDRPUnitBargraph;
import com.intfocus.yhdev.util.BargraphDataComparator;
import com.intfocus.yhdev.util.PinyinUtil;
import com.intfocus.yhdev.util.ToastUtils;
import com.intfocus.yhdev.view.NotScrollListView;
import com.intfocus.yhdev.view.PlusMinusChart;
import com.intfocus.yhdev.view.SortCheckBox;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * 正负图表模块
 */
public class ModularTwo_UnitPlusMinusChartModeFragment extends BaseModeFragment implements AdapterView.OnItemClickListener {
    private static final String ARG_PARAM = "param1";
    private View rootView;

    @ViewInject(R.id.lv_MDRPUnit_PlusMinusChart)
    private NotScrollListView lv;
    private BargraptAdapter adapter;

    @ViewInject(R.id.fl_MDRPUnit_PlusMinusChart_container)
    private FrameLayout fl_container;

    @ViewInject(R.id.cbox_name)
    private SortCheckBox cbox_name;
    @ViewInject(R.id.cbox_percentage)
    private SortCheckBox cbox_percentage;

    private PlusMinusChart pmChart;

    private String mParam;
    private MDRPUnitBargraph entityData;
    private LinkedList<BargraphComparator> lt_data;
    private BargraphNameComparator nameComparator;
    private BargraphDataComparator dataComparator;

    public static ModularTwo_UnitPlusMinusChartModeFragment newInstance(String param) {
        ModularTwo_UnitPlusMinusChartModeFragment fragment = new ModularTwo_UnitPlusMinusChartModeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_mdrpunit_plus_minus_chart, container, false);
            x.view().inject(this, rootView);
            initView();
            bindData();
        }
        return rootView;
    }

    private void initView() {
        lt_data = new LinkedList<>();
        nameComparator = new BargraphNameComparator();
        dataComparator = new BargraphDataComparator();

        adapter = new BargraptAdapter(ctx, null);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
    }

    @Event({R.id.cbox_name, R.id.cbox_percentage})
    private void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.cbox_name:
                cbox_percentage.reset();
                if (cbox_name.getCheckedState() == SortCheckBox.CheckedState.sort_noneicon) {
                    Collections.sort(lt_data, nameComparator);
                } else {
                    Collections.reverse(lt_data);
                }
                break;

            case R.id.cbox_percentage:
                cbox_name.reset();
                if (cbox_percentage.getCheckedState() == SortCheckBox.CheckedState.sort_noneicon) {
                    Collections.sort(lt_data, dataComparator);
                } else {
                    Collections.reverse(lt_data);
                }
                break;
        }
        adapter.updateData(lt_data);
        ArrayList<String> chartData = new ArrayList<>();
        for (BargraphComparator bargraphComparator : lt_data) {
            chartData.add(bargraphComparator.data);
        }
        pmChart.updateData(lt_data);
    }

    private void bindData() {
        lt_data.clear();
        entityData = JSON.parseObject(mParam, MDRPUnitBargraph.class);
        String[] data_name = entityData.xAxis.data;
        ArrayList<MDRPUnitBargraph.Series.Data> data_value = entityData.series.data;
        for (int i = 0; i < data_name.length; i++) {
            String name = data_name[i];
            String value = data_value.get(i).value;
            int color = data_value.get(i).color;
            lt_data.add(new BargraphComparator(name, value, color));
        }


        cbox_percentage.setText(entityData.series.name);
        cbox_name.setText(entityData.xAxis.name);
//        LinkedList<BargraphComparator> lvdata = new LinkedList<>();
//        lvdata.addAll(lt_data);
        adapter.updateData(lt_data);

        //设置图表数据
        pmChart = new PlusMinusChart(ctx);
        pmChart.setDefauteolor(getResources().getColor(R.color.co9));
        pmChart.setDataValues(lt_data);
        fl_container.addView(pmChart);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adapter.setSelectItem(position);
        String xValue = entityData.xAxis.data[position];
        ToastUtils.INSTANCE.show(ctx, xValue);
    }

    class BargraphNameComparator implements Comparator<BargraphComparator> {

        @Override
        public int compare(BargraphComparator o1, BargraphComparator o2) {
            String str1 = PinyinUtil.getPingYin(o1.name);
            String str2 = PinyinUtil.getPingYin(o2.name);
            int flag = str1.compareTo(str2);
            return flag;
        }
    }

}
