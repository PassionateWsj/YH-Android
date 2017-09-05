package com.intfocus.yh_android.dashboard.work_box

import android.content.Context
import com.intfocus.yh_android.data.response.home.WorkBoxResult
import com.intfocus.yh_android.net.ApiException
import com.intfocus.yh_android.net.CodeHandledSubscriber
import com.intfocus.yh_android.net.RetrofitUtil
import com.intfocus.yh_android.util.URLs
import com.zbl.lib.baseframe.core.AbstractMode
import org.greenrobot.eventbus.EventBus

/**
 * Created by liuruilin on 2017/7/28.
 */
class WorkBoxMode(var ctx: Context) : AbstractMode() {
    var mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)

    override fun requestData() {
        RetrofitUtil.getHttpService(ctx).getWorkBox(mUserSP.getString(URLs.kGroupId, "0"), mUserSP.getString(URLs.kRoleId, "0"))
                .compose(RetrofitUtil.CommonOptions<WorkBoxResult>())
                .subscribe(object : CodeHandledSubscriber<WorkBoxResult>() {
                    override fun onError(apiException: ApiException?) {
                        val result1 = WorkBoxRequest(false, -1)
                        EventBus.getDefault().post(result1)
                    }

                    override fun onBusinessNext(data: WorkBoxResult?) {
                        val result1 = WorkBoxRequest(true, 200)
                        result1.workBoxDatas = data!!.data
                        EventBus.getDefault().post(result1)
                    }

                    override fun onCompleted() {
                    }
                })
    }
}
