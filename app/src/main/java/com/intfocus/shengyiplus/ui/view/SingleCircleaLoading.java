package com.intfocus.shengyiplus.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.FloatRange;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.intfocus.shengyiplus.util.DisplayUtil;

/**
 * @author liuruilin
 * @data 2017/12/12
 * @describe
 */

public class SingleCircleaLoading implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

    /**
     * View 默认尺寸
     */
    private static final float DEFAULT_SIZE = 56.0f;

    /**
     * 动画开始延迟
     */
    private static final long ANIMATION_START_DELAY = 333;

    /**
     * 动画持续时间
     */
    private static final long ANIMATION_DURATION = 1333;

    /**
     * 外圆角度
     */
    private static final int OUTER_CIRCLE_ANGLE = 320;

    /**
     * 最终动画阶段
     */
    private static final int FINAL_STATE = 2;

    /**
     * 当前动画阶段
     */
    private int currentAnimationState = 0;

    /**
     * 旋转开始角度
     */
    private int mStartRotateAngle;

    /**
     * 旋转角度
     */
    private int mRotateAngle;

    private float mAllSize;
    private float mViewWidth;
    private float mViewHeight;

    private Paint mStrokePaint;
    private RectF mOuterCircleRectF;
    private Drawable.Callback mCallback;
    private ValueAnimator mFloatValueAnimator;
    private long mDuration;

    public void init(Context context) {
        mAllSize = DisplayUtil.dip2px(context, DEFAULT_SIZE * 0.5f - 10);
        mViewWidth = DisplayUtil.dip2px(context, DEFAULT_SIZE);
        mViewHeight = DisplayUtil.dip2px(context, DEFAULT_SIZE);
        mDuration = ANIMATION_DURATION;

        initAnimators();
    }

    private void initAnimators() {
        mFloatValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mFloatValueAnimator.setRepeatCount(Animation.INFINITE);
        mFloatValueAnimator.setDuration(mDuration);
        mFloatValueAnimator.setStartDelay(ANIMATION_START_DELAY);
        mFloatValueAnimator.setInterpolator(new LinearInterpolator());
    }

    public void initParams(Context context) {
        /* 最大尺寸 */
        float outR = getAllSize();
        /* 小圆尺寸 */
        float inR = outR * 0.6f;
        /* 初始化画笔 */
        initPaint(inR * 0.4f);
        /* 旋转角度 */
        mStartRotateAngle = 0;
        /* 圆范围 */
        mOuterCircleRectF = new RectF();
        mOuterCircleRectF.set(getViewCenterX() - outR, getViewCenterY() - outR, getViewCenterX() + outR, getViewCenterY() + outR);
    }

    private void initPaint(float lineWidth) {
        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(lineWidth);
        mStrokePaint.setColor(Color.WHITE);
//        mStrokePaint.setAlpha(0);
        mStrokePaint.setDither(true);
        mStrokePaint.setFilterBitmap(true);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        mStrokePaint.setStrokeJoin(Paint.Join.ROUND);
    }

    private float getAllSize() {
        return mAllSize;
    }

    private void onDraw(Canvas canvas) {
        canvas.save();
        //外圆
        canvas.drawArc(mOuterCircleRectF, mStartRotateAngle % 360, mRotateAngle % 360, false, mStrokePaint);
        canvas.restore();
    }

    private void setAlpha(int alpha)
    {
        mStrokePaint.setAlpha(alpha);
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        if (++currentAnimationState > FINAL_STATE)
        {
            /* 还原到第一阶段 */
            currentAnimationState = 0;
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        computeUpdateValue(animation, (float) animation.getAnimatedValue());
        invalidateSelf();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    private float getIntrinsicHeight() {
        return mViewHeight;
    }

    private float getIntrinsicWidth() {
        return mViewWidth;
    }

    private float getViewCenterX() {
        return getIntrinsicWidth() * 0.5f;
    }

    private float getViewCenterY() {
        return getIntrinsicHeight() * 0.5f;
    }

    private void computeUpdateValue(ValueAnimator animation, @FloatRange(from = 0.0, to = 1.0) float animatedValue) {
        mStartRotateAngle = (int) (360 * animatedValue);
        switch (currentAnimationState)
        {
            case 0:
                mRotateAngle = (int) (OUTER_CIRCLE_ANGLE * animatedValue);
                break;
            case 1:
                mRotateAngle = OUTER_CIRCLE_ANGLE - (int) (OUTER_CIRCLE_ANGLE * animatedValue);
                break;
            default:
                break;
        }
    }

    private void invalidateSelf() {
        if (mCallback != null)
        {
            mCallback.invalidateDrawable(null);
        }
    }
}
