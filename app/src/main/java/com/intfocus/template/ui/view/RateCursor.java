package com.intfocus.template.ui.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;

import com.intfocus.template.R;

import static android.animation.ObjectAnimator.ofFloat;

/**
 * 仪表盘箭头
 * Created by zbaoliang on 17-4-29.
 */
public class RateCursor extends android.support.v7.widget.AppCompatImageView {

    private int[] sources = new int[]{
            R.drawable.icon_redarrow_up,
            R.drawable.icon_yellowarrow_up,
            R.drawable.icon_greenarrow_up,
            R.drawable.icon_redarrow_down,
            R.drawable.icon_yellowarrow_down,
            R.drawable.icon_greenarrow_down
    };
    boolean currAnim;
    private ObjectAnimator downAnimator;
    private ObjectAnimator upAnimator;
    private boolean animOn = true;

    public RateCursor(Context context) {
        super(context);
        init();
    }

    public RateCursor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RateCursor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
//        setImageResource(sources[0]);
        downAnimator = ObjectAnimator.ofFloat(this, "rotation", 0, 180)
                .setDuration(500);
        upAnimator = ofFloat(this, "rotation", 180, 0)
                .setDuration(500);
    }

    public void setCursorState(int state, boolean isAnim) {
        if (state < 0) {
            setImageResource(0);
        } else {
            setImageResource(sources[state % sources.length]);
        }

        if (currAnim != isAnim) {
            if (isAnim) {
                downAnimator.start();
            } else {
                upAnimator.start();
            }
        }
        currAnim = isAnim;
    }
}
