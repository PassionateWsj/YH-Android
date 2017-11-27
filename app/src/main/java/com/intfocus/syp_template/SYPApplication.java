package com.intfocus.syp_template;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.intfocus.syp_template.dashboard.DashboardActivity;
import com.intfocus.syp_template.general.FetchPatchHandler;
import com.intfocus.syp_template.login.LoginActivity;
import com.intfocus.syp_template.model.DaoUtil;
import com.intfocus.syp_template.util.AppStatusTracker;
import com.intfocus.syp_template.util.K;
import com.intfocus.syp_template.util.LogUtil;
import com.intfocus.syp_template.util.OpenUDIDManager;
import com.intfocus.syp_template.util.URLs;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.tinker.loader.app.ApplicationLike;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import org.xutils.x;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.intfocus.syp_template.constant.Params.IS_LOGIN;

/**
 * Created by lijunjie on 16/1/15.
 */
public class SYPApplication extends Application {
    /**
     * 缓存目录
     */
    public static String CACHEDIR;

    public static ExecutorService threadPool = Executors.newFixedThreadPool(2);

    private Context appContext;
    public static Context globalContext;
    SharedPreferences mSettingSP;
    SharedPreferences mUserSP;
    PackageInfo packageInfo;
    @Override
    public void onCreate() {
        super.onCreate();
        AppStatusTracker.init(this);
        if (BuildConfig.TINKER_ENABLE) {
            // 我们可以从这里获得Tinker加载过程的信息
            ApplicationLike tinkerApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();

            // 初始化TinkerPatch SDK, 更多配置可参照API章节中的,初始化SDK
            TinkerPatch.init(tinkerApplicationLike)
                    .reflectPatchLibrary()
                    .setPatchRollbackOnScreenOff(true)
                    .setPatchRestartOnSrceenOff(true);

            // 每隔1个小时去访问后台时候有更新,通过handler实现轮训的效果
            new FetchPatchHandler().fetchPatchWithInterval(1);
            LogUtil.d("TAG", "tinker init");
        }

        appContext = getApplicationContext();
        globalContext = getApplicationContext();
        mSettingSP = getSharedPreferences("SettingPreference", Context.MODE_PRIVATE);
        mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE);
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            if (mSettingSP.getInt("Version", 0) != packageInfo.versionCode) {
                getSharedPreferences("AssetsMD5", Context.MODE_PRIVATE).edit().clear().commit();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        /*
         * 初始化 TBS X5 内核
         */
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean b) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                LogUtil.d("x5_app", " onViewInitFinished is " + b);
            }

            @Override
            public void onCoreInitFinished() {
            }
        };
        QbSdk.initX5Environment(getApplicationContext(),  cb);

        /*
         * Bugly 异常上报
         */
        CrashReport.initCrashReport(getApplicationContext(), ConfigConstants.BUGLY_APP_ID, BuildConfig.DEBUG);

        /*
         * 友盟分享初始化
         */
        UMShareAPI.get(this);

        /*
         * 配置微信 appKey
         */
        PlatformConfig.setWeixin(ConfigConstants.kWXAppId, ConfigConstants.kWXAppSecret);

        /*
         * 初始化数据库
         */
        DaoUtil.INSTANCE.initDataBase(globalContext);

        initXutils();

        /*
         *  初始化 OpenUDID, 设备唯一化
         */
        OpenUDIDManager.sync(getApplicationContext());

        /*
         * 注册推送服务，每次调用register方法都会回调该接口
         */
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                SharedPreferences mPushSP = getSharedPreferences("PushMessage", MODE_PRIVATE);
                SharedPreferences.Editor mPushSPEdit = mPushSP.edit();

                mPushSPEdit.putString(K.K_PUSH_DEVICE_TOKEN, deviceToken).commit();
            }

            @Override
            public void onFailure(String s, String s1) {
                // 向服务器推送: 设备信息,
            }
        });
        mPushAgent.setNotificationClickHandler(pushMessageHandler);
    }

    private void initXutils() {
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }

    final UmengNotificationClickHandler pushMessageHandler = new UmengNotificationClickHandler() {

        @Override
        public void dealWithCustomAction(Context context, UMessage uMessage) {
            super.dealWithCustomAction(context, uMessage);

            Intent intent = null;
            boolean isLogin = getApplicationContext().getSharedPreferences("UserBean", MODE_PRIVATE).getBoolean(IS_LOGIN, false);
            if (isLogin) {
                intent = new Intent(appContext, DashboardActivity.class);
            } else {
                intent = new Intent(appContext, LoginActivity.class);
            }
            Bundle bundle = new Bundle();
            bundle.putString("message", uMessage.custom);
            bundle.putString("message_body_title", uMessage.title);
            bundle.putString("message_body_text", uMessage.text);
            intent.putExtra("msgData", bundle);
            startActivity(intent);
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 程序终止时会执行以下代码
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}


