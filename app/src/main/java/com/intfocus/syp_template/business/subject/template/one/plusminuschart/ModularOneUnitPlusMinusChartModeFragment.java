package com.intfocus.syp_template.business.subject.template.one.plusminuschart;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.intfocus.syp_template.R;
import com.intfocus.syp_template.business.subject.template.one.adapter.BargraptAdapter;
import com.intfocus.syp_template.business.subject.template.one.entity.BargraphComparator;
import com.intfocus.syp_template.business.subject.template.one.entity.MDRPUnitBargraph;
import com.intfocus.syp_template.general.base.BaseModeFragment;
import com.intfocus.syp_template.general.bean.Report;
import com.intfocus.syp_template.general.gen.ReportDao;
import com.intfocus.syp_template.general.util.BargraphDataComparator;
import com.intfocus.syp_template.general.util.DaoUtil;
import com.intfocus.syp_template.general.util.PinyinUtil;
import com.intfocus.syp_template.general.view.NotScrollListView;
import com.intfocus.syp_template.general.view.PlusMinusChart;
import com.intfocus.syp_template.general.view.SortCheckBox;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import static com.intfocus.syp_template.constant.Params.REPORT_TYPE_PLUS_MINUS;

/**
 * 正负图表模块
 */
public class ModularOneUnitPlusMinusChartModeFragment extends BaseModeFragment implements AdapterView.OnItemClickListener, PlusMinusChart.PlusMinusOnItemClickListener {
    private static final String ARG_INDEX = "index";
    private static final String ARG_UUID = "uuid";
    private View rootView;
    private int index;
    private String uuid;

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
    private MDRPUnitBargraph entityData;
    private LinkedList<BargraphComparator> mLtData;
    private BargraphNameComparator nameComparator;
    private BargraphDataComparator dataComparator;
    private BargraphComparator mSelectItem;

    public static ModularOneUnitPlusMinusChartModeFragment newInstance(String uuid, int index) {
        ModularOneUnitPlusMinusChartModeFragment fragment = new ModularOneUnitPlusMinusChartModeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        args.putString(ARG_UUID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_INDEX);
            uuid = getArguments().getString(ARG_UUID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_mdrpunit_plus_minus_chart, container, false);
            x.view().inject(this, rootView);
            initView();
            init();
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
        pmChart.updateData(mLtData);
        adapter.setSelectItem(mLtData.indexOf(mSelectItem));
        pmChart.onClickItem(mLtData.indexOf(mSelectItem));
    }

    private void bindData() {
        entityData = JSON.parseObject(mParam, MDRPUnitBargraph.class);
        String[] dataName = entityData.xAxis.data;
        ArrayList<MDRPUnitBargraph.Series.Data> dataValue = entityData.series.data;
        for (int i = 0; i < dataName.length; i++) {
            String name = dataName[i];
            String value = dataValue.get(i).value;
            int color = dataValue.get(i).color;
            mLtData.add(new BargraphComparator(name, value, color));
        }


        mCboxPercentage.setText(entityData.series.name);
        mCboxName.setText(entityData.xAxis.name);
        adapter.updateData(mLtData);

        //设置图表数据
        pmChart = new PlusMinusChart(ctx);
        pmChart.setDrawingCacheEnabled(true);
        pmChart.setDefauteolor(ContextCompat.getColor(ctx, R.color.co9));
        pmChart.setDataValues(mLtData);
        pmChart.setPointClickListener(this);
        mFlContainer.addView(pmChart);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        pmChart.onClickItem(position);
        itemClick(position);
    }

    @Override
    public void onPointClick(int index) {
        itemClick(index);
    }

    private void itemClick(int index) {
        mSelectItem = mLtData.get(index);
        adapter.setSelectItem(index);
        String xValue = entityData.xAxis.data[index];
        Toast.makeText(ctx, xValue, Toast.LENGTH_SHORT).show();
    }

    class BargraphNameComparator implements Comparator<BargraphComparator> {

        @Override
        public int compare(BargraphComparator o1, BargraphComparator o2) {
            String str1 = PinyinUtil.getPingYin(o1.name);
            String str2 = PinyinUtil.getPingYin(o2.name);
            return str1.compareTo(str2);
        }
    }

    private void init() {
        ReportDao reportDao = DaoUtil.INSTANCE.getReportDao();
        Report report = reportDao.queryBuilder()
                .where(reportDao.queryBuilder()
                        .and(ReportDao.Properties.Uuid.eq(uuid)
                                , ReportDao.Properties.Type.eq(REPORT_TYPE_PLUS_MINUS)
                                , ReportDao.Properties.Index.eq(index)))
                .unique();

        mParam = report.getConfig();
    }
}
