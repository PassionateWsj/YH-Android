package com.intfocus.yhdev.business.subject.template.one

import android.content.Context
import com.intfocus.yhdev.business.subject.templateone.entity.MererDetailEntity

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
        fun onDataLoaded(entity: MererDetailEntity)
        fun onDataNotAvailable(e: Throwable)
    }

    fun getData(ctx: Context, groupId: String, reportId: String,callback: LoadDataCallback)
}
