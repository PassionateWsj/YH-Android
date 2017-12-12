package com.intfocus.shengyiplus.dashboard.feedback

import android.content.Context
import android.util.Log
import com.intfocus.shengyiplus.SYPApplication.globalContext
import com.intfocus.shengyiplus.constant.Params.USER_NUM
import com.intfocus.shengyiplus.general.net.ApiException
import com.intfocus.shengyiplus.general.net.CodeHandledSubscriber
import com.intfocus.shengyiplus.general.net.RetrofitUtil
import com.intfocus.shengyiplus.model.callback.LoadDataCallback
import com.intfocus.shengyiplus.model.response.mine_page.FeedbackContent
import com.intfocus.shengyiplus.model.response.mine_page.FeedbackList
import rx.Subscription

/**
 * @author liuruilin
 * @data 2017/11/28
 * @describe 问题反馈 Model 实现类
 */
class FeedbackModelImpl: FeedbackModel{
    private var mUserSP = globalContext.getSharedPreferences("UserBean", Context.MODE_PRIVATE)

    companion object {
        private val TAG = "FeedbackModelImpl"
        private var INSTANCE: FeedbackModelImpl? = null
        private var observable: Subscription? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): FeedbackModelImpl {
            return INSTANCE ?: FeedbackModelImpl()
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

    override fun getList(callback: LoadDataCallback<FeedbackList>) {
        RetrofitUtil.getHttpService(globalContext).getFeedbackList(mUserSP.getString(USER_NUM, ""))
                .compose(RetrofitUtil.CommonOptions<FeedbackList>())
                .subscribe(object : CodeHandledSubscriber<FeedbackList>() {
                    override fun onError(apiException: ApiException?) {
                    }

                    override fun onCompleted() {
                    }

                    override fun onBusinessNext(data: FeedbackList?) {
                        callback.onSuccess(data!!)
                    }
                })
    }

    override fun getContent(id: Int, callback: LoadDataCallback<FeedbackContent>) {
        RetrofitUtil.getHttpService(globalContext).getFeedbackContent(id)
                .compose(RetrofitUtil.CommonOptions<FeedbackContent>())
                .subscribe(object : CodeHandledSubscriber<FeedbackContent>() {
                    override fun onError(apiException: ApiException?) {
                    }

                    override fun onCompleted() {
                    }

                    override fun onBusinessNext(data: FeedbackContent?) {
                        callback.onSuccess(data!!)
                    }
                })
    }

    override fun submit() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
