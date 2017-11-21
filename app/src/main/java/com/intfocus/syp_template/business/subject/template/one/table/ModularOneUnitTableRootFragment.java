package com.intfocus.syp_template.business.subject.template.one.table;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.intfocus.syp_template.R;
import com.intfocus.syp_template.business.dashboard.mine.adapter.TableTitleAdapter;
import com.intfocus.syp_template.business.subject.template.one.entity.MDetailUnitEntity;
import com.intfocus.syp_template.business.subject.template.one.entity.msg.MDetailRootPageRequestResult;
import com.intfocus.syp_template.business.subject.template.one.mode.ModularTwo_UnitTablesParentMode;
import com.intfocus.syp_template.general.base.BaseModeFragment;
import com.intfocus.syp_template.general.data.TempSubData;
import com.intfocus.syp_template.general.util.LogUtil;
import com.zbl.lib.baseframe.core.Subject;
import com.zbl.lib.baseframe.utils.ToastUtil;

import org.jetbrains.annotations.NotNull;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 表格根
 */
public class ModularOneUnitTableRootFragment extends BaseModeFragment<ModularTwo_UnitTablesParentMode> implements TableTitleAdapter.NoticeItemListener, TableRootContract.View {
    private String fragmentTag;
    private static final String SU_ROOT_ID = "suRootId";
    private static final String ARG_INDEX = "index";
    private static final String ARG_UUID = "uuid";

    private int index;
    private String uuid;

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

    private MDetailRootPageRequestResult entity;

    /**
     * 最上层跟跟标签ID
     */
    public int suRootID;
    //title
    @ViewInject(R.id.recycler_view)
    private RecyclerView recyclerView;
    private TableTitleAdapter adapter;
    private List<MDetailUnitEntity> datas;
    private TableRootContract.Presenter mPresenter;


    public static ModularOneUnitTableRootFragment newInstance(int suRootID, String uuid, int index) {
        ModularOneUnitTableRootFragment fragment = new ModularOneUnitTableRootFragment();
        Bundle args = new Bundle();
        args.putInt(SU_ROOT_ID, suRootID);
        args.putInt(ARG_INDEX, index);
        args.putString(ARG_UUID, uuid);
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
            index = getArguments().getInt(ARG_INDEX);
            uuid = getArguments().getString(ARG_UUID);
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
        mPresenter.loadData(uuid, index);
        datas = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new TableTitleAdapter(getContext(), datas, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void dataLoaded(@NotNull MDetailRootPageRequestResult data) {
        bindData(data);
    }

    @Override
    public TableRootContract.Presenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void setPresenter(TableRootContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void bindData(MDetailRootPageRequestResult entity) {
        this.entity = entity;
        if (entity != null && entity.datas != null) {
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
        LogUtil.d(this.getClass().getSimpleName(), "currentFtName:" + currentFtName);
        if (currFragment != null && currFragment == toFragment) {
            return;
        }

        if (toFragment == null) {
            toFragment = ModularOneUnitTablesContentModeFragment.newInstance(suRootID, index);
            TempSubData.setData(index, entity.datas.get(checkId).config);
            new TableContentPresenter(TableImpl.getInstance(), (ModularOneUnitTablesContentModeFragment) toFragment);
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
