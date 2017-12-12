package com.intfocus.shengyiplus.dashboard.workbox

import android.content.Context
import com.intfocus.shengyiplus.constant.Params.GROUP_ID
import com.intfocus.shengyiplus.constant.Params.ROLD_ID
import com.intfocus.shengyiplus.model.response.home.WorkBoxResult
import com.intfocus.shengyiplus.general.net.ApiException
import com.intfocus.shengyiplus.general.net.CodeHandledSubscriber
import com.intfocus.shengyiplus.general.net.RetrofitUtil
import com.zbl.lib.baseframe.core.AbstractMode
import org.greenrobot.eventbus.EventBus

/**
 * Created by liuruilin on 2017/7/28.
 */
class WorkBoxMode(var ctx: Context) : AbstractMode() {
    var mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)

    override fun requestData() {
        RetrofitUtil.getHttpService(ctx).getWorkBox(mUserSP.getString(GROUP_ID, "0"), mUserSP.getString(ROLD_ID, "0"))
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
