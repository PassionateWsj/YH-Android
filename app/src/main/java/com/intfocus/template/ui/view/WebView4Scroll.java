package com.intfocus.template.ui.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;

import com.tencent.smtt.sdk.WebView;

/**
 * ****************************************************
 * author jameswong
 * created on: 18/01/23 下午4:15
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
public class WebView4Scroll extends WebView {
    private SwipeRefreshLayout swipeRefreshLayout;

    public WebView4Scroll(Context context, SwipeRefreshLayout swipeRefreshLayout){
        super(context);
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (this.getWebScrollY() == 0){
            swipeRefreshLayout.setEnabled(true);
        }else {
            swipeRefreshLayout.setEnabled(false);
        }
    }
}
