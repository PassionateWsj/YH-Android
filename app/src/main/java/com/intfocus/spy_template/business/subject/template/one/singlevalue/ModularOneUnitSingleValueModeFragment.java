package com.intfocus.spy_template.business.subject.template.one.singlevalue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intfocus.spy_template.R;
import com.intfocus.spy_template.general.base.BaseModeFragment;
import com.intfocus.spy_template.general.view.RateCursor;
import com.intfocus.spy_template.business.subject.template.one.entity.MDRPUnitSingleValue;
import com.intfocus.spy_template.business.subject.templateone.singlevalue.SingleValueContract;
import com.intfocus.spy_template.business.subject.templateone.singlevalue.SingleValueImpl;

import org.jetbrains.annotations.NotNull;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;

/**
 * 单值组件
 */
public class ModularOneUnitSingleValueModeFragment extends BaseModeFragment implements SingleValueContract.View {
    @NonNull
    private static final String ARG_PARAM = "SingleValueParam";
    private String mParam;
    private int showCount = 0;

    private View rootView;

    @ViewInject(R.id.tv_singlevalue_d1)
    private TextView tvD1;
    @ViewInject(R.id.tv_singlevalue_d1name)
    private TextView tvD1name;
    @ViewInject(R.id.tv_singlevalue_d2)
    private TextView tvD2;
    @ViewInject(R.id.tv_singlevalue_d2name)
    private TextView tvD2name;
    @ViewInject(R.id.tv_singlevalue_ratio)
    private TextView tvRate;

    @ViewInject(R.id.img_singlevalue_ratiocursor)
    RateCursor rateCursor;

    int[] coCursor;

    private String diffValue;
    private String diffRate;
    private float mainValue;
    private boolean isSwitch;
    private SingleValueContract.Presenter mPresenter;

    public static ModularOneUnitSingleValueModeFragment newInstance(String param) {
        ModularOneUnitSingleValueModeFragment fragment = new ModularOneUnitSingleValueModeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setPresenter(@NonNull SingleValueContract.Presenter presenter) {
        mPresenter = presenter;
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
            mPresenter.loadData(mParam);
        }
        return rootView;
    }

    @Event(R.id.tv_singlevalue_ratio)
    private void onViewClick(View view) {
        switch (showCount) {
            case 0:
                tvRate.setText(diffValue);
                break;

            case 1:
                tvRate.setText(diffRate);
                break;

            case 2:
                tvRate.setText(mainValue + "");
                showCount = -1;
                break;

            default:
                tvRate.setText(mainValue + "");
                break;
        }

        showCount++;
    }

    @Override
    public void showData(@NotNull MDRPUnitSingleValue data) {
        coCursor = getResources().getIntArray(R.array.co_cursor);
        DecimalFormat df = new DecimalFormat("###,###.##");
        int state = data.state.color;
        int color = coCursor[state];

        tvD1name.setText(data.main_data.name);
        tvD2name.setText(data.sub_data.name);
        mainValue = Float.parseFloat(data.main_data.data.replace("%", ""));
        tvD1.setText(df.format(mainValue));
        float subData = Float.parseFloat(data.sub_data.data.replace("%", ""));
        tvD2.setText(df.format(subData));

        tvD1.setTextColor(color);
        tvRate.setTextColor(color);
        float rate = (mainValue - subData) / subData;
        float diff = mainValue - subData;
        diffValue = df.format(diff);
        diffRate = new DecimalFormat(".##%").format(rate);
        tvRate.setText(mainValue + "");

        float absmv = Math.abs(rate);
        boolean isPlus;
        if (absmv <= 0.1f) {
            isPlus = rate <= 0;
        } else if (rate < -0.1f) {
            isPlus = false;
        } else {
            isPlus = true;
        }
        rateCursor.setCursorState(state, isPlus);
        rateCursor.setDrawingCacheEnabled(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SingleValueImpl.destroyInstance();
    }

    public void getData() {

    }

    @Override
    public SingleValueContract.Presenter getPresenter() {
        return mPresenter;
    }
}
