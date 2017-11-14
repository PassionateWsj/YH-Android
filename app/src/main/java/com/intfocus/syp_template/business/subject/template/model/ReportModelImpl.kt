package com.intfocus.syp_template.business.subject.template.model

import com.intfocus.syp_template.YHApplication.globalContext
import com.intfocus.syp_template.general.bean.Report
import com.intfocus.syp_template.general.gen.ReportDao
import com.intfocus.syp_template.general.util.ApiHelper
import com.intfocus.syp_template.general.util.DaoUtil
import com.intfocus.syp_template.general.util.HttpUtil
import com.intfocus.syp_template.general.util.URLs
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * @author liuruilin
 * @data 2017/11/10
 * @describe
 */
open class ReportModelImpl : ReportModel {
    private var reportDao: ReportDao = DaoUtil.getReportDao()
    private var reports: List<Report> = arrayListOf()

    /**
     * 报表数据是否有更新
     * @param url 报表数据下载链接
     */
    override fun checkUpdate(url: String): Boolean {
        var header = ApiHelper.checkResponseHeader(url)
        var response = HttpUtil.downloadResponse(url, header)

        return "200" == response[URLs.kCode]
    }

    /**
     * 本地数据库中是否存在该报表数据
     * @param uuid 报表唯一标识 uuid = reportId + templateId + groupId
     */
    override fun available(uuid: String): Boolean {
        reports = reportDao.queryBuilder()
                .where(ReportDao.Properties.Uuid.eq(uuid))
                .list()

        return reports.isNotEmpty()
    }

    /**
     * 插入报表数据
     * @param uuid 报表唯一标识 uuid = reportId + templateId + groupId
     * @param type 报表组件类型
     * @param config 报表组件配置Json数据
     * @param index 报表组件对应index
     */
    override fun insert(uuid: String, config: String, type: String, index: Int, page: Int) {
        var report = Report()
        report.id = null
        report.config = config
        report.type = type
        report.uuid = uuid
        report.index = index
        report.page = page

        reportDao.insert(report)
    }

    fun insertMainData(uuid: String, config: String, type: String, title: String, page: Int) {
        var report = Report()
        report.id = null
        report.config = config
        report.type = type
        report.uuid = uuid
        report.index = 1000
        report.page = page
        report.title = title

        reportDao.insert(report)
    }

    /**
     * 删除 uuid = xxx 的所有数据
     * @param uuid 报表唯一标识 uuid = reportId + templateId + groupId
     */
    override fun delete(uuid: String) {
        reports = reportDao.queryBuilder()
                .where(ReportDao.Properties.Uuid.eq(uuid))
                .list()

        if (reports.isNotEmpty()) {
            for (report in reports) {
                reportDao.deleteByKey(report.id)
            }
        }
    }

    /**
     * 加载本地 json 测试数据的方法
     * @param assetsName 本地 Json 数据文件名
     * @return
     */
    fun getAssetsJsonData(assetsName:String): String {
        var inputStream: InputStream? = null
        var reader: BufferedReader? = null
        var sb: StringBuilder? = null
        try {
            inputStream = globalContext.resources.assets.open(assetsName)
            reader = BufferedReader(InputStreamReader(inputStream!!))
            sb = StringBuilder()
            var line: String?
            while (true) {
                line = reader.readLine()
                if (line != null) sb.append(line + "\n") else break

            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (reader != null) {
                    reader.close()
                }
                if (inputStream != null) {
                    inputStream.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb!!.toString()
    }
}
