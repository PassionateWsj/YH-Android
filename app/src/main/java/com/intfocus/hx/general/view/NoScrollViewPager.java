package com.intfocus.hx.general.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.intfocus.hx.general.constant.ConfigConstants;

/**
 * Created by liuruilin on 2017/7/27.
 */

public class NoScrollViewPager extends ViewPager {
    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ConfigConstants.DASHBOARD_ENABLE_HORIZONTAL_SCROLL) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }
}
