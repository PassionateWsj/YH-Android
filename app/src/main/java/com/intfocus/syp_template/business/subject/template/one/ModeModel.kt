package com.intfocus.syp_template.business.subject.template.one

import android.content.Context
import com.intfocus.syp_template.general.bean.Report

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/25 下午5:09
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface ModeModel {
    interface LoadDataCallback {
        fun onDataLoaded(reports: List<String>)
        fun onDataNotAvailable(e: Throwable)
    }

    fun getData(ctx: Context, groupId: String, reportId: String,callback: LoadDataCallback)
}
