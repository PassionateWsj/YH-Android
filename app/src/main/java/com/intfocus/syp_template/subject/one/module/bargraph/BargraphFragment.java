package com.intfocus.syp_template.subject.one.module.bargraph;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSON;
import com.intfocus.syp_template.R;
import com.intfocus.syp_template.subject.one.ModeImpl;
import com.intfocus.syp_template.subject.one.entity.BargraphComparator;
import com.intfocus.syp_template.subject.one.entity.Bargraph;
import com.intfocus.syp_template.ui.BaseModeFragment;
import com.intfocus.syp_template.util.PinyinUtil;
import com.intfocus.syp_template.util.ToastUtils;
import com.intfocus.syp_template.ui.view.NotScrollListView;
import com.intfocus.syp_template.ui.view.PlusMinusChart;
import com.intfocus.syp_template.ui.view.SortCheckBox;

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
public class BargraphFragment extends BaseModeFragment implements AdapterView.OnItemClickListener {
    private static final String ARG_INDEX = "index";
    private static final String ARG_ROOT_ID = "rootId";
    private View rootView;
    private int index;
    private int rootId;

    @ViewInject(R.id.lv_MDRPUnit_PlusMinusChart)
    private NotScrollListView lv;
    private BargraptAdapter adapter;

    @ViewInject(R.id.fl_MDRPUnit_PlusMinusChart_container)
    private FrameLayout mFlContainer;

    @ViewInject(R.id.cbox_name)
    private SortCheckBox mCboxName;
    @ViewInject(R.id.cbox_percentage)
    private SortCheckBox mCboxPercentage;

    private PlusMinusChart pmChart;

    private String mParam;
    private Bargraph entityData;
    private LinkedList<BargraphComparator> mLtData;
    private BargraphNameComparator nameComparator;
    private BargraphDataComparator dataComparator;

    public static BargraphFragment newInstance(int rootId, int index) {
        BargraphFragment fragment = new BargraphFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_mdrpunit_plus_minus_chart, container, false);
            x.view().inject(this, rootView);
            mParam = ModeImpl.getInstance().queryModuleConfig(index, rootId);
            initView();
            bindData();
        }
        return rootView;
    }

    private void initView() {
        mLtData = new LinkedList<>();
        nameComparator = new BargraphNameComparator();
        dataComparator = new BargraphDataComparator();

        adapter = new BargraptAdapter(ctx);
        lv.setAdapter(adapter);
        lv.setFocusable(false);
        lv.setOnItemClickListener(this);
    }

    @Event({R.id.cbox_name, R.id.cbox_percentage})
    private void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.cbox_name:
                mCboxPercentage.reset();
                if (mCboxName.getCheckedState() == SortCheckBox.CheckedState.sort_noneicon) {
                    Collections.sort(mLtData, nameComparator);
                } else {
                    Collections.reverse(mLtData);
                }
                break;

            case R.id.cbox_percentage:
                mCboxName.reset();
                if (mCboxPercentage.getCheckedState() == SortCheckBox.CheckedState.sort_noneicon) {
                    Collections.sort(mLtData, dataComparator);
                } else {
                    Collections.reverse(mLtData);
                }
                break;
            default:
                break;
        }
        adapter.updateData(mLtData);
//        ArrayList<String> chartData = new ArrayList<>();
//        for (BargraphComparator bargraphComparator : mLtData) {
//            chartData.add(bargraphComparator.data);
//        }
        pmChart.updateData(mLtData);
    }

    private void bindData() {
//        mLtData.clear();
        entityData = JSON.parseObject(mParam, Bargraph.class);
        String[] dataName = entityData.xAxis.data;
        ArrayList<Bargraph.Series.Data> dataValue = entityData.series.data;
        for (int i = 0; i < dataName.length; i++) {
            String name = dataName[i];
            String value = dataValue.get(i).value;
            int color = dataValue.get(i).color;
            mLtData.add(new BargraphComparator(name, value, color));
        }


        mCboxPercentage.setText(entityData.series.name);
        mCboxName.setText(entityData.xAxis.name);
//        LinkedList<BargraphComparator> lvdata = new LinkedList<>();
//        lvdata.addAll(mLtData);
        adapter.updateData(mLtData);

        //设置图表数据
        pmChart = new PlusMinusChart(ctx);
        pmChart.setDrawingCacheEnabled(true);
        pmChart.setDefauteolor(ContextCompat.getColor(ctx,R.color.co9));
        pmChart.setDataValues(mLtData);
        mFlContainer.addView(pmChart);
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
            return str1.compareTo(str2);
        }
    }
}