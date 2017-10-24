package com.intfocus.yhdev.subject.template_v1;

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

import com.intfocus.yhdev.R;
import com.intfocus.yhdev.base.BaseModeFragment;
import com.intfocus.yhdev.subject.template_v1.entity.MDetailUnitEntity;
import com.intfocus.yhdev.subject.template_v1.entity.msg.MDetalRootPageRequestResult;
import com.intfocus.yhdev.subject.template_v1.mode.MDetalRootPageMode;
import com.intfocus.yhdev.subject.template_v1.singlevalue.ModularTwo_UnitSingleValueModeFragment;
import com.intfocus.yhdev.subject.template_v1.singlevalue.SingleValueImpl;
import com.intfocus.yhdev.subject.template_v1.singlevalue.SingleValuePresenter;
import com.zbl.lib.baseframe.core.Subject;
import com.zzhoujay.richtext.RichText;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Random;


/**
 * 模块一根标签页面
 */
public class ModularOneRootPageModeFragment extends BaseModeFragment<MDetalRootPageMode> {
    private static final String TAG = "模块一根标签页面";

    public static final String SU_ROOT_ID = "suRootID";
    private static final String ARG_PARAM = "param";
    private String mParam;

    private View rootView;

    private FragmentManager fm;

    @ViewInject(R.id.ll_mdrp_container)
    private LinearLayout llMdrpContainer;

    /**
     * 最上层跟跟标签ID
     */
    private int suRootID;
    private static int STATE_CODE_SUCCESS= 200;

    @Override
    public Subject setSubject() {
        return new MDetalRootPageMode(ctx);
    }

    public static ModularOneRootPageModeFragment newInstance(int suRootID, String param) {
        ModularOneRootPageModeFragment fragment = new ModularOneRootPageModeFragment();
        Bundle args = new Bundle();
        args.putInt(SU_ROOT_ID, suRootID);
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            suRootID = getArguments().getInt(SU_ROOT_ID);
            mParam = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fm = getFragmentManager();
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_mdetal, container, false);
            x.view().inject(this, rootView);
            getModel().analysisData(mParam);
        }
        return rootView;
    }

    /**
     * 图表点击事件统一处理方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final MDetalRootPageRequestResult entity) {
        if (entity != null && entity.stateCode == STATE_CODE_SUCCESS) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bindData(entity);
                }
            });
        }
    }

    /**
     * 绑定数据
     */
    private void bindData(MDetalRootPageRequestResult result) {
        ArrayList<MDetailUnitEntity> datas = result.datas;
        int size = datas.size();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            Fragment fragment = null;
            MDetailUnitEntity entity = datas.get(i);
            switch (entity.type) {
                //标题栏
                case "banner":
                    fragment = ModularOneUnitBannerModeFragment.newInstance(entity.config);
                    break;

                //曲线图表/柱状图(竖)
                case "chart":
                    fragment = ModularOneUnitCurveChartModeFragment.newInstance(entity.config);
                    break;

                //一般标签(附标题)
                case "info":
                    try {
                        View view = LayoutInflater.from(ctx).inflate(R.layout.item_info_layout, null);
                        TextView tv = (TextView) view.findViewById(R.id.tv_info);
                        String info = new JSONObject(entity.config).getString("title");
                        RichText.from(info).into(tv);
                        llMdrpContainer.addView(view);
                    } catch (Exception e) {
                        Log.e(TAG,"json 创建失败");
                    }
                    break;

                //单值组件
                case "single_value":
                    fragment = ModularTwo_UnitSingleValueModeFragment.newInstance(entity.config);
                    new SingleValuePresenter(SingleValueImpl.getInstance(),(ModularTwo_UnitSingleValueModeFragment)fragment);
                    break;

                //条状图(横)
                case "bargraph":
                    fragment = ModularOneUnitPlusMinusChartModeFragment.newInstance(entity.config);
                    break;

                //类Excel冻结横竖首列表格
                case "tables":
                    fragment = ModularOneUnitTablesModeFragment.newInstance(suRootID, entity.config);
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
}
