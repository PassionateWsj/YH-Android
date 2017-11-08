package com.intfocus.hxtest.general.mode

import android.content.Context
import com.intfocus.hxtest.business.dashboard.report.mode.ReportListPageRequest
import com.intfocus.hxtest.general.data.response.home.ReportListResult
import com.intfocus.hxtest.general.net.ApiException
import com.intfocus.hxtest.general.net.CodeHandledSubscriber
import com.intfocus.hxtest.general.net.RetrofitUtil
import com.intfocus.hxtest.general.util.URLs.kGroupId
import com.intfocus.hxtest.general.util.URLs.kRoleId
import com.zbl.lib.baseframe.core.AbstractMode
import org.greenrobot.eventbus.EventBus

/**
 * 主页 - 报表 Model
 * Created by liuruilin on 2017/6/15.
 */
class ReportsListMode(var ctx: Context) : AbstractMode() {
    var mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)

    override fun requestData() {
        RetrofitUtil.getHttpService(ctx).getReportList(mUserSP.getString(kGroupId, "0"), mUserSP.getString(kRoleId, "0"))
                .compose(RetrofitUtil.CommonOptions<ReportListResult>())
                .subscribe(object : CodeHandledSubscriber<ReportListResult>() {

                    override fun onBusinessNext(data: ReportListResult?) {
                        val result1 = ReportListPageRequest(true, 200)
                        result1.categroy_list = data!!.data
                        EventBus.getDefault().post(result1)
                    }

                    override fun onError(apiException: ApiException?) {
                        val result1 = ReportListPageRequest(false, -1)
                        EventBus.getDefault().post(result1)
                    }

                    override fun onCompleted() {
                    }
                })
    }
}
