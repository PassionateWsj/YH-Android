package com.intfocus.syp_template.general.util;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
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

    private static final long MAX_INTERVAL = 60 * 60 * 1000;
    private static final long TEN_YEAR_TIMEMILLIS = 10 * 365 * 24 * 60 * 60 * 1000;
    private static AppStatusTracker tracker;
    private Application application;
    private boolean isForground;
    private int activeCount;
    private long timestamp;


    private AppStatusTracker(Application application) {
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
            if (timestamp < TEN_YEAR_TIMEMILLIS && timestamp > MAX_INTERVAL) {
                ToastUtils.INSTANCE.show(activity,"登录过期");
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

    }

}
