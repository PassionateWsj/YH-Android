package com.intfocus.yonghuitest.subject.template_v1;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

import com.intfocus.yonghuitest.CommentActivity;
import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.base.BaseModeActivity;
import com.intfocus.yonghuitest.base.BaseModeFragment;
import com.intfocus.yonghuitest.subject.template_v1.entity.msg.EventRefreshTableRect;
import com.intfocus.yonghuitest.subject.template_v1.entity.msg.MDetalActRequestResult;
import com.intfocus.yonghuitest.subject.template_v1.mode.MeterDetalActMode;
import com.intfocus.yonghuitest.util.ActionLogUtil;
import com.intfocus.yonghuitest.util.DisplayUtil;
import com.intfocus.yonghuitest.util.ImageUtil;
import com.intfocus.yonghuitest.util.ToastUtils;
import com.intfocus.yonghuitest.util.URLs;
import com.intfocus.yonghuitest.view.RootScrollView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zbl.lib.baseframe.core.ActManager;
import com.zbl.lib.baseframe.core.Subject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 模块二页面
 */
public class ModularTwo_Mode_Activity extends BaseModeActivity<MeterDetalActMode> {
    private String TAG = ModularTwo_Mode_Activity.class.getSimpleName();
    private static final String fragmentTag = "android:switcher:" + R.layout.actvity_meter_detal + ":";

    @ViewInject(R.id.rootScrollView)
    public RootScrollView rScrollView;

    @ViewInject(R.id.fl_mdetal_top_suspend_container)
    public FrameLayout suspendContainer;
    public int suspendRootId;
    public ViewStub actionbar;

    private FragmentManager fm;
    private FragmentTransaction ft;
    public static int lastCheckId;
    private BaseModeFragment currFragment;
    private BaseModeFragment toFragment;
    private String currentFtName;

    private String groupId;
    private String reportId;
    private String objectType;
    private String bannerName;

    @ViewInject(R.id.fl_mdetal_title_container)
    private FrameLayout fl_titleContainer;

    private RadioGroup radioGroup;
    private RootTableCheckedChangeListener rootTableListener;

    private TextView tv_single_title;

    private Context mContext;
    /**
     * 数据实体
     */
    private MDetalActRequestResult entity;

    @Override
    public int setLayoutRes() {
        return R.layout.actvity_meter_detal;
    }

    @Override
    public Subject setSubject() {
        EventBus.getDefault().register(this);
        return new MeterDetalActMode(ctx);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreateFinish(Bundle bundle) {
        initHeader();
        actionbar = stub_header;
        getSupportActionBar().hide();
        fm = getSupportFragmentManager();
        x.view().inject(this);
        mContext = this;
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
        showDialog(mContext);
        getModel().requestData(groupId, reportId);
    }

    /**
     * 切换页面的重载，优化了fragment的切换
     */
    public void switchFragment(int checkId) {
        lastCheckId = checkId;
        currentFtName = fragmentTag + checkId;
        toFragment = (BaseModeFragment) getSupportFragmentManager().findFragmentByTag(currentFtName);

        if (currFragment != null && currFragment == toFragment)
            return;

        if (toFragment == null)
            toFragment = ModularTwo_RootPageModeFragment.newInstance(checkId, entity.datas.data.get(checkId).parts);

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

//        if (!toFragment.isAdded()) {
            // 隐藏当前的fragment，add下一个到Activity中
//            if (currFragment == null)
                ft.replace(R.id.fl_mdetal_cont_container, toFragment, currentFtName).commitAllowingStateLoss();
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

        currFragment = toFragment;
        EventBus.getDefault().post(new EventRefreshTableRect(checkId));
    }

    /**
     * 图表点击事件统一处理方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MDetalActRequestResult entity) {
        this.entity = entity;
        setACTitle(bannerName);
        if (entity != null) {
            int dataSize = entity.datas.data.size();
            if (dataSize > 1) {    // 多个根页签
                View scroll_title = LayoutInflater.from(ctx)
                        .inflate(R.layout.item_mdetal_scroll_title, null);
                fl_titleContainer.addView(scroll_title);
                radioGroup = (RadioGroup) scroll_title.findViewById(R.id.radioGroup);

                for (int i = 0; i < dataSize; i++) {
                    RadioButton rbtn = new RadioButton(this);
                    RadioGroup.LayoutParams params_rb = new RadioGroup.LayoutParams(
                            RadioGroup.LayoutParams.WRAP_CONTENT,
                            DisplayUtil.dip2px(ctx, 25f));
                    params_rb.setMargins(50, 0, 0, 0);

                    rbtn.setTag(i);
                    rbtn.setPadding(DisplayUtil.dip2px(ctx, 15f), 0, DisplayUtil.dip2px(ctx, 15f), 0);
                    Bitmap a = null;
                    rbtn.setButtonDrawable(new BitmapDrawable(a));
                    rbtn.setBackgroundResource(R.drawable.selector_mdetal_act_rbtn);
                    rbtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_medium));
                    ColorStateList colorStateList = getResources().getColorStateList(R.color.color_mdetal_act_rbtn);
                    rbtn.setTextColor(colorStateList);
                    rbtn.setText(entity.datas.data.get(i).title);
                    radioGroup.addView(rbtn, params_rb);
                    rbtn.setOnCheckedChangeListener(rootTableListener);
                    if (i == 0)
                        rbtn.setChecked(true);
                }
            } else if (dataSize == 1) {    // 只有一个根页签
//                View single_title = LayoutInflater.from(ctx)
//                        .inflate(R.layout.item_mdetal_single_title, null);
//                tv_single_title = (TextView) single_title.findViewById(R.id.tv_mdetal_single_title);
//                fl_titleContainer.addView(single_title);
//                tv_single_title.setText(entity.datas.data.get(0).title);
                fl_titleContainer.setVisibility(View.GONE);
                switchFragment(0);
            }
        } else
            ToastUtils.INSTANCE.show(ctx, "数据实体为空");
        hideLoading();
    }

    class RootTableCheckedChangeListener implements RadioButton.OnCheckedChangeListener {

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
                showDialog(mContext);
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

        /*
         * 用户行为记录, 单独异常处理，不可影响用户体验
         */
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

    /*
     * 评论
     */
    public void actionLaunchCommentActivity(View v) {
        Intent intent = new Intent(ctx, CommentActivity.class);
        intent.putExtra(URLs.kBannerName, bannerName);
        intent.putExtra(URLs.kObjectId, reportId);
        intent.putExtra(URLs.kObjectType, objectType);
        mContext.startActivity(intent);
    }

    /**
     * 刷新
     * @param v
     */
    public void refresh(View v) {
        getModel().requestData(groupId, reportId);
    }
}