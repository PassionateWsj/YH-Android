package com.intfocus.spy_template.business.dashboard.mine.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.intfocus.spy_template.R;
import com.intfocus.spy_template.general.base.BaseActivity;
import com.intfocus.spy_template.general.util.K;
import com.intfocus.spy_template.general.util.PrivateURLs;
import com.intfocus.spy_template.general.util.URLs;

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

    @Override
    public void dismissActivity(View v) {
        ThursdaySayActivity.this.onBackPressed();
    }
}
