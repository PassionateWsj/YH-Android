package com.intfocus.yhdev.dashboard.work_box

import android.content.Context
import com.intfocus.yhdev.data.response.home.WorkBoxResult
import com.intfocus.yhdev.net.ApiException
import com.intfocus.yhdev.net.CodeHandledSubscriber
import com.intfocus.yhdev.net.RetrofitUtil
import com.intfocus.yhdev.util.URLs
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
