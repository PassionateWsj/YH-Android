package com.intfocus.syp_template.business.subject.template.one

import android.util.Log
import com.alibaba.fastjson.JSONReader
import com.intfocus.syp_template.business.subject.template.model.ReportModelImpl
import com.intfocus.syp_template.business.subject.templateone.entity.MererDetailEntity
import com.intfocus.syp_template.constant.Params.REPORT_TYPE_MAIN_DATA
import com.zbl.lib.baseframe.utils.TimeUtil
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.StringReader
import java.util.*

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/25 下午5:11
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class ModeImpl : ReportModelImpl() {

    private var uuid = ""

    companion object {
        private val TAG = "ModeImpl"
        private var INSTANCE: ModeImpl? = null
        private var observable: Subscription? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): ModeImpl {
            return INSTANCE ?: ModeImpl()
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

    fun checkReportData(reportId: String, templateId: String, groupId: String, callback: ModeModel.LoadDataCallback) {
        uuid = reportId + templateId + groupId
        val urlString = reportId + templateId + groupId
        when {
            checkUpdate(urlString) -> analysisData(groupId, reportId, callback)
            available(uuid) -> {
                val entity = MererDetailEntity()
                entity.name = "123"
                callback.onDataLoaded(entity)
//                analysisData(groupId, reportId, callback)
            }
            else -> analysisData(groupId, reportId, callback)
        }
    }

    fun analysisData(groupId: String, reportId: String, callback: ModeModel.LoadDataCallback) {
        val jsonFileName = String.format("group_%s_template_%s_report_%s.json", groupId, "1", reportId)

        observable = Observable.just(jsonFileName)
                .subscribeOn(Schedulers.io())
                .map {
                    uuid = reportId + "1" + groupId
                    delete(uuid)
                    val response: String?
//                    val jsonFilePath = FileUtil.dirPath(globalContext, K.K_CACHED_DIR_NAME, it)
//                    val dataState = ApiHelper.reportJsonData(globalContext, groupId, "1", reportId)
//                    if (dataState || File(jsonFilePath).exists()) {
//                        response = FileUtil.readFile(jsonFilePath)
//                    } else {
//                        throw Throwable("获取数据失败")
//                    }
                    response = getAssetsJsonData("test.json")
//                    response = getAssetsJsonData("kpi_detaldata.json")
                    Log.i(TAG, "analysisDataStartTime:" + TimeUtil.getNowTime())
                    val stringReader = StringReader(response)
                    Log.i(TAG, "analysisDataReaderTime1:" + TimeUtil.getNowTime())
                    val reader = JSONReader(stringReader)
                    reader.startArray()
                    reader.startObject()
                    val entity = MererDetailEntity()
                    entity.data = ArrayList()
                    var page = 0
                    var index = 0
                    Log.i(TAG, "analysisDataReaderTime2:" + TimeUtil.getNowTime())

                    while (reader.hasNext()) {
                        val key = reader.readString()
                        when (key) {
                            "name" -> {
                                entity.name = reader.readObject().toString()
                                Log.i(TAG, "name:" + TimeUtil.getNowTime())
                            }

                            "data" -> {
                                Log.i(TAG, "dataStart:" + TimeUtil.getNowTime())
                                reader.startArray()

                                while (reader.hasNext()) {
                                    reader.startObject()
                                    var config = ""
                                    var title = ""
                                    val data = MererDetailEntity.PageData()
                                    while (reader.hasNext()) {
                                        val dataKey = reader.readString()
                                        when (dataKey) {
                                            "parts" -> {
                                                config = reader.readObject().toString()
                                                data.parts = config
                                                val moduleStringReader = StringReader(config)
                                                val moduleReader = JSONReader(moduleStringReader)
                                                moduleReader.startArray()
                                                while (moduleReader.hasNext()) {
                                                    var moduleConfig = ""
                                                    var moduleType = ""
                                                    moduleReader.startObject()
                                                    while (moduleReader.hasNext()) {
                                                        when (moduleReader.readString()) {
                                                            "config" -> moduleConfig = moduleReader.readObject().toString()

                                                            "type" -> moduleType = moduleReader.readObject().toString()
                                                        }
                                                    }
                                                    moduleReader.endObject()
                                                    insert(uuid, moduleConfig, moduleType, index, page)
                                                    index++
                                                }
                                                moduleReader.endArray()
                                            }
                                            "title" -> title = reader.readObject().toString()
                                        }
                                    }
                                    insertMainData(uuid, config, REPORT_TYPE_MAIN_DATA, title, page)
                                    page++
                                    reader.endObject()
                                    entity.data!!.add(data)
                                }
                                reader.endArray()
                                Log.i(TAG, "dataEnd:" + TimeUtil.getNowTime())
                            }
                        }
                    }
                    reader.endObject()
                    reader.endArray()
                    Log.i(TAG, "analysisDataEndTime:" + TimeUtil.getNowTime())
                    entity
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<MererDetailEntity>() {
                    override fun onCompleted() {
                    }

                    override fun onNext(t: MererDetailEntity?) {
//                        t ?:callback.onLoadData(t)
                        t?.let { callback.onDataLoaded(it) }

                    }

                    override fun onError(e: Throwable?) {
                        callback.onDataNotAvailable(e!!)
                    }

                })
    }
}
