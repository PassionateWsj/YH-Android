package com.intfocus.yhdev.subject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.intfocus.yhdev.CommentActivity;
import com.intfocus.yhdev.R;
import com.intfocus.yhdev.base.BaseActivity;
import com.intfocus.yhdev.constant.ToastColor;
import com.intfocus.yhdev.dashboard.mine.adapter.FilterMenuAdapter;
import com.intfocus.yhdev.data.response.filter.Menu;
import com.intfocus.yhdev.data.response.filter.MenuItem;
import com.intfocus.yhdev.data.response.filter.MenuResult;
import com.intfocus.yhdev.filter.MyFilterDialogFragment;
import com.intfocus.yhdev.util.ActionLogUtil;
import com.intfocus.yhdev.util.ApiHelper;
import com.intfocus.yhdev.util.FileUtil;
import com.intfocus.yhdev.util.ImageUtil;
import com.intfocus.yhdev.util.K;
import com.intfocus.yhdev.util.LogUtil;
import com.intfocus.yhdev.util.ToastUtils;
import com.intfocus.yhdev.util.URLs;
import com.intfocus.yhdev.view.addressselector.FilterPopupWindow;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.webkit.WebView.enableSlowWholeDocumentDraw;
import static java.lang.String.format;

public class SubjectActivity extends BaseActivity implements FilterMenuAdapter.FilterMenuListener, FilterPopupWindow.MenuLisenter, MyFilterDialogFragment.FilterLisenter {

    private String templateID;
    private String bannerName, link;
    private String groupID;
    private String objectID;
    private String objectType;
    private String userNum;
    private RelativeLayout bannerView;
    private Context mContext;
    private TextView mTitle;
    private ImageView iv_BannerBack;
    private TextView tv_BannerBack;
    private ImageView iv_BannerSetting;

    /**
     * 筛选
     *
     * @param savedInstanceState
     */
    private RelativeLayout rlAddressFilter;
    private TextView tvLocationAddress;
    private TextView tvAddressFilter;
    private RecyclerView filterRecyclerView;
    private View viewLine;
    /**
     * 地址选择
     */
    private List<MenuItem> locationDatas;
    /**
     * 菜单
     */
    private int currentPosition = 0;//当前展开的menu
    private List<MenuItem> menuDatas;
    private FilterMenuAdapter menuAdpter;
    private FilterPopupWindow filterPopupWindow;
    private FrameLayout mWebFrameLayout;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
         * 判断当前设备版本，5.0 以 上 Android 系统使用才 enableSlowWholeDocumentDraw();
		 */
        if (Build.VERSION.SDK_INT > 20) {
            enableSlowWholeDocumentDraw();
        }
        setContentView(R.layout.activity_subject);


        initData();

        initView();

        initAdapter();

        initListener();

        initActiongBar();

        initSubWebView();

        isWeiXinShared = false;

        mMyApp.setCurrentActivity(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mContext = this;
        groupID = mUserSP.getString(URLs.kGroupId, "-2");
        userNum = mUserSP.getString(URLs.kUserNum, "not-set");
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mWebFrameLayout = (FrameLayout) findViewById(R.id.browser);
        mWebView = new WebView(getApplicationContext());
        mWebFrameLayout.addView(mWebView, 0);

        iv_BannerBack = (ImageView) findViewById(R.id.iv_banner_back);
        tv_BannerBack = (TextView) findViewById(R.id.tv_banner_back);
        iv_BannerSetting = (ImageView) findViewById(R.id.iv_banner_setting);
        rlAddressFilter = (RelativeLayout) findViewById(R.id.rl_address_filter);
        tvLocationAddress = (TextView) findViewById(R.id.tv_location_address);
        tvAddressFilter = (TextView) findViewById(R.id.tv_address_filter);
        filterRecyclerView = (RecyclerView) findViewById(R.id.filter_recycler_view);
        viewLine = findViewById(R.id.view_line);
        animLoading = (RelativeLayout) findViewById(R.id.anim_loading);
        bannerView = (RelativeLayout) findViewById(R.id.rl_action_bar);
        mTitle = (TextView) findViewById(R.id.tv_banner_title);
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        filterRecyclerView.setLayoutManager(mLayoutManager);
        menuAdpter = new FilterMenuAdapter(mContext, menuDatas, this);
        filterRecyclerView.setAdapter(menuAdpter);
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        // 筛选按钮监听
        tvAddressFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogFragment();
            }
        });
    }

    private void initActiongBar() {
        Intent intent = getIntent();
        link = intent.getStringExtra(URLs.kLink);
        templateID = intent.getStringExtra(URLs.kTemplatedId);
        bannerName = intent.getStringExtra(URLs.kBannerName);
        objectID = intent.getStringExtra(URLs.kObjectId);
        objectType = intent.getStringExtra(URLs.kObjectType);
        mTitle.setText(bannerName);

        iv_BannerSetting.setVisibility(View.VISIBLE);
    }

    /**
     * WebView 初始化设置
     *
     * @return
     */
    @Override
    public WebView initSubWebView() {

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");

        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);
        webSettings.setAllowFileAccess(true);

        webSettings.setAppCacheEnabled(true);

        mWebView.setDrawingCacheEnabled(true);

        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
        setWebViewLongListener(true);

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

                //是否有筛选数据，有就显示出来
                if (locationDatas != null && locationDatas.size() > 0) {
                    rlAddressFilter.setVisibility(View.VISIBLE);
                    LogUtil.d("location", locationDatas.size() + "");
                } else {
                    rlAddressFilter.setVisibility(View.GONE);
                }
                if (menuDatas != null && menuDatas.size() > 0) {
                    LogUtil.d("faster_select", menuDatas.size() + "");
                    filterRecyclerView.setVisibility(View.VISIBLE);
                    viewLine.setVisibility(View.VISIBLE);
                    menuAdpter.setData(menuDatas);
                } else {
                    filterRecyclerView.setVisibility(View.GONE);
                    viewLine.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                LogUtil.d("onReceivedError",
                        String.format("errorCode: %d, description: %s, url: %s", errorCode, description,
                                failingUrl));
            }
        });

        mWebView.requestFocus();
        mWebView.setVisibility(View.VISIBLE);
        mWebView.addJavascriptInterface(new JavaScriptInterface(), URLs.kJSInterfaceName);
        animLoading.setVisibility(View.VISIBLE);

        loadHtml();
        return mWebView;
    }

    /**
     * 标题栏点击设置按钮显示下拉菜单
     */
    public void launchDropMenuActivity(View v) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_menu_v2, null);
        x.view().inject(this, contentView);

        //设置弹出框的宽度和高度
        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        //设置可以点击
        popupWindow.setTouchable(true);
        //进入退出的动画
        popupWindow.showAsDropDown(v);
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
            case R.id.ll_copylink:
                // 拷贝外部链接
                actionCopyLink(view);
                break;
            case R.id.ll_refresh:
                animLoading.setVisibility(View.VISIBLE);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 横屏时隐藏标题栏、导航栏
        Boolean isLandscape = (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);

        bannerView.setVisibility(isLandscape ? View.GONE : View.VISIBLE);
        if (isLandscape) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void loadHtml() {
        WebSettings webSettings = mWebView.getSettings();
            // format: /mobile/v1/group/:group_id/template/:template_id/report/:report_id
            // deprecated
            // format: /mobile/report/:report_id/group/:group_id
            String urlPath = format(link.replace("%@", "%s"), groupID);
            urlString = String.format("%s%s", K.kBaseUrl, urlPath);
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

            Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    ApiHelper.reportData(mAppContext, String.format("%s", groupID), templateID, objectID);
                    String jsFileName = "";

                    // 模板 4 的 groupID 为 0
                    if (Integer.valueOf(templateID) == 4) {
                        jsFileName = String.format("group_%s_template_%s_report_%s.js", "0", templateID, objectID);
                    } else {
                        jsFileName = String.format("group_%s_template_%s_report_%s.js", groupID, templateID, objectID);
                    }
                    String javascriptPath = String.format("%s/assets/javascripts/%s", sharedPath, jsFileName);
                    subscriber.onNext(javascriptPath);
                    subscriber.onCompleted();
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }

                        @Override
                        public void onNext(String javascriptPath) {
                            if (new File(javascriptPath).exists()) {
                                mHandlerForDetecting.setVariables(mWebView, urlString, sharedPath, assetsPath, relativeAssetsPath);
                                Message message = mHandlerForDetecting.obtainMessage();
                                message.what = 200;
                                mHandlerForDetecting.sendMessage(message);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle("温馨提示")
                                        .setMessage("报表数据下载失败,不再加载网页")
                                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                SubjectActivity.this.finish();
                                            }
                                        });
                                builder.show();
                            }
                        }
                    });
    }

    /**
     * 拷贝链接
     */
    public void actionCopyLink(View v) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setText(link);
        ToastUtils.INSTANCE.show(mContext, "链接已拷贝", ToastColor.SUCCESS);
    }

    /**
     * 分享截图至微信
     */
    public void actionShare2Weixin(View v) {
        if (link.toLowerCase().endsWith(".pdf")) {
            ToastUtils.INSTANCE.show(mAppContext, "暂不支持 PDF 分享");
            return;
        }

        if (!isWeiXinShared) {
            ToastUtils.INSTANCE.show(mAppContext, "网页加载完成,才能使用分享功能");
            return;
        }

        Bitmap bmpScrennShot = ImageUtil.takeScreenShot(SubjectActivity.this);
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
            logParams = new JSONObject();
            logParams.put("action", "分享");
            ActionLogUtil.actionLog(mContext, logParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        Intent intent = new Intent(mContext, CommentActivity.class);
        intent.putExtra(URLs.kBannerName, bannerName);
        intent.putExtra(URLs.kObjectId, objectID);
        intent.putExtra(URLs.kObjectType, objectType);
        mContext.startActivity(intent);
    }

    /*
     * 返回
     */
    @Override
    public void dismissActivity(View v) {
        SubjectActivity.this.onBackPressed();
    }

    @Override
    public void onBackPressed() {
            finish();
    }

    public void refresh(View v) {
        animLoading.setVisibility(View.VISIBLE);
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                    String urlKey;
                    if (urlString != null && !urlString.isEmpty()) {
                        urlKey = urlString.contains("?") ? TextUtils.split(urlString, "?")[0] : urlString;
                        ApiHelper.clearResponseHeader(urlKey, assetsPath);
                    }
                    urlKey = String.format(K.kReportDataAPIPath, K.kBaseUrl, groupID, templateID, objectID);
                    ApiHelper.clearResponseHeader(urlKey, FileUtil.sharedPath(mAppContext));
                    subscriber.onNext(ApiHelper.reportData(mAppContext, groupID, templateID, objectID));
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        loadHtml();
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onNext(Boolean reportDataState) {
                        if (reportDataState) {
                            mHandlerForDetecting.setVariables(mWebView, urlString, sharedPath, assetsPath, relativeAssetsPath);
                            Message message = mHandlerForDetecting.obtainMessage();
                            message.what = 200;
                            mHandlerForDetecting.sendMessage(message);
                        } else {
                            String urlStringForLoading = String.format("file:///%s/loading/%s.html", sharedPath, "400");
                            mWebView.loadUrl(urlStringForLoading);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, intent);
    }

    private void showDialogFragment() {
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("dialogFragment");
        if (fragment != null) {
            //为了不重复显示dialog，在显示对话框之前移除正在显示的对话框
            mFragTransaction.remove(fragment);
        }
        MyFilterDialogFragment dialogFragment = new MyFilterDialogFragment((ArrayList<MenuItem>) locationDatas, this);
        dialogFragment.show(mFragTransaction, "dialogFragment"); //显示一个Fragment并且给该Fragment添加一个Tag，可通过findFragmentByTag找到该Fragment
    }

    /**
     * 点击普通筛选栏
     */
    @Override
    public void itemClick(int position) {
        //标记点击位置
        menuDatas.get(position).setArrorDirection(true);
        menuAdpter.setData(menuDatas);
        currentPosition = position;
        showMenuPop(position);
    }

    private void showMenuPop(int position) {
        if (filterPopupWindow == null) {
            initMenuPopup(position);
        } else {
            filterPopupWindow.upDateDatas(menuDatas.get(position).getData());
        }
//        viewBg.visibility = View.VISIBLE
        filterPopupWindow.showAsDropDown(viewLine);
        filterPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                for (MenuItem menuItem : menuDatas) {
                    menuItem.setArrorDirection(false);
                }
                menuAdpter.setData(menuDatas);
            }
        });
    }

    private void initMenuPopup(int position) {
        filterPopupWindow = new FilterPopupWindow(this, menuDatas.get(position).getData(), this);
        filterPopupWindow.init();
    }

    /**
     * 普通排序列表点击
     *
     * @param position
     */
    @Override
    public void menuItemClick(int position) {
        for (MenuItem menuItem : menuDatas.get(currentPosition).getData()) {
            menuItem.setArrorDirection(false);
        }

        //标记点击位置
        menuDatas.get(currentPosition).getData().get(position).setArrorDirection(true);
        filterPopupWindow.dismiss();
    }

    @Override
    public void complete(@NotNull ArrayList<MenuItem> data) {
        try {
            String addStr = "";
            for (int i = 0; i < data.size(); i++) {
                addStr += data.get(i).getName() + "||";
            }

            addStr = addStr.substring(0, addStr.length() - 2);
            String selectedItemPath = String.format("%s.selected_item", FileUtil.reportJavaScriptDataPath(SubjectActivity.this, groupID, templateID, objectID));
            FileUtil.writeFile(selectedItemPath, addStr);

            animLoading.setVisibility(View.VISIBLE);
            loadHtml();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * JavaScript 接口类
     *
     * @JavascriptInterface装饰方法 暴露给JS调用
     */
    private class JavaScriptInterface extends JavaScriptBase {
        @JavascriptInterface
        public void storeTabIndex(final String pageName, final int tabIndex) {
            try {
                String filePath = FileUtil.dirPath(mAppContext, K.kConfigDirName, K.kTabIndexConfigFileName);

                JSONObject config = new JSONObject();
                if ((new File(filePath).exists())) {
                    String fileContent = FileUtil.readFile(filePath);
                    config = new JSONObject(fileContent);
                }
                config.put(pageName, tabIndex);

                FileUtil.writeFile(filePath, config.toString());
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public int restoreTabIndex(final String pageName) {
            int tabIndex = 0;
            try {
                String filePath = FileUtil.dirPath(mAppContext, K.kConfigDirName, K.kTabIndexConfigFileName);

                JSONObject config = new JSONObject();
                if ((new File(filePath).exists())) {
                    String fileContent = FileUtil.readFile(filePath);
                    config = new JSONObject(fileContent);
                }
                tabIndex = config.getInt(pageName);
            } catch (JSONException e) {
            }

            return tabIndex < 0 ? 0 : tabIndex;
        }

        @JavascriptInterface
        public void jsException(final String ex) {
            /*
             * 用户行为记录, 单独异常处理，不可影响用户体验
             */
            try {
                logParams = new JSONObject();
                logParams.put(URLs.kAction, "JS异常");
                logParams.put("obj_id", objectID);
                logParams.put(URLs.kObjType, objectType);
                logParams.put(URLs.kObjTitle, String.format("主题页面/%s/%s", bannerName, ex));
                ActionLogUtil.actionLog(mContext, logParams);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void reportSearchItems(final String arrayString) {
            String str = arrayString;
        }

        @JavascriptInterface
        public void reportSearchItemsV2(final String arrayString) {
            if (!TextUtils.isEmpty(arrayString)) {
                MenuResult msg = new Gson().fromJson(arrayString, MenuResult.class);
                if (msg != null && msg.getData() != null && msg.getData().size() > 0) {
                    for (Menu menu : msg.getData()) {
                        if ("location".equals(menu.getType())) {
                            locationDatas = menu.getData();
                            String selectedItemPath = String.format("%s.selected_item", FileUtil.reportJavaScriptDataPath(SubjectActivity.this, groupID, templateID, objectID));
                            if (!new File(selectedItemPath).exists()) {
                                if (locationDatas != null) {
                                    tvLocationAddress.setText(menu.getCurrent_location().getDisplay());
                                }
                            }
                        }
                        if ("faster_select".equals(menu.getType())) {
                            menuDatas = menu.getData();
                        }
                    }
                }
            }
        }

        @JavascriptInterface
        public void setBannerTitle(final String bannerTitle) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!"".equals(bannerTitle)) {
                        mTitle.setText(bannerTitle);
                    } else {
                        mTitle.setText("");
                    }
                }
            });
        }

        @JavascriptInterface
        public String reportSelectedItem() {
            String item = "";
            String selectedItemPath = String.format("%s.selected_item", FileUtil.reportJavaScriptDataPath(SubjectActivity.this, groupID, templateID, objectID));
            if (new File(selectedItemPath).exists()) {
                item = FileUtil.readFile(selectedItemPath);
                final String filterText = item;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvLocationAddress.setText(filterText);
                    }
                });
            }

            return item;
        }

        @Override
        @JavascriptInterface
        public void refreshBrowser() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    animLoading.setVisibility(View.VISIBLE);
                    loadHtml();
                }
            });
        }

        @JavascriptInterface
        public void toggleShowBanner(final String state) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bannerView.setVisibility("show".equals(state) ? View.VISIBLE : View.GONE);
                }
            });
        }

        @JavascriptInterface
        public String getLocation() {
            String location = ApiHelper.getLocation(mAppContext);
            return location;
        }

        @JavascriptInterface
        public void toggleShowBannerBack(final String state) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    iv_BannerBack.setVisibility("show".equals(state) ? View.VISIBLE : View.GONE);
                    tv_BannerBack.setVisibility("show".equals(state) ? View.VISIBLE : View.GONE);
                }
            });
        }

        @JavascriptInterface
        public void toggleShowBannerMenu(final String state) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    iv_BannerSetting.setVisibility("show".equals(state) ? View.VISIBLE : View.GONE);
                }
            });
        }

        @JavascriptInterface
        public void goBack(String info) {
            mWebView.goBack();
        }

        @JavascriptInterface
        public void closeSubjectView() {
            finish();
        }

        @JavascriptInterface
        public void showAlert(final String title, final String content) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SubjectActivity.this);
                    builder.setTitle(title)
                            .setMessage(content)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }
            });
        }
    }
}
