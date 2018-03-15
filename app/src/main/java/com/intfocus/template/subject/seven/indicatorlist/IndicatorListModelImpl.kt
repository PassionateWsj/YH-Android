package com.intfocus.template.subject.seven.indicatorlist

import com.alibaba.fastjson.JSON
import com.intfocus.template.SYPApplication
import com.intfocus.template.general.net.ApiException
import com.intfocus.template.general.net.CodeHandledSubscriber
import com.intfocus.template.general.net.RetrofitUtil
import com.intfocus.template.general.net.SaaSRetrofitUtil
import com.intfocus.template.subject.seven.bean.ConcernGroupBean
import com.intfocus.template.subject.seven.bean.ConcernItemsBean
import com.intfocus.template.util.OKHttpUtils
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

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

    override fun getConcernedListByUser(controlId: String, repCode: String, callback: IndicatorListModel.OnConcernedListResult) {
        SaaSRetrofitUtil.getHttpServiceKotlin(SYPApplication.globalContext)
                .getConcernItemsListData(repCode,  controlId)
                .compose(RetrofitUtil.CommonOptions<ConcernItemsBean>())
                .subscribe(object : CodeHandledSubscriber<ConcernItemsBean>() {
                    override fun onError(apiException: ApiException?) {

                    }

                    override fun onBusinessNext(data: ConcernItemsBean?) {
                        data?.data?.let {
                            observable = Observable.just(it)
                                    .subscribeOn(Schedulers.io())
                                    .map {
                                        it.forEach {
                                            val itemUrl = "http://shengyiplus.idata.mobi/saas-api/api/portal/custom?repCode=${it.rep_code}&dataSourceCode=DATA_000007&obj_id=${it.obj_id}"
                                            val itemListData: ConcernGroupBean?
                                            try {
                                                itemListData = JSON.parseObject(
                                                        OKHttpUtils.newInstance().getSyncData(itemUrl),
                                                        ConcernGroupBean::class.java)
                                                it.concern_item_group_list = itemListData.data
                                            } catch (e: Exception) {
                                                onError(Throwable(e))
                                            }
                                        }
                                        it
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

                    override fun onCompleted() {
                    }

                })
    }
}