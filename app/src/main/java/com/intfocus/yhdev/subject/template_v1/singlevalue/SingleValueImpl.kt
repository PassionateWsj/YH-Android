package com.intfocus.yhdev.subject.template_v1.singlevalue

import com.alibaba.fastjson.JSON
import com.intfocus.yhdev.subject.template_v1.entity.MDRPUnitSingleValue
import rx.Observable
import rx.Observer
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
class SingleValueImpl : SingleValueModel {

    companion object {

        private var INSTANCE: SingleValueImpl? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): SingleValueImpl {
            return INSTANCE ?: SingleValueImpl()
                    .apply { INSTANCE = this }
        }

        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }

    override fun getData(mParam: String, callback: SingleValueModel.LoadDataCallback) {
        Observable.just(mParam)
                .subscribeOn(Schedulers.io())
                .map { JSON.parseObject<MDRPUnitSingleValue>(it, MDRPUnitSingleValue::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<MDRPUnitSingleValue> {
                    override fun onNext(t: MDRPUnitSingleValue?) {
                        t?.let { callback.onDataLoaded(t) }
                    }

                    override fun onError(e: Throwable?) {
                        callback.onDataNotAvailable(e)
                    }

                    override fun onCompleted() {
                    }
                })
//        val valueData = JSON.parseObject(mParam, MDRPUnitSingleValue::class.java)
//        callback.onDataLoaded(valueData)
    }

}