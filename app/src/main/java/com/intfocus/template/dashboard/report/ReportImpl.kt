package com.intfocus.template.subject.one

import android.content.Context
import com.intfocus.template.constant.Params
import com.intfocus.template.general.net.ApiException
import com.intfocus.template.general.net.CodeHandledSubscriber
import com.intfocus.template.general.net.RetrofitUtil
import com.intfocus.template.model.response.home.ReportListResult
import rx.Subscription

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/25 下午5:11
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc: 模板一 Model 层
 * ****************************************************
 */
class ReportImpl : ReportModel {

    companion object {
        private val TAG = "ReportImpl"
        private var INSTANCE: ReportImpl? = null
        private var observable: Subscription? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): ReportImpl {
            return INSTANCE ?: ReportImpl()
                    .apply { INSTANCE = this }
        }

        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic
        fun destroyInstance() {
            unSubscribe()
            INSTANCE = null
        }

        /**
         * 取消订阅
         */
        private fun unSubscribe() {
            observable?.unsubscribe() ?: return
        }
    }

    override fun getData(ctx: Context, callBack: ReportModel.LoadDataCallback) {
        val mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)

        RetrofitUtil.getHttpService(ctx).getReportList(mUserSP.getString(Params.GROUP_ID, "0"), mUserSP.getString(Params.ROLD_ID, "0"))
                .compose(RetrofitUtil.CommonOptions<ReportListResult>())
                .subscribe(object : CodeHandledSubscriber<ReportListResult>() {

                    override fun onBusinessNext(data: ReportListResult?) {
                        data?.let { callBack.onDataLoaded(it) }

                    }

                    override fun onError(apiException: ApiException?) {
                        apiException?.let { callBack.onDataNotAvailable(it) }
                    }

                    override fun onCompleted() {
                    }
                })
    }
}
