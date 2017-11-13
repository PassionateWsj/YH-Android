package com.intfocus.syp_template.business.subject.template.one

import android.content.Context
import android.util.Log
import com.alibaba.fastjson.JSONReader
import com.intfocus.syp_template.YHApplication.globalContext
import com.intfocus.syp_template.business.subject.template.model.ReportModelImpl
import com.intfocus.syp_template.business.subject.templateone.entity.MererDetailEntity
import com.intfocus.syp_template.general.bean.Report
import com.intfocus.syp_template.general.util.ApiHelper
import com.intfocus.syp_template.general.util.DaoUtil
import com.intfocus.syp_template.general.util.FileUtil
import com.intfocus.syp_template.general.util.K
import com.zbl.lib.baseframe.utils.TimeUtil
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.*
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
class ModeImpl: ReportModelImpl() {

    companion object {
        private val TAG = "ModeImpl"
        private var uuid = ""
        private var INSTANCE: ModeImpl? = null

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
            INSTANCE = null
        }
    }

    fun getUuid(): String {
        return uuid
    }

    fun checkReportData(reportId: String, templateId: String, groupId: String, callback: ModeModel.LoadDataCallback) {
        uuid = reportId + templateId + groupId
        var urlString = reportId + templateId + groupId
        if (update(urlString)) {
            analyseData(groupId, reportId, callback)
        }
        else if (available(uuid)) {
            var entity = MererDetailEntity()
            entity.name = "123"
            callback.onDataLoaded(entity)
        }
        else {
            analyseData(groupId, reportId, callback)
        }
    }

    fun analyseData(groupId: String, reportId: String, callback: ModeModel.LoadDataCallback) {
        val jsonFileName = String.format("group_%s_template_%s_report_%s.json", groupId, "1", reportId)

        Observable.just(jsonFileName)
                .subscribeOn(Schedulers.io())
                .map {
                    uuid = reportId + groupId
                    var reportDao = DaoUtil.getReportDao()
                    val response: String?
                    val jsonFilePath = FileUtil.dirPath(globalContext, K.K_CACHED_DIR_NAME, it)
                    val dataState = ApiHelper.reportJsonData(globalContext, groupId, "1", reportId)
                    if (dataState || File(jsonFilePath).exists()) {
                        response = FileUtil.readFile(jsonFilePath)
                    } else {
                        throw Throwable("获取数据失败")
                    }
//                    response = getJsonData(ctx,"kpi_detaldata.json")
                    Log.i(TAG, "analysisDataStartTime:" + TimeUtil.getNowTime())
                    val stringReader = StringReader(response)
                    Log.i(TAG, "analysisDataReaderTime1:" + TimeUtil.getNowTime())
                    val reader = JSONReader(stringReader)
                    reader.startArray()
                    reader.startObject()
                    val entity = MererDetailEntity()
                    entity.data = ArrayList()
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
                                    val data = MererDetailEntity.PageData()
                                    while (reader.hasNext()) {
                                        val dataKey = reader.readString()
                                        when (dataKey) {
//                                            "parts" -> data.parts = reader.readObject().toString()
                                            "parts" -> {
                                                var config = reader.readObject().toString()
                                                insertDb(uuid, config)
                                                var moduleStringReader = StringReader(config)
                                                var moduleReader = JSONReader(moduleStringReader)
                                                moduleReader.startArray()
                                                while (moduleReader.hasNext()) {
                                                    var index = 0
                                                    var report = Report()
                                                    report.id = null
                                                    report.uuid = uuid
                                                    report.index = index.toString()
                                                    moduleReader.startObject()
                                                    while (moduleReader.hasNext()) {
                                                        val key = moduleReader.readString()
                                                        when (key) {
                                                            "config" -> report.config = moduleReader.readObject().toString()

                                                            "type" -> report.type = moduleReader.readObject().toString()
                                                        }
                                                    }
                                                    moduleReader.endObject()
                                                    reportDao.insert(report)
                                                }
                                                moduleReader.endArray()
                                            }
                                            "title" -> data.title = reader.readObject().toString()
                                        }
                                    }
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
//                        t ?:callback.onSuccess(t)
                        t?.let { callback.onDataLoaded(it) }

                    }

                    override fun onError(e: Throwable?) {
                        callback.onDataNotAvailable(e!!)
                    }

                })
    }
}
