package com.intfocus.template.subject.templateone.curvechart

import com.intfocus.template.subject.one.entity.Chart
import org.jetbrains.annotations.NotNull

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/24 下午3:57
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface ChartModel {
    // 加载数据结果 回调接口
    interface LoadDataCallback {
        fun onDataLoaded(data: Chart)

        fun onDataNotAvailable(e: Throwable)
    }
    fun getData(@NotNull rootId: Int, @NotNull index: Int, @NotNull callback: LoadDataCallback)

}
