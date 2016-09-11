package com.intfocus.yh_android;

import android.os.Bundle;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import com.intfocus.yh_android.util.PrivateURLs;
import com.intfocus.yh_android.util.URLs;

/**
 * Created by 40284 on 2016/9/10.
 */
public class ThursdaySayActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        pullToRefreshWebView = (PullToRefreshWebView) findViewById(R.id.browser);
        initWebView();
        setPullToRefreshWebView(true);

        mWebView.requestFocus();
        mWebView.loadUrl(urlStringForLoading);
        urlString = String.format(URLs.BLOG_PLINK_PATH, PrivateURLs.kBaseUrl);
        new Thread(mRunnableForDetecting).start();
    }

    public void dismissActivity(View v) {
        ThursdaySayActivity.this.onBackPressed();
    }
}
