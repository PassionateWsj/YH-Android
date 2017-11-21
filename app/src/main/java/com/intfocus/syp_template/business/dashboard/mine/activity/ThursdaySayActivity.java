package com.intfocus.syp_template.business.dashboard.mine.activity;

import android.os.Bundle;
import android.view.View;

import com.intfocus.syp_template.general.constant.ConfigConstants;
import com.intfocus.syp_template.general.util.K;
import com.intfocus.syp_template.general.util.URLs;
import com.tencent.smtt.sdk.WebView;

import com.intfocus.syp_template.R;
import com.intfocus.syp_template.general.base.BaseActivity;
import com.intfocus.syp_template.general.constant.ConfigConstants;
import com.intfocus.syp_template.general.util.K;
import com.intfocus.syp_template.general.util.URLs;

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
//        urlString = String.format(K.K_THURSDAY_SAY_MOBILE_PATH, ConfigConstants.kBaseUrl, URLs.currentUIVersion(ThursdaySayActivity.this));
        urlString = "http://111.231.113.158:8080/websites/cav/quan2.html";
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