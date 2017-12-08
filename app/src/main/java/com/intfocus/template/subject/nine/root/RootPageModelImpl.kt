package com.intfocus.template.subject.nine.root

import com.alibaba.fastjson.JSONReader
import com.intfocus.template.subject.nine.CollectionModelImpl.Companion.insertData
import com.intfocus.template.model.callback.LoadDataCallback
import com.intfocus.template.subject.nine.entity.Content
import com.intfocus.template.subject.nine.entity.RootPageRequestResult
import com.intfocus.template.constant.StateParams.STATE_CODE_SUCCESS
import com.intfocus.template.util.LogUtil
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.StringReader
import java.util.*

/**
 * ****************************************************
 * author jameswong
 * created on: 17/10/25 上午10:05
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class RootPageModelImpl : RootPageModel<RootPageRequestResult> {
    private val TAG = "hjjzz"

    companion object {
        private var INSTANCE: RootPageModelImpl? = null
        private var observable: Subscription? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): RootPageModelImpl {
            return INSTANCE ?: RootPageModelImpl()
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

    override fun getData(mParam: String, callback: LoadDataCallback<RootPageRequestResult>) {
        val datas = ArrayList<Content>()
        observable = Observable.just(mParam)
                .subscribeOn(Schedulers.io())
                .map {
                    val isr = StringReader(it)
                    val reader = JSONReader(isr)
                    reader.startArray()
                    while (reader.hasNext()) {
                        val entity = Content()
                        reader.startObject()
                        while (reader.hasNext()) {
                            val key = reader.readString()
                            when (key) {
                                "type" -> entity.type = reader.readObject().toString()

                                "key" -> entity.key = reader.readObject().toString()

                                "is_show" -> entity.is_show = reader.readInteger()

                                "is_list" -> entity.is_list = reader.readInteger()

                                "is_filter" -> entity.is_filter = reader.readInteger()

                                "is_must" -> entity.is_must = reader.readInteger()

                                "config" -> entity.config = reader.readObject().toString()

                                else -> LogUtil.d(TAG, key + "is error key in ContentEntity")
                            }
                        }
                        datas.add(entity)
                        reader.endObject()
                    }
                    reader.endArray()
                    datas
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ArrayList<Content>> {
                    override fun onError(e: Throwable?) {
                        callback.onError(e!!)
                    }

                    override fun onNext(t: ArrayList<Content>?) {
                        if (null != t && t.size != 0) {
                            callback.onSuccess(RootPageRequestResult(true, STATE_CODE_SUCCESS, t))
                        }
                        insertData(datas)
                    }

                    override fun onCompleted() {
                        LogUtil.d(TAG, "EndAnalysisTime")
                    }
                })
    }
}
