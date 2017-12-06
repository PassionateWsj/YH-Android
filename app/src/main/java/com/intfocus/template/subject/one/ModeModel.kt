package com.intfocus.template.subject.one

import android.content.Context
import com.intfocus.template.subject.one.entity.Filter

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
        fun onDataLoaded(reports: List<String>, filter: Filter)
        fun onDataNotAvailable(e: Throwable)
    }

    fun getData(ctx: Context, groupId: String, reportId: String, callback: LoadDataCallback)
}
