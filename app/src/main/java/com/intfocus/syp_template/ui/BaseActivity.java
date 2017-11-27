package com.intfocus.syp_template.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.intfocus.syp_template.SYPApplication;

/**
 *
 * @author lijunjie
 * @date 16/1/14
 */
public class BaseActivity extends FragmentActivity {
    public SYPApplication mApp;
    public SharedPreferences mUserSP;
    public Context mAppContext;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (SYPApplication) getApplication();
        mAppContext = mApp.getApplicationContext();
        mUserSP = getSharedPreferences("UserBean", MODE_PRIVATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 返回
     */
    public void dismissActivity(View v) {
        super.onBackPressed();
    }
}
