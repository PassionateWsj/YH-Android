package com.intfocus.yonghuitest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;

import com.intfocus.yonghuitest.base.BaseActivity;
import com.intfocus.yonghuitest.data.request.CommentBody;
import com.intfocus.yonghuitest.data.response.BaseResult;
import com.intfocus.yonghuitest.net.ApiException;
import com.intfocus.yonghuitest.net.CodeHandledSubscriber;
import com.intfocus.yonghuitest.net.RetrofitUtil;
import com.intfocus.yonghuitest.util.ActionLogUtil;
import com.intfocus.yonghuitest.util.ApiHelper;
import com.intfocus.yonghuitest.util.K;
import com.intfocus.yonghuitest.util.ToastUtils;
import com.intfocus.yonghuitest.util.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends BaseActivity {

    private String bannerName;
    private int objectID;
    private int objectType;
    private int loadCount = 0;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        TextView mTitle = (TextView) findViewById(R.id.bannerTitle);
        mWebView = (WebView) findViewById(R.id.browser);
        initSubWebView();

        mWebView.requestFocus();
        mWebView.addJavascriptInterface(new JavaScriptInterface(), URLs.kJSInterfaceName);
        mWebView.loadUrl(urlStringForLoading);

        Intent intent = getIntent();
        bannerName = intent.getStringExtra(URLs.kBannerName);
        objectID = intent.getIntExtra(URLs.kObjectId, -1);
        objectType = intent.getIntExtra(URLs.kObjectType, -1);

        mTitle.setText(bannerName);
        urlString = String.format(K.kCommentMobilePath, K.kBaseUrl, URLs.currentUIVersion(mAppContext), objectID, objectType);

        new Thread(mRunnableForDetecting).start();
    }

    protected void onResume() {
        mMyApp.setCurrentActivity(this);
        super.onResume();
    }

    /*
     * 返回
     */
    public void dismissActivity(View v) {
        CommentActivity.this.onBackPressed();
    }

    private class JavaScriptInterface extends JavaScriptBase {
        /*
         * JS 接口，暴露给JS的方法使用@JavascriptInterface装饰
         */
        @JavascriptInterface
        public void writeComment(final String content) {
            CommentBody commentBody = new CommentBody();
            commentBody.setUserNum(mUserSP.getString(URLs.kUserNum, "0"));
            commentBody.setContent(content);

            RetrofitUtil.getHttpService().submitComment(commentBody)
                    .compose(new RetrofitUtil.CommonOptions<BaseResult>())
                    .subscribe(new CodeHandledSubscriber<BaseResult>() {
                        @Override
                        public void onError(ApiException apiException) {
                        }

                        @Override
                        public void onBusinessNext(BaseResult data) {
                            ToastUtils.INSTANCE.show(mAppContext, "评论成功");
                        }

                        @Override
                        public void onCompleted() {
                        }
                    });


            /*
             * 用户行为记录, 单独异常处理，不可影响用户体验
             */
            try {
                logParams = new JSONObject();
                logParams.put(URLs.kAction, "评论");
                logParams.put(URLs.kObjTitle, bannerName);
                ActionLogUtil.actionLog(mAppContext, logParams);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                logParams.put(URLs.kObjTitle, String.format("评论页面/%s/%s", bannerName, ex));
                ActionLogUtil.actionLog(mAppContext, logParams);

                //点击两次还是有异常 异常报出
                if (loadCount < 2) {
                    showWebViewExceptionForWithoutNetwork();
                    loadCount++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
