package com.intfocus.template;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.support.multidex.MultiDex;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.intfocus.template.general.FetchPatchHandler;
import com.intfocus.template.general.PriorityThreadPoolExecutor;
import com.intfocus.template.model.DaoUtil;
import com.intfocus.template.model.entity.PushMsgBean;
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

import java.util.HashMap;
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
            public Notification getNotification(final Context context, final UMessage uMessage) {
                PushMsgBean pushMsg = com.alibaba.fastjson.JSONObject.parseObject(uMessage.custom, PushMsgBean.class);
                pushMsg.setTicker(uMessage.ticker);
                pushMsg.setBody_title(uMessage.title);
                pushMsg.setText(uMessage.text);
                pushMsg.setNew_msg(true);
                DaoUtil.INSTANCE.getPushMsgDao().insert(pushMsg);

                new AlertDialog.Builder(context)
                        .setTitle(uMessage.title)
                        .setMessage(uMessage.text)
                        .setPositiveButton("查看", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pushMsgOnClick(context, uMessage);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                return showNotifications(context, uMessage);

            }

            /**
             * 自定义通知布局
             *
             * @param context 上下文
             * @param msg     消息体
             */
            private Notification showNotifications(Context context, UMessage msg) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setContentTitle(msg.title)
                        .setContentText(msg.text)
                        .setTicker(msg.ticker)
                        .setWhen(System.currentTimeMillis())
//                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true);

                return builder.build();
            }
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
            pushMsgOnClick(context, uMessage);
        }
    };

    private void pushMsgOnClick(Context context, UMessage uMessage) {
        PushMsgBean pushMsg = JSONObject.parseObject(uMessage.custom, PushMsgBean.class);
        HashMap<String, String> paramsMappingBean = JSONObject.parseObject(pushMsg.getParams_mapping(), new TypeReference<HashMap<String, String>>() {
        });
        String templateId = "";
        if (pushMsg.getTemplate_id() == null || "".equals(pushMsg.getTemplate_id())) {
            String[] temp = pushMsg.getUrl().split("/");
            for (int i = 0; i < temp.length; i++) {
                if ("template".equals(temp[i]) && i + 1 < temp.length) {
                    templateId = temp[i + 1];
                    break;
                }
            }
        } else {
            templateId = pushMsg.getTemplate_id();
        }
//            SharedPreferences userSP = context.getSharedPreferences("UserBean", Context.MODE_PRIVATE);
//            String groupID = userSP.getString(GROUP_ID, "0");
//            String userNum = userSP.getString(USER_NUM, "");
//
//            if () {
//            }
        PageLinkManage.INSTANCE.pageLink(context, pushMsg.getTitle(), pushMsg.getUrl(), pushMsg.getObj_id(), templateId, "4", paramsMappingBean, true);
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


