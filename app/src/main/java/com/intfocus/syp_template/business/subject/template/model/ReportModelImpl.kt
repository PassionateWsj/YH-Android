package com.intfocus.syp_template.business.subject.template.model

import android.content.Context
import com.intfocus.syp_template.YHApplication.globalContext
import com.intfocus.syp_template.general.bean.Report
import com.intfocus.syp_template.general.gen.ReportDao
import com.intfocus.syp_template.general.util.ApiHelper
import com.intfocus.syp_template.general.util.DaoUtil
import com.intfocus.syp_template.general.util.HttpUtil
import com.intfocus.syp_template.general.util.URLs
import com.intfucos.yhdev.constant.Params.REPORT_TYPE_MAIN_DATA
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
    private lateinit var reportDao: ReportDao
    private var report: Report? = null

    /**
     * 报表数据是否有更新
     */
    override fun update(url: String): Boolean {
        var header = ApiHelper.checkResponseHeader(url)
        var response = HttpUtil.downloadResponse(url, header)

        return "200" == response[URLs.kCode]
    }

    /**
     * 本地数据库中是否存在该报表数据
     */
    override fun available(uuid: String): Boolean {
        reportDao = DaoUtil.getReportDao()
        report = reportDao.queryBuilder()
                .where(reportDao.queryBuilder().and(ReportDao.Properties.Uuid.eq(uuid), ReportDao.Properties.Type.eq(REPORT_TYPE_MAIN_DATA)))
                .unique()

        return null != report
    }

    override fun insertDb(uuid: String, config: String) {
        reportDao = DaoUtil.getReportDao()
        if (null != report) {
            reportDao.delete(report)
        }

        report = Report()
        report!!.id = null
        report!!.uuid = uuid
        report!!.index = "1000"
        report!!.config = config
        report!!.type = REPORT_TYPE_MAIN_DATA

        reportDao.insert(report)
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
