package com.intfocus.template;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.intfocus.template.general.FetchPatchHandler;
import com.intfocus.template.general.PriorityThreadPoolExecutor;
import com.intfocus.template.model.DaoUtil;
import com.intfocus.template.model.entity.PushMsgBean;
import com.intfocus.template.ui.DiaLogActivity;
import com.intfocus.template.util.AppStatusTracker;
import com.intfocus.template.util.K;
import com.intfocus.template.util.LogUtil;
import com.intfocus.template.util.OpenUDIDManager;
import com.intfocus.template.util.PageLinkManage;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.tinker.loader.app.ApplicationLike;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import org.xutils.x;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

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
        QbSdk.initX5Environment(getApplicationContext(), cb);

        /*
         * Bugly 异常上报
         */
        CrashReport.initCrashReport(getApplicationContext(), ConfigConstants.BUGLY_APP_ID, BuildConfig.DEBUG);

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
        mPushAgent.setMessageHandler(new UmengMessageHandler() {

            @Override
            public void dealWithNotificationMessage(Context context, UMessage uMessage) {
                super.dealWithNotificationMessage(context, uMessage);
                PushMsgBean pushMsg = com.alibaba.fastjson.JSONObject.parseObject(uMessage.custom, PushMsgBean.class);
                pushMsg.setTicker(uMessage.ticker);
                pushMsg.setBody_title(uMessage.title);
                pushMsg.setText(uMessage.text);
                pushMsg.setNew_msg(true);
                DaoUtil.INSTANCE.getPushMsgDao().insert(pushMsg);

                Intent intent = new Intent(context, DiaLogActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("body_title", pushMsg.getBody_title());
                intent.putExtra("title", pushMsg.getTitle());
                intent.putExtra("url", pushMsg.getUrl());
                intent.putExtra("obj_id", pushMsg.getObj_id());
                intent.putExtra("template_id", pushMsg.getTemplate_id());
                intent.putExtra("params_mapping", pushMsg.getParams_mapping());
                startActivity(intent);
            }

//            /**
//             * 自定义通知布局
//             *
//             * @param context 上下文
//             * @param msg     消息体
//             */
//            private Notification showNotifications(Context context, UMessage msg) {
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//                builder.setContentTitle(msg.title)
//                        .setContentText(msg.text)
//                        .setTicker(msg.ticker)
//                        .setWhen(System.currentTimeMillis())
////                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
//                        .setDefaults(Notification.DEFAULT_ALL)
//                        .setAutoCancel(true);
//                return builder.build();
//            }
        });
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }

    private void initXutils() {
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }

    final UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

        @Override
        public void dealWithCustomAction(Context context, UMessage uMessage) {
            super.dealWithCustomAction(context, uMessage);
            PushMsgBean pushMsg = JSONObject.parseObject(uMessage.custom, PushMsgBean.class);
            PageLinkManage.INSTANCE.pageLink(context, pushMsg);
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
        priorityThreadPool.shutdown();
    }
}


