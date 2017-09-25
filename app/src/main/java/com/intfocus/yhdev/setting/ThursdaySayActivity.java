package com.intfocus.yhdev.setting;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.intfocus.yhdev.base.BaseActivity;
import com.intfocus.yhdev.R;
import com.intfocus.yhdev.util.K;
import com.intfocus.yhdev.util.PrivateURLs;
import com.intfocus.yhdev.util.URLs;

/**
 * Created by 40284 on 2016/9/10.
 */
public class ThursdaySayActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thursday_say);

        mWebView = (WebView) findViewById(R.id.browser);
        initSubWebView();

        animLoading.setVisibility(View.VISIBLE);
        setWebViewLongListener(false);
        urlString = String.format(K.kThursdaySayMobilePath, PrivateURLs.kBaseUrl, URLs.currentUIVersion(ThursdaySayActivity.this));
        new Thread(mRunnableForDetecting).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMyApp.setCurrentActivity(this);
    }

    public void dismissActivity(View v) {
        ThursdaySayActivity.this.onBackPressed();
    }
}
