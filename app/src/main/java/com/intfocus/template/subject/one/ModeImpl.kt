package com.intfocus.template.subject.one

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONReader
import com.intfocus.template.BuildConfig
import com.intfocus.template.SYPApplication.globalContext
import com.intfocus.template.model.DaoUtil
import com.intfocus.template.model.entity.Report
import com.intfocus.template.model.entity.ReportModule
import com.intfocus.template.model.gen.ReportDao
import com.intfocus.template.subject.model.ReportModelImpl
import com.intfocus.template.subject.one.entity.Filter
import com.intfocus.template.util.ApiHelper
import com.intfocus.template.util.ApiHelper.clearResponseHeader
import com.intfocus.template.util.FileUtil
import com.intfocus.template.util.K
import com.intfocus.template.util.LogUtil
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.io.StringReader

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/25 下午5:11
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc: 模板一 Model 层
 * ****************************************************
 */
class ModeImpl : ReportModelImpl() {
    private val sqlDistinctPageTitle = "SELECT DISTINCT " + ReportDao.Properties.Page_title.columnName + " FROM " + ReportDao.TABLENAME
    private var pageTitleList: MutableList<String> = arrayListOf()
    private var reportDao: ReportDao = DaoUtil.getReportDao()
    lateinit private var filterObject: Filter
    private var urlString: String = ""
    private var jsonFileName: String = ""
    private var groupId: String = ""
    private var templateId: String = ""
    private var reportId: String = ""

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

    fun getData(reportId: String, templateId: String, groupId: String, callback: ModeModel.LoadDataCallback) {
        this.reportId = reportId
        this.templateId = templateId
        this.groupId = groupId
        uuid = reportId + templateId + groupId
        jsonFileName = String.format("group_%s_template_%s_report_%s.json", groupId, templateId, reportId)
        urlString = String.format(K.API_REPORT_JSON_ZIP, BuildConfig.BASE_URL, groupId, templateId, reportId)

        checkReportData(callback)
    }

    private fun checkReportData(callback: ModeModel.LoadDataCallback) {
        observable = Observable.just(uuid)
                .subscribeOn(Schedulers.io())
                .map {
                    when {
                        check(urlString) -> analysisData()
                        available(uuid) -> {
                            queryFilter(uuid)
                            generatePageList()
                        }
                        else -> analysisData()
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<List<String>>() {
                    override fun onCompleted() {

                    }

                    override fun onNext(t: List<String>?) {
                        t?.let { callback.onDataLoaded(it, filterObject) }
                    }

                    override fun onError(e: Throwable?) {
                        delete(uuid)
                        clearResponseHeader(urlString)
                        callback.onDataNotAvailable(e!!)
                    }
                })
    }

    private fun analysisData(): List<String> {
        LogUtil.d(TAG, "ModeImpl 报表数据开始转为对象")
        delete(uuid)
        val response: String?
        val jsonFilePath = FileUtil.dirPath(globalContext, K.K_CACHED_DIR_NAME, jsonFileName)
        val dataState = ApiHelper.reportJsonData(globalContext, groupId, templateId, reportId)
        if (dataState || File(jsonFilePath).exists()) {
            response = FileUtil.readFile(jsonFilePath)
        } else {
            throw Throwable("获取数据失败")
        }

//      response = LoadAssetsJsonUtil.getAssetsJsonData("template1_06.json")

        val stringReader = StringReader(response)
        val reader = JSONReader(stringReader)

        reader.startObject()
        while (reader.hasNext()) {
            val configKey = reader.readString()
            when (configKey) {
                "filter" -> {
                    filterObject = JSON.parseObject(reader.readObject().toString(), Filter::class.java)
                    val report = Report()
                    report.id = null
                    report.uuid = uuid
                    report.name = filterObject.display
                    report.type = "filter"
                    report.page_title = "filter"
                    report.config = JSON.toJSONString(filterObject)
                    reportDao.insert(report)
                }
                "parts" -> {
                    reader.startArray()
                    var i = 0
                    while (reader.hasNext()) {
                        val partsItem = JSON.parseObject(reader.readObject().toString(), ReportModule::class.java)
                        val report = Report()
                        report.id = null
                        report.uuid = uuid
                        report.name = partsItem.name ?: "name"
                        report.page_title = partsItem.page_title ?: "page_title"
                        report.index = i
                        report.type = partsItem.type ?: "unknown_type"
                        report.config = partsItem.config ?: "null_config"
                        reportDao.insert(report)
                        i++
                    }
                    reader.endArray()
                }
            }
        }
        reader.endObject()
        LogUtil.d(TAG, "ModeImpl 报表数据解析完成")
        return generatePageList()
    }

    /**
     * 查询筛选条件
     * @uuid  报表唯一标识
     * @return 筛选实体类
     */
    private fun queryFilter(uuid: String) {
        val reportDao = DaoUtil.getReportDao()
        val filter = reportDao.queryBuilder()
                .where(reportDao.queryBuilder()
                        .and(ReportDao.Properties.Uuid.eq(uuid), ReportDao.Properties.Type.eq("filter"))).unique()
        filterObject = JSON.parseObject(filter.config, Filter::class.java)
    }

    /**
     * 查询单个页面的所有报表组件
     * @pageId 页面id
     * @return pageId 对应页面的所有报表组件
     */
    fun queryPageData(pageId: Int): List<Report> {
        val reportDao = DaoUtil.getReportDao()
        return if (null == filterObject.data) {
            reportDao.queryBuilder()
                    .where(reportDao.queryBuilder()
                            .and(ReportDao.Properties.Uuid.eq(uuid), ReportDao.Properties.Page_title.eq(pageTitleList[pageId])))
                    .list()
        } else {
            reportDao.queryBuilder()
                    .where(reportDao.queryBuilder()
                            .and(ReportDao.Properties.Uuid.eq(uuid), ReportDao.Properties.Page_title.eq(pageTitleList[pageId]), ReportDao.Properties.Name.eq(filterObject!!.display)))
                    .list()
        }
    }

    /**
     * 查询单个组件的 Config
     * @index 组件 index 标识
     * @pageId 组件所在页面的 id
     * @return 组件 config
     */
    fun queryModuleConfig(index: Int, pageId: Int): String {
        val reportDao = DaoUtil.getReportDao()
        return if (null == filterObject.data) {
            reportDao.queryBuilder()
                    .where(reportDao.queryBuilder()
                            .and(ReportDao.Properties.Uuid.eq(uuid), ReportDao.Properties.Page_title.eq(pageTitleList[pageId]), ReportDao.Properties.Index.eq(index)))
                    .unique().config
        } else {
            reportDao.queryBuilder()
                    .where(reportDao.queryBuilder()
                            .and(ReportDao.Properties.Uuid.eq(uuid), ReportDao.Properties.Page_title.eq(pageTitleList[pageId]), ReportDao.Properties.Name.eq(filterObject.display), ReportDao.Properties.Index.eq(index)))
                    .unique().config
        }
    }

    /**
     * 更新筛选条件
     * @display 当前已选的筛选项
     * @callback 数据加载回调
     */
    fun updateFilter(display: String, callback: ModeModel.LoadDataCallback) {
        filterObject.display = display

        val reportDao = DaoUtil.getReportDao()
        val filter = reportDao.queryBuilder()
                .where(reportDao.queryBuilder()
                        .and(ReportDao.Properties.Uuid.eq(uuid), ReportDao.Properties.Type.eq("filter"))).unique()

        filter.name = display
        filter.config = JSON.toJSONString(filterObject)

        reportDao.update(filter)
        checkReportData(callback)
    }

    /**
     * 根页签去重
     * @reports 当前报表所有数据
     * @return 去重后的根页签
     */
    private fun generatePageList(): List<String> {
        pageTitleList.clear()
        var distinctPageTitle = sqlDistinctPageTitle

        if (null != filterObject.data) {
            distinctPageTitle = sqlDistinctPageTitle + " WHERE " + ReportDao.Properties.Name.columnName + " = \'" + filterObject.display + "\'"
        }

        var cursor = DaoUtil.getDaoSession()!!.database.rawQuery(distinctPageTitle, null)

        try {
            if (cursor.moveToFirst()) {
                do {
                    if ("filter" != cursor.getString(0)) {
                        pageTitleList.add(cursor.getString(0))
                    }
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
        }

        return pageTitleList
    }
}
