package com.intfocus.template.dashboard.kpi

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.intfocus.template.ui.view.CustomLinearLayoutManager

/**
 * Created by CANC on 2017/7/28.
 */

class KpiScrollerListener(val context: Context,
                          val recyclerView: RecyclerView,
                          val titleTop: View) : RecyclerView.OnScrollListener() {
//    private val statusBarHeight: Int = Utils.getStatusBarHeight(context)
    private val mLinearLayoutManager: CustomLinearLayoutManager = recyclerView.layoutManager as CustomLinearLayoutManager


    /**
     * firstVisibleItem：当前能看见的第一个列表项ID（从0开始）
     * visibleItemCount：当前能看见的列表项个数（小半个也算） totalItemCount：列表项共数
     */
    override fun onScrolled(view: RecyclerView?, dx: Int, dy: Int) {
        val isSignificantDelta = Math.abs(dy) > 4
        if (isSignificantDelta) {
            if (dy > 0) {
            } else {
            }
        }

        val loc = IntArray(2)
        val firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()
        if (firstVisibleItem == 0 && recyclerView.getChildAt(firstVisibleItem) != null) {
            recyclerView.getChildAt(firstVisibleItem).getLocationOnScreen(loc)
            if (loc[1] < 0) {
                val alpha = Math.abs(loc[1] - 0) * 1.0f / titleTop
                        .measuredHeight
                titleTop.alpha = alpha
            } else {
                titleTop.alpha = 0f
            }

        }
    }
}
