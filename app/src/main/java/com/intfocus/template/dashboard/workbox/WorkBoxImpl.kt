package com.intfocus.template.subject.one

import android.content.Context
import com.intfocus.template.constant.Params
import com.intfocus.template.constant.Params.USER_BEAN
import com.intfocus.template.general.net.ApiException
import com.intfocus.template.general.net.CodeHandledSubscriber
import com.intfocus.template.general.net.RetrofitUtil
import com.intfocus.template.model.response.home.WorkBoxResult
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
class WorkBoxImpl : WorkBoxModel {

    companion object {
        private val TAG = "WorkBoxImpl"
        private var INSTANCE: WorkBoxImpl? = null
        private var observable: Subscription? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): WorkBoxImpl {
            return INSTANCE ?: WorkBoxImpl()
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

    override fun getData(ctx: Context, callBack: WorkBoxModel.LoadDataCallback) {
        val mUserSP = ctx.getSharedPreferences(USER_BEAN, Context.MODE_PRIVATE)
        RetrofitUtil.getHttpService(ctx).getWorkBox(mUserSP.getString(Params.GROUP_ID, "0"), mUserSP.getString(Params.ROLD_ID, "0"))
                .compose(RetrofitUtil.CommonOptions<WorkBoxResult>())
                .subscribe(object : CodeHandledSubscriber<WorkBoxResult>() {
                    override fun onError(apiException: ApiException?) {
                         callBack.onDataNotAvailable(apiException!!)
                    }

                    override fun onBusinessNext(data: WorkBoxResult?) {
                        callBack.onDataLoaded(data!!)
                    }

                    override fun onCompleted() {
                    }
                })
    }
}
