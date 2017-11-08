package com.intfocus.hxtest.general.view;

/**
 * Created by liuruilin on 2017/7/26.
 */

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.IHeaderView;
import com.lcodecore.tkrefreshlayout.OnAnimEndListener;
import com.lcodecore.tkrefreshlayout.R.id;
import com.lcodecore.tkrefreshlayout.R.layout;

public class DefaultRefreshView extends FrameLayout implements IHeaderView {
    private ImageView refreshArrow;
    private ImageView loadingView;
    private TextView refreshTextView;
    private String pullDownStr;
    private String releaseRefreshStr;
    private String refreshingStr;

    public DefaultRefreshView(Context context) {
        this(context, (AttributeSet) null);
    }

    public DefaultRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.pullDownStr = "";
        this.releaseRefreshStr = "";
        this.refreshingStr = "";
        this.init();
    }

    private void init() {
        View rootView = View.inflate(this.getContext(), layout.view_sinaheader, (ViewGroup) null);
        this.refreshArrow = (ImageView) rootView.findViewById(id.iv_arrow);
        this.refreshTextView = (TextView) rootView.findViewById(id.tv);
        this.loadingView = (ImageView) rootView.findViewById(id.iv_loading);
        this.addView(rootView);
    }

    public void setArrowResource(@DrawableRes int resId) {
        this.refreshArrow.setImageResource(resId);
    }

    public void setTextColor(@ColorInt int color) {
        this.refreshTextView.setTextColor(color);
    }

    public void setPullDownStr(String pullDownStr1) {
        this.pullDownStr = pullDownStr1;
    }

    public void setReleaseRefreshStr(String releaseRefreshStr1) {
        this.releaseRefreshStr = releaseRefreshStr1;
    }

    public void setRefreshingStr(String refreshingStr1) {
        this.refreshingStr = refreshingStr1;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onPullingDown(float fraction, float maxHeadHeight, float headHeight) {
        if (fraction < 1.0F) {
            this.refreshTextView.setText(this.pullDownStr);
        }

        if (fraction > 1.0F) {
            this.refreshTextView.setText(this.releaseRefreshStr);
        }

        this.refreshArrow.setRotation(fraction * headHeight / maxHeadHeight * 180.0F);
    }

    @Override
    public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {
        if (fraction < 1.0F) {
            this.refreshTextView.setText(this.pullDownStr);
            this.refreshArrow.setRotation(fraction * headHeight / maxHeadHeight * 180.0F);
            if (this.refreshArrow.getVisibility() == View.GONE) {
                this.refreshArrow.setVisibility(View.VISIBLE);
                this.loadingView.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void startAnim(float maxHeadHeight, float headHeight) {
        this.refreshTextView.setText(this.refreshingStr);
        this.refreshArrow.setVisibility(View.GONE);
        this.loadingView.setVisibility(View.VISIBLE);
        ((AnimationDrawable) this.loadingView.getDrawable()).start();
    }

    @Override
    public void onFinish(OnAnimEndListener listener) {
        listener.onAnimEnd();
    }

    @Override
    public void reset() {
        this.refreshArrow.setVisibility(View.VISIBLE);
        this.loadingView.setVisibility(View.GONE);
        this.refreshTextView.setText(this.pullDownStr);
    }
}

