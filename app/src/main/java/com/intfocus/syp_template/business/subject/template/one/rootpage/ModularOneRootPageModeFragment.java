package com.intfocus.syp_template.business.subject.template.one.rootpage;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intfocus.syp_template.R;
import com.intfocus.syp_template.business.subject.template.one.banner.ModularOneUnitBannerModeFragment;
import com.intfocus.syp_template.business.subject.template.one.curvechart.ModularOneUnitCurveChartModeFragment;
import com.intfocus.syp_template.business.subject.template.one.mode.MDetalRootPageMode;
import com.intfocus.syp_template.business.subject.template.one.plusminuschart.ModularOneUnitPlusMinusChartModeFragment;
import com.intfocus.syp_template.business.subject.template.one.singlevalue.ModularOneUnitSingleValueModeFragment;
import com.intfocus.syp_template.business.subject.template.one.table.ModularOneUnitTablesModeFragment;
import com.intfocus.syp_template.business.subject.templateone.curvechart.CurveChartImpl;
import com.intfocus.syp_template.business.subject.templateone.curvechart.CurveChartPresenter;
import com.intfocus.syp_template.business.subject.templateone.rootpage.RootPageContract;
import com.intfocus.syp_template.business.subject.templateone.rootpage.RootPageImpl;
import com.intfocus.syp_template.business.subject.templateone.singlevalue.SingleValueImpl;
import com.intfocus.syp_template.business.subject.templateone.singlevalue.SingleValuePresenter;
import com.intfocus.syp_template.general.bean.Report;
import com.intfocus.syp_template.general.base.BaseModeFragment;
import com.zbl.lib.baseframe.core.Subject;
import com.zzhoujay.richtext.RichText;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;
import java.util.Random;


/**
 * 模块一根标签页面
 */
public class ModularOneRootPageModeFragment extends BaseModeFragment<MDetalRootPageMode> implements RootPageContract.View {
    private static final String TAG = "模块一根标签页面";

    public static final String SU_ROOT_ID = "suRootID";
    public static final String SU_UUID = "uuid";

    private View rootView;

    private FragmentManager fm;

    @ViewInject(R.id.ll_mdrp_container)
    private LinearLayout llMdrpContainer;

    /**
     * 最上层跟跟标签ID
     */
    private int suRootID;
    private String uuid;
    private static int STATE_CODE_SUCCESS = 200;
    private RootPageContract.Presenter mPresenter;

    @Override
    public Subject setSubject() {
        return new MDetalRootPageMode(ctx);
    }

    public static ModularOneRootPageModeFragment newInstance(int suRootID, String uuid) {
        ModularOneRootPageModeFragment fragment = new ModularOneRootPageModeFragment();
        Bundle args = new Bundle();
        args.putInt(SU_ROOT_ID, suRootID);
        args.putString(SU_UUID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            suRootID = getArguments().getInt(SU_ROOT_ID);
            uuid = getArguments().getString(SU_UUID);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RootPageImpl.destroyInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fm = getFragmentManager();
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_mdetal, container, false);
            x.view().inject(this, rootView);

            mPresenter.loadData(uuid, suRootID);
        }
        return rootView;
    }

    // ----------------------------------------------------------------
    // -------------------------- 重构代码起始 --------------------------
    // ----------------------------------------------------------------

    @Override
    public void showData(@NotNull List<? extends Report> reports) {
        Random random = new Random();
        for (Report report : reports) {
            Fragment fragment = null;
            switch (report.getType()) {
                //标题栏
                case "banner":
                    fragment = ModularOneUnitBannerModeFragment.newInstance(uuid, report.getIndex());
                    break;

                //曲线图表/柱状图(竖)
                case "chart":
                    fragment = ModularOneUnitCurveChartModeFragment.newInstance(uuid, report.getIndex());
                    new CurveChartPresenter(CurveChartImpl.getInstance(), (ModularOneUnitCurveChartModeFragment) fragment);
                    break;

                //一般标签(附标题)
                case "info":
                    try {
                        View view = LayoutInflater.from(ctx).inflate(R.layout.item_info_layout, null);
                        TextView tv =  view.findViewById(R.id.tv_info);
                        String info = new JSONObject(report.getConfig()).getString("title");
                        RichText.from(info).into(tv);
                        llMdrpContainer.addView(view);
                    } catch (Exception e) {
                        Log.e(TAG, "json 创建失败");
                    }
                    break;

                //单值组件
                case "single_value":
                    fragment = ModularOneUnitSingleValueModeFragment.newInstance(uuid, report.getIndex());
                    new SingleValuePresenter(SingleValueImpl.getInstance(), (ModularOneUnitSingleValueModeFragment) fragment);
                    break;

                //条状图(横)
                case "bargraph":
                    fragment = ModularOneUnitPlusMinusChartModeFragment.newInstance(uuid, report.getIndex());
                    break;

                //类Excel冻结横竖首列表格
                case "tables":
                    fragment = ModularOneUnitTablesModeFragment.newInstance(suRootID, uuid, report.getIndex());
                    break;
                default:
                    break;
            }

            if (fragment != null) {
                FrameLayout layout = new FrameLayout(ctx);
                AppBarLayout.LayoutParams params = new AppBarLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                layout.setLayoutParams(params);
                int id = random.nextInt(Integer.MAX_VALUE);
                layout.setId(id);
                llMdrpContainer.addView(layout);
                FragmentTransaction ft = fm.beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.replace(layout.getId(), fragment);
                ft.commitNow();
            }
        }
    }

    @Override
    public RootPageContract.Presenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void setPresenter(RootPageContract.Presenter presenter) {
        this.mPresenter = presenter;
    }
    // ----------------------------------------------------------------
    // ----------------------- 我是 到此为止分割线 ------------------------
    // ----------------------------------------------------------------
}
