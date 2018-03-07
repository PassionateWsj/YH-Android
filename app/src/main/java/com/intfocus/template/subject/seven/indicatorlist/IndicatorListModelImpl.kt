package com.intfocus.template.subject.seven.indicatorlist

import com.alibaba.fastjson.JSON
import com.intfocus.template.subject.seven.bean.ConcernGroupBean
import com.intfocus.template.subject.seven.bean.ConcernItemsBean
import com.intfocus.template.util.OKHttpUtils
import okhttp3.Call
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/26 下午1:49
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class IndicatorListModelImpl : IndicatorListModel {

    /**
     * 默认只有一个页签
     */
    private val pageId = 0

    companion object {
        private val TAG = "IndicatorListModelImpl"
        private var INSTANCE: IndicatorListModelImpl? = null
        private var observable: Subscription? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): IndicatorListModelImpl {
            return INSTANCE ?: IndicatorListModelImpl()
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

    override fun getConcernedListByUser(id: String, rep: String, callback: IndicatorListModel.OnConcernedListResult) {
        val url = "http://47.96.170.148:8081/saas-api/api/portal/custom?repCode=$rep&dataSourceCode=DATA_000007&controlId=$id"
        OKHttpUtils.newInstance().getAsyncData(url, object : OKHttpUtils.OnResultListener {
            override fun onFailure(call: Call?, e: IOException?) {
            }

            override fun onSuccess(call: Call?, response: String?) {
                observable = Observable.just(response)
                        .subscribeOn(Schedulers.io())
                        .map {
                            val data = JSON.parseObject(it,
                                    ConcernItemsBean::class.java).data!!
                            data.forEach {
                                val itemUrl = "http://47.96.170.148:8081/saas-api/api/portal/custom?repCode=${it.rep_code}&dataSourceCode=DATA_000007&objId=${it.obj_id}"
                                val itemListData = JSON.parseObject(
                                        OKHttpUtils.newInstance().getSyncData(itemUrl),
                                        ConcernGroupBean::class.java)
                                it.concern_item_group_list = itemListData.data
                            }
                            data
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Observer<List<ConcernItemsBean.ConcernItem>> {
                            override fun onCompleted() {

                            }

                            override fun onNext(t: List<ConcernItemsBean.ConcernItem>?) {
                                t?.let { callback.onLoadListDataSuccess(it) }
                            }

                            override fun onError(e: Throwable?) {
                            }
                        })
            }
        }
        )
    }
}