package com.intfocus.syp_template.subject.one

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.JSONReader
import com.intfocus.syp_template.subject.model.ReportModelImpl
import com.intfocus.syp_template.model.entity.Report
import com.intfocus.syp_template.model.entity.ReportModule
import com.intfocus.syp_template.ConfigConstants
import com.intfocus.syp_template.model.gen.ReportDao
import com.intfocus.syp_template.model.DaoUtil
import com.intfocus.syp_template.util.K
import com.intfocus.syp_template.util.LogUtil
import com.zbl.lib.baseframe.utils.TimeUtil
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.StringReader

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

    private val TEMPLATE_ID = "1"
    lateinit private var filterObject: JSONObject
    lateinit private var pageTitleList: List<String>

    companion object {
        private val TAG = "ModeImpl"
        private var INSTANCE: ModeImpl? = null
        private var observable: Subscription? = null
        private var uuid: String = ""

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
        val urlString = String.format(K.K_REPORT_JSON_ZIP_API_PATH, ConfigConstants.kBaseUrl, groupId, templateId, reportId)
        when {
            check(urlString) -> analysisData(groupId, reportId, callback)
            available(uuid) -> {
                analysisData(groupId, reportId, callback)
//                observable = Observable.just(uuid)
//                        .subscribeOn(Schedulers.io())
//                        .map { queryDateBase(it) }
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(object : Subscriber<List<String>>() {
//                            override fun onCompleted() {
//                            }
//
//                            override fun onNext(t: List<String>?) {
//                                t?.let { callback.onDataLoaded(it) }
//                            }
//
//                            override fun onError(e: Throwable?) {
//                                delete(uuid)
//                                clearResponseHeader(urlString)
//                                callback.onDataNotAvailable(e!!)
//                            }
//                        })
            }
            else -> analysisData(groupId, reportId, callback)
        }
    }

    fun analysisData(groupId: String, reportId: String, callback: ModeModel.LoadDataCallback) {
        val jsonFileName = String.format("group_%s_template_%s_report_%s.json", groupId, TEMPLATE_ID, reportId)
        LogUtil.d(TAG, "ModeImpl 表格数据开始转为对象")
        var startTime = System.currentTimeMillis()
        observable = Observable.just(jsonFileName)
                .subscribeOn(Schedulers.io())
                .map {
                    val uuid = reportId + TEMPLATE_ID + groupId
                    delete(uuid)
                    val response: String?
//                    val jsonFilePath = FileUtil.dirPath(globalContext, K.K_CACHED_DIR_NAME, it)
//                    val dataState = ApiHelper.reportJsonData(globalContext, groupId, "1", reportId)
//                    if (dataState || File(jsonFilePath).exists()) {
//                        response = FileUtil.readFile(jsonFilePath)
//                    } else {
//                        throw Throwable("获取数据失败")
//                    }
                    response = getAssetsJsonData("template1_05.json")

                    val stringReader = StringReader(response)
                    val reader = JSONReader(stringReader)

                    reader.startObject()
                    while (reader.hasNext()) {
                        val configKey = reader.readString()
                        when (configKey) {
                            "filter" -> {
                                var report = Report()
                                report.id = null
                                report.uuid = uuid
                                report.type = "filter"
                                report.config = reader.readObject().toString()
                                insertDateBase(report)
                            }
                            "parts" -> {
                                reader.startArray()
                                var i = 0
                                while (reader.hasNext()) {

                                    var partsItem = JSON.parseObject(reader.readObject().toString(), ReportModule::class.java)
                                    var report = Report()
                                    report.id = null
                                    report.uuid = uuid
                                    report.name = partsItem.name ?: "name"
                                    report.page_title = partsItem.page_title ?: "page_title"
                                    report.index = i
                                    report.type = partsItem.type ?: "unknown_type"
                                    report.config = partsItem.config ?: "null_config"
                                    insertDateBase(report)
                                    i++
                                }
                                reader.endArray()
                            }
                        }
                    }
                    reader.endObject()
                    LogUtil.d(TAG, "analysisDataEndTime:" + TimeUtil.getNowTime())
                    pageTitleList = generatePageList(queryDateBase(uuid))
                    pageTitleList
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<List<String>>() {
                    override fun onCompleted() {
                        LogUtil.d(TAG, "ModeImpl 表格数据转为对象结束")
                        LogUtil.d(TAG, "ModeImpl 转换耗时 ::: " + (System.currentTimeMillis() - startTime) + " 毫秒")
                        startTime = System.currentTimeMillis()
                    }

                    override fun onNext(t: List<String>?) {
                        t?.let { callback.onDataLoaded(it) }
                    }

                    override fun onError(e: Throwable?) {
                        callback.onDataNotAvailable(e!!)
                    }
                })
    }

    private fun insertDateBase(report: Report) {
        val reportDao = DaoUtil.getReportDao()
        reportDao.insert(report)
    }

    private fun queryDateBase(uuid: String): List<Report> {
        val reportDao = DaoUtil.getReportDao()
        val filter = reportDao.queryBuilder()
                .where(reportDao.queryBuilder()
                        .and(ReportDao.Properties.Uuid.eq(uuid), ReportDao.Properties.Type.eq("filter"))).unique()
        filterObject = JSON.parseObject(filter.config)

        return if (filterObject.isEmpty()) {
            reportDao.queryBuilder()
                    .where(ReportDao.Properties.Uuid.eq(uuid))
                    .list() ?: mutableListOf()
        } else {
            reportDao.queryBuilder()
                    .where(reportDao.queryBuilder()
                            .and(ReportDao.Properties.Uuid.eq(uuid), ReportDao.Properties.Name.eq(filterObject["display"])))
                    .list() ?: mutableListOf()
        }
    }

    fun queryDateBase(pageId: Int): List<Report> {
        val reportDao = DaoUtil.getReportDao()
        return if (filterObject.isEmpty()) {
            reportDao.queryBuilder()
                    .where(reportDao.queryBuilder()
                            .and(ReportDao.Properties.Uuid.eq(uuid), ReportDao.Properties.Page_title.eq(pageTitleList[pageId])))
                    .list()
        }
        else {
            reportDao.queryBuilder()
                    .where(reportDao.queryBuilder()
                            .and(ReportDao.Properties.Uuid.eq(uuid), ReportDao.Properties.Page_title.eq(pageTitleList[pageId]), ReportDao.Properties.Name.eq(filterObject["display"])))
                    .list()
        }
    }

    fun queryModuleConfig(index: Int, pageId: Int): String {
        val reportDao = DaoUtil.getReportDao()
        return if (filterObject.isEmpty()) {
            reportDao.queryBuilder()
                    .where(reportDao.queryBuilder()
                            .and(ReportDao.Properties.Uuid.eq(uuid), ReportDao.Properties.Page_title.eq(pageTitleList[pageId]), ReportDao.Properties.Index.eq(index)))
                    .unique().config
        }
        else {
            reportDao.queryBuilder()
                    .where(reportDao.queryBuilder()
                            .and(ReportDao.Properties.Uuid.eq(uuid), ReportDao.Properties.Page_title.eq(pageTitleList[pageId]), ReportDao.Properties.Name.eq(filterObject["display"]), ReportDao.Properties.Index.eq(index)))
                    .unique().config
        }
    }

    fun checkFilter() {

    }

    private fun generatePageList(reports: List<Report>): List<String> {
        var pageSet = HashSet<String>()
        for (report in reports) {
            if (null != report.page_title) {
                pageSet.add(report.page_title)
            }
        }
        return pageSet.toList()
    }
}
