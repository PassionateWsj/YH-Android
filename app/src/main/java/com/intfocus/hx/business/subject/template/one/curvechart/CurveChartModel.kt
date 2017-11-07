package com.intfocus.hx.business.subject.templateone.curvechart

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
interface CurveChartModel {
    // 加载数据结果 回调接口
    interface LoadDataCallback {
        fun onDataLoaded(data: com.intfocus.hx.business.subject.template.one.entity.MDRPUnitCurveChartEntity)

        fun onDataNotAvailable(e: Throwable)
    }
    fun getData(@NotNull mParam:String, @NotNull callback: LoadDataCallback)

}
