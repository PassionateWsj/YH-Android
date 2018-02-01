package com.intfocus.template;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.facebook.stetho.Stetho;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.intfocus.template.general.PriorityRunnable;
import com.intfocus.template.general.PriorityThreadPoolExecutor;
import com.intfocus.template.model.DaoUtil;
import com.intfocus.template.service.MyPushIntentService;
import com.intfocus.template.util.AppStatusTracker;
import com.intfocus.template.util.K;
import com.intfocus.template.util.OpenUDIDManager;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import org.xutils.x;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static com.intfocus.template.constant.Params.USER_BEAN;


/**
 * @author lijunjie
 * @date 16/1/15
 */
public class SYPApplication extends Application {
    /**
     * 缓存目录
     */
    public static String CACHEDIR;
    public static ThreadFactory priorityThreadFactory = new ThreadFactoryBuilder().setNameFormat("priority-thread-%d").build();
    public static ExecutorService priorityThreadPool = new PriorityThreadPoolExecutor(1,
            1,
            10,
            TimeUnit.SECONDS,
            new PriorityBlockingQueue<Runnable>(10),
            priorityThreadFactory);

    public static Context globalContext;
    SharedPreferences mSettingSP;
    SharedPreferences mUserSP;
    PackageInfo packageInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        AppStatusTracker.init(this);
//        if (BuildConfig.TINKER_ENABLE) {
//            // 我们可以从这里获得Tinker加载过程的信息
//            ApplicationLike tinkerApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();
//
//            // 初始化TinkerPatch SDK, 更多配置可参照API章节中的,初始化SDK
//            TinkerPatch.init(tinkerApplicationLike)
//                    .reflectPatchLibrary()
//                    .setPatchRollbackOnScreenOff(true)
//                    .setPatchRestartOnSrceenOff(true);
//
//            // 每隔1个小时去访问后台时候有更新,通过handler实现轮训的效果
//            new FetchPatchHandler().fetchPatchWithInterval(1);
//            LogUtil.d("TAG", "tinker init");
//        }

        globalContext = getApplicationContext();

        priorityThreadPool.execute(new PriorityRunnable(1) {
            @Override
            public void doSth() {
                mSettingSP = getSharedPreferences("SettingPreference", Context.MODE_PRIVATE);
                Utils.init(SYPApplication.this);
                mUserSP = getSharedPreferences(USER_BEAN, Context.MODE_PRIVATE);
                try {
                    packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    if (mSettingSP.getInt("Version", 0) != packageInfo.versionCode) {
                        getSharedPreferences("AssetsMD5", Context.MODE_PRIVATE).edit().clear().apply();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                /*
                 * 初始化数据库
                 */
                DaoUtil.INSTANCE.initDataBase(globalContext);

                initXutils();
            }
        });



        /*
         * 初始化 TBS X5 内核
         */
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean b) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("x5_app", " onViewInitFinished is " + b);
            }

            @Override
            public void onCoreInitFinished() {
            }
        };
        QbSdk.initX5Environment(getApplicationContext(), cb);

        /*
         * Bugly 异常上报
         */
        CrashReport.initCrashReport(getApplicationContext(), BuildConfig.BUGLY_APP_ID, BuildConfig.DEBUG);

        /*
         * 友盟分享初始化
         */
        UMShareAPI.get(this);

        /*
         * Stetho 调试初始化
         */
        Stetho.initializeWithDefaults(this);

        /*
         * 配置微信 appKey
         */
        PlatformConfig.setWeixin(BuildConfig.WX_APP_ID, BuildConfig.WX_APP_Secret);

        /*
         *  初始化 OpenUDID, 设备唯一化
         */
        OpenUDIDManager.sync(SYPApplication.this);

        /*
         * 注册推送服务，每次调用register方法都会回调该接口
         */
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                SharedPreferences mPushSP = getSharedPreferences("PushMessage", MODE_PRIVATE);
                SharedPreferences.Editor mPushSPEdit = mPushSP.edit();

                mPushSPEdit.putString(K.K_PUSH_DEVICE_TOKEN, deviceToken).apply();
            }

            @Override
            public void onFailure(String s, String s1) {
                // 向服务器推送: 设备信息,
            }
        });
        mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);
    }

    private void initXutils() {
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }


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
        priorityThreadPool.shutdown();
    }
}


