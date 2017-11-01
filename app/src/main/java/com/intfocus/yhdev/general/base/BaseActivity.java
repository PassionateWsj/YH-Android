package com.intfocus.yhdev.general.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.intfocus.yhdev.R;
import com.intfocus.yhdev.general.YHApplication;
import com.intfocus.yhdev.general.constant.ToastColor;
import com.intfocus.yhdev.general.util.ApiHelper;
import com.intfocus.yhdev.general.util.FileUtil;
import com.intfocus.yhdev.general.util.HttpUtil;
import com.intfocus.yhdev.general.util.K;
import com.intfocus.yhdev.general.util.LogUtil;
import com.intfocus.yhdev.general.util.ToastUtils;
import com.intfocus.yhdev.general.util.URLs;
import com.intfocus.yhdev.business.login.LoginActivity;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.umeng.message.PushAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by lijunjie on 16/1/14.
 */
public class BaseActivity extends FragmentActivity {

    public final static String kLoading = "loading";
    public final static String kPath = "path";
    public final static String kMessage = "message";
    public final static String kVersionCode = "versionCode";
    public String sharedPath;
    public String relativeAssetsPath;
    public YHApplication mMyApp;
    public PopupWindow popupWindow;
    public DisplayMetrics displayMetrics;
    public boolean isWeiXinShared = false;
    public android.webkit.WebView mWebView;
    public RelativeLayout animLoading;
    public String userID;
    public String urlString;
    public String assetsPath;
    public String urlStringForLoading;
    public JSONObject logParams = new JSONObject();
    public Context mAppContext;
    public Toast toast;
    int displayDpi;
    public boolean isOffline = false;
    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessage1;
    public SharedPreferences mUserSP;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取当前设备屏幕密度
        displayMetrics = getResources().getDisplayMetrics();
        displayDpi = displayMetrics.densityDpi;

        mMyApp = (YHApplication) this.getApplication();
        mAppContext = mMyApp.getAppContext();

        //统计应用启动数据
        PushAgent.getInstance(mAppContext).onAppStart();

        sharedPath = FileUtil.sharedPath(mAppContext);
        assetsPath = sharedPath;
        relativeAssetsPath = "assets";
        urlStringForLoading = loadingPath(kLoading);
        mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE);

        if (mUserSP.getBoolean(URLs.kIsLogin, false)) {
            userID = mUserSP.getString("user_id", "0");
            assetsPath = FileUtil.dirPath(mAppContext, K.kHTMLDirName);
            relativeAssetsPath = "../../Shared/assets";
        }
    }

    @Override
    protected void onDestroy() {
        clearReferences();
        fixInputMethodManager(BaseActivity.this);
        mMyApp = null;
        mAppContext = null;
        super.onDestroy();
    }

    /**
     * 返回
     */
    public void dismissActivity(View v) {
        super.onBackPressed();
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    private void clearReferences() {
        String currActivity = mMyApp.getCurrentActivity();
        if (this.equals(currActivity)) {
            mMyApp.setCurrentActivity(null);
        }
    }

    private void fixInputMethodManager(Context context) {
        if (context == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object obj_get = null;
        for (String param : arr) {
            try {
                f = imm.getClass().getDeclaredField(param);
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (v_get.getContext() == context) { // 被InputMethodManager持有引用的context是想要销毁的
                        f.set(imm, null);                // 置空
                    } else {
                        break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    protected String loadingPath(String htmlName) {
        return String.format("file:///%s/loading/%s.html", sharedPath, htmlName);
    }

    public void setWebViewLongListener(final boolean flag) {
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return flag;
            }
        });
    }

    public android.webkit.WebView initSubWebView() {
        animLoading = (RelativeLayout) findViewById(R.id.anim_loading);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");

        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        mWebView.getSettings().setAppCachePath(appCachePath);
        mWebView.getSettings().setAllowFileAccess(true);

        mWebView.getSettings().setAppCacheEnabled(true);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setDrawingCacheEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogUtil.d("onPageStarted", String.format("%s - %s", URLs.timestamp(), url));
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                animLoading.setVisibility(View.GONE);
                isWeiXinShared = true;
                LogUtil.d("onPageFinished", String.format("%s - %s", URLs.timestamp(), url));
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                LogUtil.d("onReceivedError",
                        String.format("errorCode: %d, description: %s, url: %s", errorCode, description,
                                failingUrl));
            }
        });

        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
        setWebViewLongListener(true);
        return mWebView;
    }

    protected final HandlerForDetecting mHandlerForDetecting = new HandlerForDetecting(BaseActivity.this);
    protected final HandlerWithAPI mHandlerWithAPI = new HandlerWithAPI(BaseActivity.this);

    public final Runnable mRunnableForDetecting = new Runnable() {
        @Override
        public void run() {
            mHandlerForDetecting.setVariables(mWebView, urlString, sharedPath, assetsPath, relativeAssetsPath);
            Message message = mHandlerForDetecting.obtainMessage();
            message.what = 200;
            mHandlerForDetecting.sendMessage(message);
        }
    };

    /**
     * Instances of static inner classes do not hold an implicit reference to their outer class.
     */
    public class HandlerForDetecting extends Handler {
        private final WeakReference<BaseActivity> weakActivity;
        private final Context mContext;
        private WebView mWebView;
        private String mSharedPath;
        private String mUrlString;
        private String mAssetsPath;
        private String mRelativeAssetsPath;

        public HandlerForDetecting(BaseActivity activity) {
            weakActivity = new WeakReference<>(activity);
            mContext = weakActivity.get();
        }

        public void setVariables(WebView webView, String urlString, String sharedPath, String assetsPath, String relativeAssetsPath) {
            mWebView = webView;
            mUrlString = urlString;
            mSharedPath = sharedPath;
            mUrlString = urlString;
            mAssetsPath = assetsPath;
            mRelativeAssetsPath = relativeAssetsPath;
        }

        protected String loadingPath(String htmlName) {
            return String.format("file:///%s/loading/%s.html", mSharedPath, htmlName);
        }

        private void showWebViewForWithoutNetwork() {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    String urlStringForLoading = loadingPath("400");
                    mWebView.loadUrl(urlStringForLoading);
                }
            });
        }

        public String getLoadLocalHtmlUrl() {
            String htmlName = HttpUtil.urlToFileName(mUrlString);
            String htmlPath = String.format("%s/%s", mAssetsPath, htmlName);
            return htmlPath;
        }

        private void showDialogForDeviceForbided() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(weakActivity.get());
            alertDialog.setTitle("温馨提示");
            alertDialog.setMessage("您被禁止在该设备使用本应用");

            alertDialog.setNegativeButton(
                    "知道了",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                JSONObject configJSON = new JSONObject();
                                configJSON.put(URLs.kIsLogin, false);

                                String userConfigPath = String.format("%s/%s", FileUtil.basePath(mContext), K.kUserConfigFileName);
                                JSONObject userJSON = FileUtil.readConfigFile(userConfigPath);

                                userJSON = ApiHelper.mergeJson(userJSON, configJSON);
                                FileUtil.writeFile(userConfigPath, userJSON.toString());

                                String settingsConfigPath = FileUtil.dirPath(mContext, K.kConfigDirName, K.kSettingConfigFileName);
                                FileUtil.writeFile(settingsConfigPath, userJSON.toString());
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                            Intent intent = new Intent();
                            intent.setClass(mContext, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mContext.startActivity(intent);

                            dialog.dismiss();
                        }
                    }
            );
            alertDialog.show();
        }

        private final Runnable mRunnableWithAPI = new Runnable() {
            @Override
            public void run() {
                LogUtil.d("httpGetWithHeader", String.format("url: %s, assets: %s, relativeAssets: %s", mUrlString, mAssetsPath, mRelativeAssetsPath));
                final Map<String, String> response = ApiHelper.httpGetWithHeader(mAppContext, mUrlString, mAssetsPath, mRelativeAssetsPath);
                Looper.prepare();
                HandlerWithAPI mHandlerWithAPI = new HandlerWithAPI(weakActivity.get());
                mHandlerWithAPI.setVariables(mWebView, mSharedPath, mAssetsPath);
                Message message = mHandlerWithAPI.obtainMessage();
                message.what = Integer.parseInt(response.get(URLs.kCode));
                message.obj = response.get(kPath);

                LogUtil.d("mRunnableWithAPI",
                        String.format("code: %s, path: %s", response.get(URLs.kCode), response.get(kPath)));
                mHandlerWithAPI.sendMessage(message);
                Looper.loop();
            }
        };

        @Override
        public void handleMessage(Message message) {
            BaseActivity activity = weakActivity.get();
            if (activity == null) {
                return;
            }

            switch (message.what) {
                case 200:
                case 201:
                case 304:
                    new Thread(mRunnableWithAPI).start();
                    break;
                case 400:
                case 408:
                    if (new File(getLoadLocalHtmlUrl()).exists()) {
                        mWebView.loadUrl("file:///" + getLoadLocalHtmlUrl());
                        isOffline = true;
                    } else {
                        showWebViewForWithoutNetwork();
                    }
                    break;
                case 401:
                    if (new File(getLoadLocalHtmlUrl()).exists()) {
                        mWebView.loadUrl("file:///" + getLoadLocalHtmlUrl());
                        isOffline = true;
                    } else {
                        showDialogForDeviceForbided();
                    }
                    break;
                default:
                    if (new File(getLoadLocalHtmlUrl()).exists()) {
                        mWebView.loadUrl("file:///" + getLoadLocalHtmlUrl());
                        isOffline = true;
                    } else {
                        showWebViewForWithoutNetwork();
                    }
                    LogUtil.d("UnkownCode", String.format("%d", message.what));
                    break;
            }
        }

    }

    public class HandlerWithAPI extends Handler {
        private final WeakReference<BaseActivity> weakActivity;
        private WebView mWebView;
        private String mSharedPath;
        private String mAssetsPath;

        public HandlerWithAPI(BaseActivity activity) {
            weakActivity = new WeakReference<>(activity);
        }

        public void setVariables(WebView webView, String sharedPath, String assetsPath) {
            mWebView = webView;
            mSharedPath = sharedPath;
            mAssetsPath = assetsPath;
        }

        protected String loadingPath(String htmlName) {
            return String.format("file:///%s/loading/%s.html", mSharedPath, htmlName);
        }

        private void showWebViewForWithoutNetwork() {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    String urlStringForLoading = loadingPath("400");
                    mWebView.loadUrl(urlStringForLoading);
                }
            });
        }

        private void deleteHeadersFile() {
            String headersFilePath = String.format("%s/%s", mAssetsPath, K.kCachedHeaderConfigFileName);
            if ((new File(headersFilePath)).exists()) {
                new File(headersFilePath).delete();
            }
        }

        @Override
        public void handleMessage(final Message message) {
            BaseActivity activity = weakActivity.get();
            if (activity == null || mWebView == null) {
                return;
            }

            switch (message.what) {
                case 200:
                case 304:
                    final String localHtmlPath = String.format("file:///%s", (String) message.obj);

                    weakActivity.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mWebView.loadUrl(localHtmlPath);
                        }
                    });
                    isOffline = false;
                    break;
                case 400:
                case 401:
                case 408:
                    if (new File((String) message.obj).exists()) {
                        weakActivity.get().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mWebView.loadUrl("file:///" + message.obj);
                            }
                        });
                        isOffline = true;
                    } else {
                        showWebViewForWithoutNetwork();
                    }
                    deleteHeadersFile();
                    break;
                default:
                    if (message.obj != null && new File((String) message.obj).exists()) {
                        weakActivity.get().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mWebView.loadUrl("file:///" + message.obj);
                            }
                        });
                        isOffline = true;
                    } else {
                        showWebViewForWithoutNetwork();
                    }
                    String msg = String.format("访问服务器失败（%d)", message.what);
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                    deleteHeadersFile();
                    break;
            }
        }
    }

    public void modifiedUserConfig(boolean isLogin) {
        try {
            JSONObject configJSON = new JSONObject();
            configJSON.put("is_login", isLogin);
            String userConfigPath = String.format("%s/%s", FileUtil.basePath(mAppContext), K.kUserConfigFileName);
            JSONObject userJSON = FileUtil.readConfigFile(userConfigPath);

            userJSON = ApiHelper.mergeJson(userJSON, configJSON);
            FileUtil.writeFile(userConfigPath, userJSON.toString());

            String settingsConfigPath = FileUtil.dirPath(mAppContext, K.kConfigDirName, K.kSettingConfigFileName);
            FileUtil.writeFile(settingsConfigPath, userJSON.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 托管在蒲公英平台，对比版本号检测是否版本更新
     * 对比 build 值，只准正向安装提示
     * 奇数: 测试版本，仅提示
     * 偶数: 正式版本，点击安装更新
     */
    public void checkPgyerVersionUpgrade(final Activity activity, final boolean isShowToast) {
        PgyUpdateManager.register(activity, "com.intfocus.yhdev.fileprovider", new UpdateManagerListener() {
            @Override
            public void onUpdateAvailable(final String result) {
                try {
                    final AppBean appBean = getAppBeanFromString(result);

                    if (result == null || result.isEmpty()) {
                        return;
                    }

                    PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    int currentVersionCode = packageInfo.versionCode;
                    JSONObject response = new JSONObject(result);
                    String message = response.getString("message");

                    JSONObject responseVersionJSON = response.getJSONObject(URLs.kData);
                    int newVersionCode = responseVersionJSON.getInt(kVersionCode);

                    String newVersionName = responseVersionJSON.getString("versionName");

                    if (currentVersionCode >= newVersionCode) {
                        return;
                    }

                    String pgyerVersionPath = String.format("%s/%s", FileUtil.basePath(mAppContext), K.kPgyerVersionConfigFileName);
                    FileUtil.writeFile(pgyerVersionPath, result);

                    if (newVersionCode % 2 == 1) {
                        if (isShowToast) {
                            ToastUtils.INSTANCE.show(mAppContext, String.format("有发布测试版本%s(%s)", newVersionName, newVersionCode), ToastColor.SUCCESS);
                        }

                        return;
                    } else if (HttpUtil.isWifi(activity) && newVersionCode % 10 == 8) {

                        startDownloadTask(activity, appBean.getDownloadURL());

                        return;
                    }
                    new AlertDialog.Builder(activity)
                            .setTitle("版本更新")
                            .setMessage(message.isEmpty() ? "无升级简介" : message)
                            .setPositiveButton(
                                    "确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startDownloadTask(activity, appBean.getDownloadURL());
                                        }
                                    })
                            .setNegativeButton("下一次",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                            .setCancelable(false)
                            .show();

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNoUpdateAvailable() {
                if (isShowToast) {
                    ToastUtils.INSTANCE.show(mAppContext, "已是最新版本");
                }
            }
        });
    }

    public class JavaScriptBase {
        /*
         * JS 接口，暴露给JS的方法使用@JavascriptInterface装饰
         */
        @JavascriptInterface
        public void refreshBrowser() {
            new Thread(mRunnableForDetecting).start();
        }

        @JavascriptInterface
        public void openURLWithSystemBrowser(final String url) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (url == null || (!url.startsWith("http://") && !url.startsWith("https://"))) {
                        ToastUtils.INSTANCE.show(mAppContext, String.format("无效链接: %s", url));
                        return;
                    }
                    Intent browserIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            });
        }
    }
}
