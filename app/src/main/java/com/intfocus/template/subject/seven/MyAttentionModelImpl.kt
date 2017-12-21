package com.intfocus.template.subject.seven

import com.alibaba.fastjson.JSON
import com.intfocus.template.model.DaoUtil
import com.intfocus.template.model.gen.AttentionItemDao
import com.intfocus.template.model.response.attention.Test2
import com.intfocus.template.util.LoadAssetsJsonUtil
import com.intfocus.template.util.LogUtil
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.schedulers.Schedulers

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 上午11:28
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class MyAttentionModelImpl : MyAttentionModel {

    private val mDao = DaoUtil.getAttentionItemDao()

    companion object {
        private val TAG = "MyAttentionModelImpl"
        private var INSTANCE: MyAttentionModelImpl? = null
        private var observable: Subscription? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): MyAttentionModelImpl {
            return INSTANCE ?: MyAttentionModelImpl()
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

    override fun getData(user_num: String, callback: MyAttentionModel.LoadDataCallback) {
        val assetsJsonData = LoadAssetsJsonUtil.getAssetsJsonData("template7_main_attention_data.json")
        val data = JSON.parseObject(assetsJsonData, Test2::class.java)
        observable = Observable.just(data.data)
                .subscribeOn(Schedulers.io())
                .flatMap { it ->
                    mDao.deleteAll()
                    mDao.insertInTx(it?.attention_list)
                    Observable.from(data.data.attentioned_data)
                }
                .subscribe(object : Observer<Test2.DataBeanXX.AttentionedDataBean> {
                    override fun onError(e: Throwable?) {
                        LogUtil.d(this@MyAttentionModelImpl, "数据库处理错误 ::: " + e?.message)
                    }

                    override fun onNext(it: Test2.DataBeanXX.AttentionedDataBean?) {
                        it?.let {
                            val attentionItem = mDao.queryBuilder().where(AttentionItemDao.Properties.Attention_item_id.eq(it.attention_item_id)).unique()
                            attentionItem.isAttentioned = true
                            mDao.update(attentionItem)
                        }
                    }

                    override fun onCompleted() {
                        LogUtil.d(this@MyAttentionModelImpl, "数据库处理完成")
                    }
                })
        callback.onDataLoaded(data, data.data.filter)
    }
}