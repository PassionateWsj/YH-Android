package com.intfocus.yhdev.subject.template_v1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.intfocus.yhdev.R;
import com.intfocus.yhdev.base.BaseModeFragment;
import com.intfocus.yhdev.subject.template_v1.contract.SingleValueContract;
import com.intfocus.yhdev.subject.template_v1.entity.MDRPUnitSingleValue;
import com.intfocus.yhdev.view.RateCursor;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;

/**
 * 单值组件
 */
public class ModularTwo_UnitSingleValueModeFragment extends BaseModeFragment implements SingleValueContract.View {
    private static final String ARG_PARAM = "SingleValueParam";
    private String mParam;
    private int showCount = 0;

    private View rootView;

    @ViewInject(R.id.tv_singlevalue_d1)
    private TextView tv_d1;
    @ViewInject(R.id.tv_singlevalue_d1name)
    private TextView tv_d1name;
    @ViewInject(R.id.tv_singlevalue_d2)
    private TextView tv_d2;
    @ViewInject(R.id.tv_singlevalue_d2name)
    private TextView tv_d2name;
    @ViewInject(R.id.tv_singlevalue_ratio)
    private TextView tv_rate;

    @ViewInject(R.id.img_singlevalue_ratiocursor)
    RateCursor rateCursor;

    int[] coCursor;

    private String diffValue;
    private String diffRate;
    private float mainValue;
    private boolean isSwitch;
    private SingleValueContract.Presenter mPresenter;

    public static ModularTwo_UnitSingleValueModeFragment newInstance(String param) {
        ModularTwo_UnitSingleValueModeFragment fragment = new ModularTwo_UnitSingleValueModeFragment();
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
            rootView = inflater.inflate(R.layout.fragment_singlevalue, container, false);
            x.view().inject(this, rootView);
            bindData();
//            mPresenter.loadData(mParam);
        }
        return rootView;
    }

    private void bindData() {
        coCursor = getResources().getIntArray(R.array.co_cursor);
        DecimalFormat df = new DecimalFormat("###,###.##");
        MDRPUnitSingleValue valueData = JSON.parseObject(mParam, MDRPUnitSingleValue.class);
        int state = valueData.state.color;
        int color = coCursor[state];

        tv_d1name.setText(valueData.main_data.name);
        tv_d2name.setText(valueData.sub_data.name);
        mainValue = Float.parseFloat(valueData.main_data.data.replace("%", ""));
        tv_d1.setText(df.format(mainValue));
        float subData = Float.parseFloat(valueData.sub_data.data.replace("%", ""));
        tv_d2.setText(df.format(subData));

        tv_d1.setTextColor(color);
        tv_rate.setTextColor(color);
        float rate = (mainValue - subData) / subData;
        float diff = mainValue - subData;
        diffValue = df.format(diff);
        diffRate = new DecimalFormat(".##%").format(rate);
        tv_rate.setText(mainValue + "");

        float absmv = Math.abs(rate);
        boolean isPlus;
//        int cursorIndex;
        if (absmv <= 0.1f) {
//            cursorIndex = 1;
//            if (rate > 0) {
//                isPlus = false;
//            } else {
//                isPlus = true;
//            }
            isPlus = rate <= 0;
        } else if (rate < -0.1f) {
//            cursorIndex = 0;
            isPlus = false;
        } else {
//            cursorIndex = 2;
            isPlus = true;
        }
        rateCursor.setCursorState(state, isPlus);
    }

    @Event(R.id.tv_singlevalue_ratio)
    private void onViewClick(View view) {
        switch (showCount) {
            case 0:
                tv_rate.setText(diffValue);
                break;

            case 1:
                tv_rate.setText(diffRate);
                break;

            case 2:
                tv_rate.setText(mainValue + "");
                showCount = -1;
                break;

            default:
                tv_rate.setText(mainValue + "");
                break;
        }

        showCount++;
    }

    @Override
    public void setPresenter(SingleValueContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
