package com.intfocus.yhdev.business.subject.template.one;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.intfocus.yhdev.general.CommentActivity;
import com.intfocus.yhdev.R;
import com.intfocus.yhdev.general.base.BaseModeActivity;
import com.intfocus.yhdev.general.base.BaseModeFragment;
import com.intfocus.yhdev.general.util.ActionLogUtil;
import com.intfocus.yhdev.general.util.DisplayUtil;
import com.intfocus.yhdev.general.util.ImageUtil;
import com.intfocus.yhdev.general.util.ToastUtils;
import com.intfocus.yhdev.general.util.URLs;
import com.intfocus.yhdev.general.view.RootScrollView;
import com.intfocus.yhdev.business.subject.template.one.entity.msg.EventRefreshTableRect;
import com.intfocus.yhdev.business.subject.template.one.mode.MeterDetailActMode;
import com.intfocus.yhdev.business.subject.template.one.rootpage.ModularOneRootPageModeFragment;
import com.intfocus.yhdev.business.subject.templateone.entity.MererDetailEntity;
import com.intfocus.yhdev.business.subject.templateone.rootpage.RootPageImpl;
import com.intfocus.yhdev.business.subject.templateone.rootpage.RootPagePresenter;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zbl.lib.baseframe.core.ActManager;
import com.zbl.lib.baseframe.core.Subject;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * 模块一页面
 */
public class TemplateOneActivity extends BaseModeActivity<MeterDetailActMode> implements ModeContract.View {
    private String TAG = TemplateOneActivity.class.getSimpleName();
    private static final String fragmentTag = "android:switcher:" + R.layout.actvity_meter_detal + ":";

//    @ViewInject(R.id.rootScrollView)
    public RootScrollView rScrollView;

//    @ViewInject(R.id.fl_mdetal_top_suspend_container)
    public FrameLayout suspendContainer;
    public int suspendRootId;
    public ViewStub actionbar;

    private FragmentManager fm;
    private FragmentTransaction ft;
    public static int lastCheckId;
    private BaseModeFragment currFragment;
    private BaseModeFragment toFragment;
    private String currentFtName;

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    /**
     * 当前的Fragment
     */
    private Fragment mCurrentFragment;

    private String groupId;
    private String reportId;
    private String objectType;
    private String bannerName;

//    @ViewInject(R.id.fl_mdetal_title_container)
    private FrameLayout fl_titleContainer;

    private RadioGroup radioGroup;
    private RootTableCheckedChangeListener rootTableListener;

    private TextView tv_single_title;

//    private Context mContext;
    /**
     * 数据实体
     */
//    private MDetailActRequestResult entity;
    private MererDetailEntity entity;
    private ModeContract.Presenter mPresenter;

    @Override
    public int setLayoutRes() {
        return R.layout.actvity_meter_detal;
    }

    @Override
    public Subject setSubject() {
//        EventBus.getDefault().register(this);
        return new MeterDetailActMode(ctx);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ModeImpl.destroyInstance();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreateFinish(Bundle bundle) {

        initHeader();
        actionbar = stubHeader;
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mFragmentManager = getSupportFragmentManager();
//        x.view().inject(this);
        fl_titleContainer = mContentView.findViewById(R.id.fl_mdetal_title_container);
        suspendContainer = mContentView.findViewById(R.id.fl_mdetal_top_suspend_container);
        rScrollView = mContentView.findViewById(R.id.rootScrollView);
        new ModePresenter(ModeImpl.getInstance(), this);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        groupId = intent.getStringExtra("group_id");
        reportId = intent.getStringExtra("objectID");
        objectType = intent.getStringExtra("objectType");
        bannerName = intent.getStringExtra("bannerName");
        rootTableListener = new RootTableCheckedChangeListener();
        setACTitle("标题");
        showDialog(this);
        mPresenter.loadData(this, groupId, reportId);
//        getModel().requestData(groupId, reportId);
    }

    /**
     * 将当前选择的fragment显示(show)出来，没选择的隐藏(hide)
     *
     * @param checkId
     */
    public void switchFragment(int checkId) {
        lastCheckId = checkId;
        currentFtName = fragmentTag + checkId;
        toFragment = (BaseModeFragment) getSupportFragmentManager().findFragmentByTag(currentFtName);

        if (mCurrentFragment != null && mCurrentFragment == toFragment) {
            return;
        }

        if (toFragment == null) {
            ArrayList<MererDetailEntity.PageData> pageDataArrayList = entity.getData();
            if (pageDataArrayList != null && pageDataArrayList.size() > 0) {
                toFragment = ModularOneRootPageModeFragment.newInstance(checkId, pageDataArrayList.get(checkId).getParts());
                new RootPagePresenter(RootPageImpl.getInstance(), (ModularOneRootPageModeFragment) toFragment);
            }
        }

        mFragmentTransaction = mFragmentManager.beginTransaction();
        if (mCurrentFragment == null) {
            mFragmentTransaction.add(R.id.fl_mdetal_cont_container, toFragment, currentFtName).commit();
            mCurrentFragment = toFragment;
        } else if (mCurrentFragment != toFragment) {
            if (!toFragment.isAdded()) {
                mFragmentTransaction.hide(mCurrentFragment).add(R.id.fl_mdetal_cont_container, toFragment, currentFtName).commit();
            } else {
                mFragmentTransaction.hide(mCurrentFragment).show(toFragment).commit();
            }
            mCurrentFragment = toFragment;
        }

//        FragmentTransaction ft = fm.beginTransaction();
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

//        if (!toFragment.isAdded()) {
        // 隐藏当前的fragment，add下一个到Activity中
//            if (currFragment == null)
//        ft.replace(R.id.fl_mdetal_cont_container, toFragment, currentFtName).commitAllowingStateLoss();
//            else
//                ft.hide(currFragment).add(R.id.fl_mdetal_cont_container, toFragment, currentFtName)
//                        .commitAllowingStateLoss();
//        } else {
//            // 隐藏当前的fragment，显示下一个
//            if (currFragment == null)
//                ft.show(toFragment).commitAllowingStateLoss();
//            else
//                ft.hide(currFragment).show(toFragment).commitAllowingStateLoss();
//        }

//        currFragment = toFragment;
        EventBus.getDefault().post(new EventRefreshTableRect(checkId));
    }


    // ----------------------------------------------------------------
    // --------------------------- 老代码起始 ---------------------------
    // ----------------------------------------------------------------

//    /**
//     * 获取到数据，生成根页签
//     *
//     * @param entity
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(MDetailActRequestResult entity) {
//        this.entity = entity;
//        setACTitle(bannerName);
//        if (entity != null) {
//            ArrayList<MererDetailEntity.PageData> pageDataArrayList = entity.getDatas().getData();
//            if (pageDataArrayList == null || pageDataArrayList.size() == 0) {
//                return;
//            }
//            int dataSize = pageDataArrayList.size();
//            // 多个根页签
//            if (dataSize > 1) {
//                View scrollTitle = LayoutInflater.from(ctx)
//                        .inflate(R.layout.item_mdetal_scroll_title, null);
//                fl_titleContainer.addView(scrollTitle);
//                radioGroup = (RadioGroup) scrollTitle.findViewById(R.id.radioGroup);
//
//                for (int i = 0; i < dataSize; i++) {
//                    RadioButton rbtn = new RadioButton(this);
//                    RadioGroup.LayoutParams paramsRb = new RadioGroup.LayoutParams(
//                            RadioGroup.LayoutParams.WRAP_CONTENT,
//                            DisplayUtil.dip2px(ctx, 25f));
//                    paramsRb.setMargins(50, 0, 0, 0);
//
//                    rbtn.setTag(i);
//                    rbtn.setPadding(DisplayUtil.dip2px(ctx, 15f), 0, DisplayUtil.dip2px(ctx, 15f), 0);
//                    rbtn.setButtonDrawable(null);
//                    rbtn.setBackgroundResource(R.drawable.selector_mdetal_act_rbtn);
//                    rbtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_medium));
//                    ColorStateList colorStateList = getResources().getColorStateList(R.color.color_mdetal_act_rbtn);
//                    rbtn.setTextColor(colorStateList);
//                    rbtn.setText(entity.getDatas().getData().get(i).getTitle());
//                    radioGroup.addView(rbtn, paramsRb);
//                    rbtn.setOnCheckedChangeListener(rootTableListener);
//                    if (i == 0) {
//                        rbtn.setChecked(true);
//                    }
//                }
//            }
//            // 只有一个根页签
//            else if (dataSize == 1) {
////                View single_title = LayoutInflater.from(ctx)
////                        .inflate(R.layout.item_mdetal_single_title, null);
////                tv_single_title = (TextView) single_title.findViewById(R.id.tv_mdetal_single_title);
////                fl_titleContainer.addView(single_title);
////                tv_single_title.setText(entity.datas.data.get(0).title);
//                fl_titleContainer.setVisibility(View.GONE);
//                switchFragment(0);
//            }
//        } else {
//            ToastUtils.INSTANCE.show(ctx, "数据实体为空");
//        }
//        hideLoading();
//    }

    // ----------------------------------------------------------------
    // ----------------------- 我是 到此为止分割线 ------------------------
    // ----------------------------------------------------------------

    public class RootTableCheckedChangeListener implements RadioButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                int tag = (Integer) buttonView.getTag();
                switchFragment(tag);
            }
        }
    }


    public void menuItemClick(View view) {
        switch (view.getId()) {
            case R.id.ll_share:
                // 分享
                actionShare2Weixin(view);
                break;
            case R.id.ll_comment:
                // 评论
                actionLaunchCommentActivity(view);
                break;
            case R.id.ll_refresh:
                showDialog(this);
                // 刷新
                refresh(view);
                break;
            default:
                break;
        }
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    /**
     * 分享截图至微信
     */
    public void actionShare2Weixin(View v) {
        Bitmap bmpScrennShot = ImageUtil.takeScreenShot(ActManager.getActManager().currentActivity());
        if (bmpScrennShot == null) {
            ToastUtils.INSTANCE.show(this, "截图失败");
        }
        UMImage image = new UMImage(this, bmpScrennShot);
        new ShareAction(this)
                .withText("截图分享")
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .setDisplayList(SHARE_MEDIA.WEIXIN)
                .withMedia(image)
                .setCallback(umShareListener)
                .open();

        // 用户行为记录, 单独异常处理，不可影响用户体验
        try {
            JSONObject logParams = new JSONObject();
            logParams.put("action", "分享");
            ActionLogUtil.actionLog(ctx, logParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 友盟分享回调监听
     */
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Log.d("throw", "throw:" + " 分享取消了");
        }
    };

    /**
     * 评论
     */
    public void actionLaunchCommentActivity(View v) {
        Intent intent = new Intent(ctx, CommentActivity.class);
        intent.putExtra(URLs.kBannerName, bannerName);
        intent.putExtra(URLs.kObjectId, reportId);
        intent.putExtra(URLs.kObjectType, objectType);
        startActivity(intent);
    }


    // ----------------------------------------------------------------
    // -------------------------- 重构代码起始 --------------------------
    // ----------------------------------------------------------------

    /**
     * 刷新
     *
     * @param v
     */
    public void refresh(View v) {
//        getModel().requestData(groupId, reportId);
        mPresenter.loadData(this, groupId, reportId);
    }

    @Override
    public void initRootView(@NotNull MererDetailEntity entity) {
        this.entity = entity;
        setACTitle(bannerName);
        ArrayList<MererDetailEntity.PageData> pageDataArrayList = entity.getData();
        if (pageDataArrayList == null || pageDataArrayList.size() == 0) {
            return;
        }
        int dataSize = pageDataArrayList.size();
        // 多个根页签
        if (dataSize > 1) {
            View scrollTitle = LayoutInflater.from(ctx)
                    .inflate(R.layout.item_mdetal_scroll_title, null);
            fl_titleContainer.addView(scrollTitle);
            radioGroup = (RadioGroup) scrollTitle.findViewById(R.id.radioGroup);

            for (int i = 0; i < dataSize; i++) {
                RadioButton rbtn = new RadioButton(this);
                RadioGroup.LayoutParams paramsRb = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT,
                        DisplayUtil.dip2px(ctx, 25f));
                paramsRb.setMargins(50, 0, 0, 0);

                rbtn.setTag(i);
                rbtn.setPadding(DisplayUtil.dip2px(ctx, 15f), 0, DisplayUtil.dip2px(ctx, 15f), 0);
                rbtn.setButtonDrawable(null);
                rbtn.setBackgroundResource(R.drawable.selector_mdetal_act_rbtn);
                rbtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_medium));
                ColorStateList colorStateList = getResources().getColorStateList(R.color.color_mdetal_act_rbtn);
                rbtn.setTextColor(colorStateList);
                rbtn.setText(entity.getData().get(i).getTitle());
                radioGroup.addView(rbtn, paramsRb);
                rbtn.setOnCheckedChangeListener(rootTableListener);
                if (i == 0) {
                    rbtn.setChecked(true);
                }
            }
        }
        // 只有一个根页签
        else if (dataSize == 1) {
//                View single_title = LayoutInflater.from(ctx)
//                        .inflate(R.layout.item_mdetal_single_title, null);
//                tv_single_title = (TextView) single_title.findViewById(R.id.tv_mdetal_single_title);
//                fl_titleContainer.addView(single_title);
//                tv_single_title.setText(entity.datas.data.get(0).title);
            fl_titleContainer.setVisibility(View.GONE);
            switchFragment(0);
        }
        hideLoading();
    }

    @Override
    public ModeContract.Presenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void setPresenter(ModeContract.Presenter presenter) {
        mPresenter = presenter;
    }

}
