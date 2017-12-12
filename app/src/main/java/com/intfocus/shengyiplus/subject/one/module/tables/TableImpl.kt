package com.intfocus.shengyiplus.subject.one.module.tables

import com.alibaba.fastjson.JSON
import com.intfocus.shengyiplus.subject.one.ModeImpl
import com.intfocus.shengyiplus.subject.one.entity.MDetailRootPageRequestResult
import com.intfocus.shengyiplus.subject.one.entity.MDetailUnitEntity
import com.intfocus.shengyiplus.subject.one.entity.Tables
import com.intfocus.shengyiplus.util.LogUtil
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/20 下午3:35
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class TableImpl : TableModel {

    companion object {

        private var INSTANCE: TableImpl? = null
        private val TAG = "TableImpl"
        private var observable: Subscription? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): TableImpl {
            return INSTANCE ?: TableImpl()
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

    override fun getData(dataJson: String, callbackTableContent: TableModel.TableContentLoadDataCallback) {
        LogUtil.d(TAG, "TableContent 表格数据开始转为对象")
        var startTime = System.currentTimeMillis()
        observable = Observable.just(dataJson)
                .subscribeOn(Schedulers.io())
                .map { JSON.parseObject<Tables>(it, Tables::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Tables> {
                    override fun onNext(t: Tables?) {
                        LogUtil.d(TAG, "TableContent 表格数据转为对象结束")
                        LogUtil.d(TAG, "TableContent 转换耗时 ::: " + (System.currentTimeMillis() - startTime) + " 毫秒")
                        startTime = System.currentTimeMillis()
                        t?.let { callbackTableContent.onDataLoaded(t) }
                    }

                    override fun onError(e: Throwable?) {
                        callbackTableContent.onDataNotAvailable(e)
                    }

                    override fun onCompleted() {
                    }
                })
    }

    override fun getRootData(rootId: Int, index: Int, callbackTableRoot: TableModel.TableRootLoadDataCallback) {
        observable = Observable.just(1)
                .subscribeOn(Schedulers.io())
                .map {
                    val startTime = System.currentTimeMillis()
                    val result = ModeImpl.getInstance().queryModuleConfig(index, rootId)
                    val data = MDetailRootPageRequestResult(JSON.parseArray(result, MDetailUnitEntity::class.java))
                    LogUtil.d(TAG, "TableRoot 转换耗时 ::: " + (System.currentTimeMillis() - startTime) + " 毫秒")
                    data
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<MDetailRootPageRequestResult> {
                    override fun onNext(t: MDetailRootPageRequestResult?) {
                        t?.let { callbackTableRoot.onDataLoaded(t) }
                    }

                    override fun onError(e: Throwable?) {
                        LogUtil.d(TAG, "TableRoot onError ::: " + e!!.message)
                        callbackTableRoot.onDataNotAvailable(e)
                    }

                    override fun onCompleted() {
                    }
                })

    }
}
