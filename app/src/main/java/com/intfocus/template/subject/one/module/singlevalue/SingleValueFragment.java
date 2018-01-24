package com.intfocus.template.subject.one.module.singlevalue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intfocus.template.R;
import com.intfocus.template.subject.one.entity.SingleValue;
import com.intfocus.template.subject.templateone.singlevalue.SingleValueContract;
import com.intfocus.template.subject.templateone.singlevalue.SingleValueImpl;
import com.intfocus.template.ui.BaseFragment;
import com.intfocus.template.ui.view.RateCursor;

import org.jetbrains.annotations.NotNull;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;

/**
 * 单值组件
 */
public class SingleValueFragment extends BaseFragment implements SingleValueContract.View {
    private static final String ARG_INDEX = "index";
    private static final String ARG_ROOT_ID = "rootId";
    private String mParam;
    private int showCount = 0;
    private int index;
    private int rootId;

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

    public static SingleValueFragment newInstance(int rootId, int index) {
        SingleValueFragment fragment = new SingleValueFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        args.putInt(ARG_ROOT_ID, rootId);
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
            index = getArguments().getInt(ARG_INDEX);
            rootId = getArguments().getInt(ARG_ROOT_ID);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_singlevalue, container, false);
            x.view().inject(this, rootView);
            mPresenter.loadData(rootId, index);
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
    public void showData(@NotNull SingleValue data) {
        coCursor = getResources().getIntArray(R.array.co_cursor);
        DecimalFormat df = new DecimalFormat("###,###.##");
        int state = data.getState().getColor() % coCursor.length;
        int color = coCursor[state];

        tvD1name.setText(data.getMain_data().getName());
        tvD2name.setText(data.getSub_data().getName());
        mainValue = Float.parseFloat(data.getMain_data().getData().replace("%", ""));
        tvD1.setText(df.format(mainValue));
        float subData = Float.parseFloat(data.getSub_data().getData().replace("%", ""));
        tvD2.setText(df.format(subData));

        tvD1.setTextColor(color);
        tvRate.setTextColor(color);
        float rate = (mainValue - subData) / subData;
        float diff = mainValue - subData;
        diffValue = df.format(diff);
        diffRate = new DecimalFormat(".##%").format(rate);
        tvRate.setText(mainValue + "");

        rateCursor.setCursorState(data.getState().getColor(), true);
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
