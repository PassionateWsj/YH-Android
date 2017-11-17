package com.intfocus.syp_template.general.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.intfocus.syp_template.business.login.LoginActivity;

/**
 * ****************************************************
 * <p>
 * author jameswong
 * created on: 17/11/16 下午1:17
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
public class AppStatusTracker implements Application.ActivityLifecycleCallbacks {

    /**
     * 登录过期时间 - 1 小时
     */
    private static final long MAX_INTERVAL = 60 * 60 * 1000;
    /**
     * （Debug）登录过期时间 - 3 秒
     */
//    private static final long MAX_INTERVAL = 1 * 3 * 1000;
    /**
     * 判断标识（app 刚启动，不在后台）
     */
    private static final long TEN_YEAR_TIMEMILLIS = 10 * 365 * 24 * 60 * 60 * 1000;
    private static AppStatusTracker tracker;
    private Application application;
    private SharedPreferences mUserSP;
    private SharedPreferences.Editor mUserSPEdit;
    /**
     * 是否在前台
     */
    private boolean isForground;
    /**
     * 前台活动的activity
     */
    private int activeCount;
    /**
     * 记录用户将 app 切到后台的时长（时间戳）
     */
    private long timestamp;


    private AppStatusTracker(Application application) {
        mUserSP = application.getSharedPreferences("UserBean", Context.MODE_PRIVATE);
        mUserSPEdit = mUserSP.edit();
        this.application = application;
        application.registerActivityLifecycleCallbacks(this);
    }

    public static void init(Application application) {
        tracker = new AppStatusTracker(application);
    }

    public static AppStatusTracker getInstance() {
        return tracker;
    }


    public boolean isForground() {
        return isForground;
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

        if (activeCount == 0) {
            timestamp = System.currentTimeMillis() - timestamp;
            boolean isLogin = mUserSP.getBoolean(URLs.kIsLogin, false);
            LogUtil.e(LogUtil.TAG, "isLogin:::" + isLogin);
            if (timestamp < TEN_YEAR_TIMEMILLIS && timestamp > MAX_INTERVAL && isLogin) {
                mUserSPEdit.putBoolean(URLs.kIsLogin, false).apply();
                ToastUtils.INSTANCE.show(activity, "登录过期");
                Intent intent = new Intent(activity, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                activity.finish();
            }
        }
        activeCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        isForground = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

        activeCount--;
        if (activeCount == 0) {
            isForground = false;
            timestamp = System.currentTimeMillis();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activeCount == 0) {
//            LogUtil.e(LogUtil.TAG, "isLogin:::" + mUserSP.getBoolean(URLs.kIsLogin, false));
            mUserSPEdit.putBoolean(URLs.kIsLogin, false).apply();
        }
    }

}
