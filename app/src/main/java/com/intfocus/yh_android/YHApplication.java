package com.intfocus.yh_android;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.intfocus.yh_android.dashboard.DashboardActivity;
import com.intfocus.yh_android.screen_lock.ConfirmPassCodeActivity;
import com.intfocus.yh_android.util.FileUtil;
import com.intfocus.yh_android.util.K;
import com.intfocus.yh_android.util.URLs;
import com.pgyersdk.crash.PgyCrashManager;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import org.OpenUDID.OpenUDID_manager;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.intfocus.yh_android.util.PrivateURLs.kWXAppId;
import static com.intfocus.yh_android.util.PrivateURLs.kWXAppSecret;
import static com.intfocus.yh_android.util.K.kPushDeviceToken;

/**
 * Created by lijunjie on 16/1/15.
 */
public class YHApplication extends Application {
    private Context appContext;
    SharedPreferences mSharedPreferences;
    PackageInfo packageInfo;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();
        String sharedPath = FileUtil.sharedPath(appContext), basePath = FileUtil.basePath(appContext);
        mSharedPreferences = getSharedPreferences("SettingPreference", Context.MODE_PRIVATE);
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        /*
         *  蒲公英平台，收集闪退日志
         */
        PgyCrashManager.register(this);

        /*
         * 友盟分享初始化
         */
        UMShareAPI.get(this);

        /*
         * 配置微信 appKey
         */
        PlatformConfig.setWeixin(kWXAppId, kWXAppSecret);

        initXutils();

        /*
         *  初始化 OpenUDID, 设备唯一化
         */
        OpenUDID_manager.sync(getApplicationContext());

        /*
         *  基本目录结构
         */
        makeSureFolderExist(K.kSharedDirName);
        makeSureFolderExist(K.kCachedDirName);

        /*
         *  新安装、或升级后，把代码包中的静态资源重新拷贝覆盖一下
         *  避免再从服务器下载更新，浪费用户流量
         */
        copyAssetFiles(basePath, sharedPath);

        /*
         *  校正静态资源
         *
         *  sharedPath/filename.zip md5 值 <=> user.plist 中 filename_md5
         *  不一致时，则删除原解压后文件夹，重新解压 zip
         */
        FileUtil.checkAssets(appContext, URLs.kAssets, false);
        FileUtil.checkAssets(appContext, URLs.kLoading, false);
        FileUtil.checkAssets(appContext, URLs.kFonts, true);
        FileUtil.checkAssets(appContext, URLs.kImages, true);
        FileUtil.checkAssets(appContext, URLs.kIcons, true);
        FileUtil.checkAssets(appContext, URLs.kStylesheets, true);
        FileUtil.checkAssets(appContext, URLs.kJavaScripts, true);
        FileUtil.checkAssets(appContext, URLs.kBarCodeScan, false);
        // FileUtil.checkAssets(mContext, URLs.kAdvertisement, false);

        /*
         *  手机待机再激活时发送开屏广播
         */
        registerReceiver(broadcastScreenOnAndOff, new IntentFilter(Intent.ACTION_SCREEN_ON));

        /*
         *  监测内存泄漏
         */
//         refWatcher = LeakCanary.install(this);

        /*
         * 注册推送服务，每次调用register方法都会回调该接口
         */
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                try {
                    //注册成功会返回device token
                    // onRegistered方法的参数registrationId即是device_token
                    String pushConfigPath = String.format("%s/%s", FileUtil.basePath(appContext), K.kPushConfigFileName);
                    if (new File(pushConfigPath).exists()) {
                        new File(pushConfigPath).delete();
                    }
                    JSONObject pushJSON = FileUtil.readConfigFile(pushConfigPath);
                    pushJSON.put(K.kPushIsValid, false);
                    pushJSON.put(kPushDeviceToken, deviceToken);
                    FileUtil.writeFile(pushConfigPath, pushJSON.toString());
                    Log.d(kPushDeviceToken, deviceToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            Intent intent = new Intent(context, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("fromMessage", true);
            startActivity(intent);
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    /*
     * 程序终止时会执行以下代码
     */
    @Override
    public void onTerminate() {
        PgyCrashManager.unregister(); // 解除注册蒲公英异常信息上传
        super.onTerminate();
    }

    public Context getAppContext() {
        return appContext;
    }

    private void makeSureFolderExist(String folderName) {
        String cachedPath = String.format("%s/%s", FileUtil.basePath(appContext), folderName);
//        if (mSharedPreferences.getInt("Version", 0) != packageInfo.versionCode) {
//            deleteDirWihtFile(new File(cachedPath));
//        }
        FileUtil.makeSureFolderExist(cachedPath);
    }

    /*
     *  手机待机再激活时接收解屏广播,进入解锁密码页
     */
    private final BroadcastReceiver broadcastScreenOnAndOff = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Intent.ACTION_SCREEN_ON) || isBackground(appContext)) {
                Log.i("BroadcastReceiver", "return" + isBackground(appContext));
                return;
            }
            Log.i("BroadcastReceiver", "Screen On");
            String currentActivityName = ((YHApplication) context.getApplicationContext()).getCurrentActivity();
            if ((currentActivityName != null && !currentActivityName.trim().equals("ConfirmPassCodeActivity")) && // 当前活动的Activity非解锁界面
                    FileUtil.checkIsLocked(appContext)) { //应用处于登录状态，并且开启了密码锁
                intent = new Intent(appContext, ConfirmPassCodeActivity.class);
                intent.putExtra("is_from_login", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                appContext.startActivity(intent);
            }
        }
    };

    private String mCurrentActivity = null;

    public String getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Context context) {
        if (context == null) {
            mCurrentActivity = null;
            return;
        }
        String mActivity = context.toString();
        String mActivityName = mActivity.substring(mActivity.lastIndexOf(".") + 1, mActivity.indexOf("@"));
        mCurrentActivity = mActivityName;
        Log.i("activityName", mCurrentActivity);
    }

    /*
     * 判断应用当前是否处于后台
     * Android 4.4 以上版本 不适用 getRunningTasks() 方法
     */
    private boolean isBackground(Context context) {
        boolean isBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isBackground = false;
            }
        }

        return isBackground;
    }

    /**
     * 新安装、或升级后，把代码包中的静态资源重新拷贝覆盖一下
     * 避免再从服务器下载更新，浪费用户流量
     */
    private void copyAssetFiles(String basePath, String sharedPath) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionConfigPath = String.format("%s/%s", basePath, K.kCurrentVersionFileName);

            boolean isUpgrade = true;
            String localVersion = "new-installer";
            if ((new File(versionConfigPath)).exists()) {
                localVersion = FileUtil.readFile(versionConfigPath);
                isUpgrade = !localVersion.equals(packageInfo.versionName);
            }
            if (!isUpgrade) return;
            Log.i("VersionUpgrade", String.format("%s => %s remove %s/%s", localVersion, packageInfo.versionName, basePath, K.kCachedHeaderConfigFileName));

            String assetZipPath;
            File assetZipFile;
            String[] assetsName = {URLs.kAssets, URLs.kLoading, URLs.kFonts, URLs.kImages, URLs.kStylesheets, URLs.kJavaScripts, URLs.kBarCodeScan}; // ,URLs.kAdvertisement

            for (String string : assetsName) {
                assetZipPath = String.format("%s/%s.zip", sharedPath, string);
                assetZipFile = new File(assetZipPath);
                if (!assetZipFile.exists()) {
                    assetZipFile.delete();
                }
                FileUtil.copyAssetFile(appContext, String.format("%s.zip", string), assetZipPath);
            }
            FileUtil.writeFile(versionConfigPath, packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }
}


