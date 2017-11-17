package com.intfocus.yhdev.business.dashboard.mine.activity;

import android.os.Bundle;
import android.view.View;
import com.tencent.smtt.sdk.WebView;

import com.intfocus.yhdev.R;
import com.intfocus.yhdev.general.base.BaseActivity;
import com.intfocus.yhdev.general.constant.ConfigConstants;
import com.intfocus.yhdev.general.util.K;
import com.intfocus.yhdev.general.util.URLs;

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
        urlString = String.format(K.K_THURSDAY_SAY_MOBILE_PATH, ConfigConstants.kBaseUrl, URLs.currentUIVersion(ThursdaySayActivity.this));
//        urlString = "http://123.59.75.85:8080/yhportal/appClientReport/personnelTracking.pdf";
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
