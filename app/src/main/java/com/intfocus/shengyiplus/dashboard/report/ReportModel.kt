package com.intfocus.shengyiplus.subject.one

import android.content.Context
import com.intfocus.shengyiplus.model.response.home.ReportListResult

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/25 下午5:09
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface ReportModel {
    interface LoadDataCallback {
        fun onDataLoaded(data: ReportListResult)
        fun onDataNotAvailable(e: Throwable)
    }

    fun getData(ctx: Context, callBack: LoadDataCallback)
}
