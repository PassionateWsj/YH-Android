package com.intfocus.yhdev.subject.template_v1;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.intfocus.yhdev.R;
import com.intfocus.yhdev.base.BaseModeFragment;
import com.intfocus.yhdev.dashboard.mine.adapter.TableTitleAdapter;
import com.intfocus.yhdev.subject.template_v1.entity.MDetailUnitEntity;
import com.intfocus.yhdev.subject.template_v1.entity.msg.MDetalRootPageRequestResult;
import com.intfocus.yhdev.subject.template_v1.mode.ModularTwo_UnitTablesParentMode;
import com.zbl.lib.baseframe.core.Subject;
import com.zbl.lib.baseframe.utils.ToastUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 表格根
 */
public class ModularTwo_UnitTablesModeFragment extends BaseModeFragment<ModularTwo_UnitTablesParentMode> implements TableTitleAdapter.NoticeItemListener {
    private String fragmentTag;
    private static final String ARG_PARAM = "TablesParam";
    private static final String SU_ROOT_ID = "SuRootId";

    private String mParam;

    private View rootView;

    @ViewInject(R.id.fl_mdetal_table_title_container)
    private FrameLayout fl_titleContainer;
    @ViewInject(R.id.fl_mdetal_table_cont_container)
    private FrameLayout fl_contContainer;

    private FragmentManager fm;
    private FragmentTransaction ft;
    public static int lastCheckId;
    private BaseModeFragment currFragment;
    private BaseModeFragment toFragment;
    private String currentFtName;

    private RadioGroup radioGroup;
    private ModularTwo_Mode_Activity.RootTableCheckedChangeListener rootTableListener;
    private MDetalRootPageRequestResult entity;

    /**
     * 最上层跟跟标签ID
     */
    public int suRootID;
    //title
    @ViewInject(R.id.recycler_view)
    private RecyclerView recyclerView;
    private TableTitleAdapter adapter;
    private List<MDetailUnitEntity> datas;


    public static ModularTwo_UnitTablesModeFragment newInstance(int suRootID, String param) {
        ModularTwo_UnitTablesModeFragment fragment = new ModularTwo_UnitTablesModeFragment();
        Bundle args = new Bundle();
        args.putInt(SU_ROOT_ID, suRootID);
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Subject setSubject() {
        return new ModularTwo_UnitTablesParentMode(ctx);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            suRootID = getArguments().getInt(SU_ROOT_ID);
            mParam = getArguments().getString(ARG_PARAM);
        }

        Random random = new Random();
        int currentFTID = random.nextInt(Integer.MAX_VALUE);
        fragmentTag = "android:switcher:" + currentFTID + ":";
        fm = getChildFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_modular_two_unit_tables, container, false);
            x.view().inject(this, rootView);
            init();
        }
        return rootView;
    }


    private void init() {
//        rootTableListener = new RootTableCheckedChangeListener();
        getModel().analysisData(mParam);
        datas = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new TableTitleAdapter(getContext(), datas, this);
        recyclerView.setAdapter(adapter);
    }

    public void onMessageEvent(final MDetalRootPageRequestResult entity) {
        if (entity != null && entity.stateCode == 200) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bindData(entity);
                }
            });
        }
    }

    private void bindData(MDetalRootPageRequestResult entity) {
        this.entity = entity;
        if (entity != null) {
            datas = entity.datas;
            adapter.setData(datas);

            switchFragment(0);
        } else {
            ToastUtil.showToast(ctx, "数据实体为空");
        }
    }

    /**
     * 切换页面的重载，优化了fragment的切换
     *
     * @param checkId 选中的下标
     */
    public void switchFragment(int checkId) {

        for (int i = 0; i < datas.size(); i++) {
            datas.get(i).isCheck = (i == checkId);
        }
        adapter.setData(datas);

        lastCheckId = checkId;
        currentFtName = fragmentTag + checkId;
        toFragment = (BaseModeFragment) fm.findFragmentByTag(currentFtName);
        Log.i(this.getClass().getSimpleName(), "currentFtName:" + currentFtName);
        if (currFragment != null && currFragment == toFragment) {
            return;
        }

        if (toFragment == null) {
            toFragment = ModularTwo_UnitTablesContModeFragment.newInstance(suRootID, entity.datas.get(checkId).config);
        }

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        // 选中的页面 添加加载过
        if (!toFragment.isAdded()) {
            // 隐藏当前的fragment，add下一个到Activity中
            if (currFragment == null) {
                ft.add(R.id.fl_mdetal_table_cont_container,
                        toFragment, currentFtName).commitAllowingStateLoss();
            } else {
                ft.hide(currFragment).add(R.id.fl_mdetal_table_cont_container,
                        toFragment, currentFtName)
                        .commitAllowingStateLoss();
            }
        } else {
            // 隐藏当前的fragment，显示下一个
            if (currFragment == null) {
                ft.show(toFragment).commitAllowingStateLoss();
            } else {
                ft.hide(currFragment).show(toFragment).commitAllowingStateLoss();
            }
        }
        currFragment = toFragment;
    }

    /**
     * 表格根页签点击事件回调
     *
     * @param position 选中的下标
     */
    @Override
    public void itemClick(int position) {
        switchFragment(position);
    }
}
