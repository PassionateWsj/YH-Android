package com.intfocus.syp_template.subject.model

import com.intfocus.syp_template.SYPApplication.globalContext
import com.intfocus.syp_template.constant.Params.CODE
import com.intfocus.syp_template.model.entity.Report
import com.intfocus.syp_template.model.gen.ReportDao
import com.intfocus.syp_template.model.DaoUtil
import com.intfocus.syp_template.util.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

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
    override fun check(url: String): Boolean {
        val header = ApiHelper.checkResponseHeader(url)
        val response = HttpUtil.downloadResponse(url, header)

        return "200" == response[CODE]
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

    override fun download(url: String, outputPath: String): HashMap<String, String> {
        val response = HashMap<String, String>()
        var input: InputStream? = null
        var output: OutputStream? = null
        var connection: HttpURLConnection? = null

        try {
            val url = URL(url)
            connection = url.openConnection() as HttpURLConnection

            connection.setRequestProperty("accept", "*/*")
            connection.setRequestProperty("connection", "Keep-Alive")
            connection.setRequestProperty("user-agent", HttpUtil.webViewUserAgent())
            connection.connectTimeout = 5 * 1000
            connection.readTimeout = 10 * 1000

            connection.connect()
            response.put(CODE, String.format("%d", connection.responseCode))
            val map = connection.headerFields
            for ((key, value) in map) {
                response.put(key, value[0])
            }
            LogUtil.d("DownloadZIP", String.format("%d - %s - %s", connection.responseCode, url, response.toString()))
            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return response
            }

            input = connection.inputStream
            output = FileOutputStream(outputPath)

            val data = ByteArray(4096)
            var total: Long = 0
            var count = 0

            while (input.read(data).let { count = it; it != -1 }) {
                total += count.toLong()
                output.write(data, 0, count)
            }

        } catch (e: Exception) {
            LogUtil.d("Exception", e.toString())
            response.put(CODE, "400")
            return response
        } finally {
            try {
                if (output != null) {
                    output.close()
                }
                if (input != null) {
                    input.close()
                }
            } catch (ignored: IOException) {
            }

            if (connection != null) {
                connection.disconnect()
            }
        }
        return response
    }

    /**
     * 插入报表数据
     * @param uuid 报表唯一标识 uuid = reportId + templateId + groupId
     * @param type 报表组件类型
     * @param config 报表组件配置Json数据
     * @param index 报表组件对应index
     */
    override fun insert(uuid: String, config: String, type: String, index: Int, page: String) {
        val report = Report()
        report.id = null
        report.config = config
        report.type = type
        report.uuid = uuid
        report.index = index
        report.page_title = page

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
     * 加载本地 assets 文件夹内 json 测试数据的方法
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
