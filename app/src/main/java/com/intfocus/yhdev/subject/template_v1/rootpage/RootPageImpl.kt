package com.intfocus.yhdev.subject.template_v1.rootpage

import android.util.Log
import com.alibaba.fastjson.JSONReader
import com.intfocus.yhdev.subject.template_v1.entity.MDetailUnitEntity
import com.intfocus.yhdev.subject.template_v1.entity.msg.MDetailRootPageRequestResult
import com.zbl.lib.baseframe.utils.TimeUtil
import rx.Observable
import rx.Observer
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
class RootPageImpl : RootPageModel {
    private val TAG = "hjjzz"

    companion object {

        private var INSTANCE: RootPageImpl? = null
        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): RootPageImpl {
            return INSTANCE ?: RootPageImpl()
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

    override fun getData(mParam: String, callback: RootPageModel.LoadDataCallback) {
        Log.i(TAG, "StartAnalysisTime:" + TimeUtil.getNowTime())
        Observable.just(mParam)
                .subscribeOn(Schedulers.io())
                .map {
                    val isr = StringReader(it)
                    val reader = JSONReader(isr)
                    val datas = ArrayList<MDetailUnitEntity>()
                    reader.startArray()
                    while (reader.hasNext()) {
                        val entity = MDetailUnitEntity()
                        reader.startObject()
                        while (reader.hasNext()) {
                            val key = reader.readString()
                            when (key) {
                                "config" -> entity.config = reader.readObject().toString()

                                "type" -> entity.type = reader.readObject().toString()
                            }
                        }
                        datas.add(entity)
                        reader.endObject()
                    }
                    reader.endArray()
                    datas
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ArrayList<MDetailUnitEntity>> {
                    override fun onError(e: Throwable?) {
                        callback.onDataNotAvailable(e!!)
                    }

                    override fun onNext(t: ArrayList<MDetailUnitEntity>?) {
                        callback.onDataLoaded(MDetailRootPageRequestResult(true, 200, t))
                    }

                    override fun onCompleted() {
                        Log.i(TAG, "EndAnalysisTime:" + TimeUtil.getNowTime())
                    }

                })
    }

}