package com.intfocus.template.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.intfocus.template.R;
import com.intfocus.template.util.DisplayUtil;

/**
 * @author liuruilin
 * @data 2017/12/5
 * @describe
 */
public class RecyclerItemDecoration extends RecyclerView.ItemDecoration {
    private Paint mPaint;
    private int height;
    private int margin;

    public RecyclerItemDecoration(Context context) {
        mPaint = new Paint();
        mPaint.setColor(context.getResources().getColor(R.color.co4));
        mPaint.setAntiAlias(true);

        height = DisplayUtil.dip2px(context, 1);
        margin = DisplayUtil.dip2px(context, 16);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();

        Rect rect = new Rect();
        rect.left = parent.getPaddingLeft() + margin;
        rect.right = parent.getWidth() - parent.getPaddingRight() - margin;

        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            rect.top = childView.getBottom();
            rect.bottom = rect.top + height;
            c.drawRect(rect, mPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom += height;
    }
}
