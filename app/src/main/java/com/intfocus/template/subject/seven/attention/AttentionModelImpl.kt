package com.intfocus.template.subject.seven.attention

import com.intfocus.template.model.DaoUtil
import com.intfocus.template.model.gen.AttentionItemDao
import com.intfocus.template.util.LogUtil
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
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
class AttentionModelImpl : AttentionModel {

    private val mDao = DaoUtil.getAttentionItemDao()

    companion object {
        private val TAG = "AttentionModelImpl"
        private var INSTANCE: AttentionModelImpl? = null
        private var observable: Subscription? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): AttentionModelImpl {
            return INSTANCE ?: AttentionModelImpl()
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

    override fun getData(keyWord: String, concerned: Boolean, callback: AttentionModel.LoadDataCallback) {
        val queryBuilder = mDao.queryBuilder()
        observable = Observable.just(keyWord)
                .subscribeOn(Schedulers.io())
                .map {
                    queryBuilder.where(queryBuilder.and(AttentionItemDao.Properties.IsAttentioned.eq(concerned),queryBuilder.or(AttentionItemDao.Properties.Attention_item_id.like("%$keyWord%"),
                            AttentionItemDao.Properties.Attention_item_name.like("%$keyWord%")))).list()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.isNotEmpty()) {
                        LogUtil.d(this@AttentionModelImpl, "数据库处理成功,获取数据条数 ::: " + it.size)
                        callback.onDataLoaded(it)
                    } else {
                        callback.onDataNotAvailable(Throwable("未查询到数据"))
                        LogUtil.d(this@AttentionModelImpl, "数据库处理错误,获取数据条数为 空")
                    }
                }
    }

    override fun concernOrCancelConcern(attentionItemId: String, attentionItemName: String, callback: AttentionModel.ConcernCallback) {
        val queryBuilder = mDao.queryBuilder()
        observable = Observable.just(listOf(attentionItemId, attentionItemName))
                .subscribeOn(Schedulers.io())
                .map {
                    queryBuilder.where(queryBuilder.and(AttentionItemDao.Properties.Attention_item_id.eq(it[0]),
                            AttentionItemDao.Properties.Attention_item_name.eq(it[1]))).unique()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it.isAttentioned = !it.isAttentioned
                    mDao.update(it)
                    callback.onConcernResult(it.isAttentioned)
                }
    }
}