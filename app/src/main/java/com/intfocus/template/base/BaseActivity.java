package com.intfocus.template.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/29 上午11:56
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        loadXml();
        initViews();
        initData();
        initListener();
    }
    protected abstract void loadXml();

    /**
     * 初始化控件
     */
    protected void initViews() {}

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 设置监听
     */
    protected void initListener(){}

}
