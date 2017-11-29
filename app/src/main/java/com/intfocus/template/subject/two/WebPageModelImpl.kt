package com.intfocus.template.subject.two

import android.util.Log
import com.intfocus.template.SYPApplication.globalContext
import com.intfocus.template.subject.model.ReportModelImpl
import com.intfocus.template.subject.nine.callback.LoadDataCallback
import com.intfocus.template.ConfigConstants
import com.intfocus.template.constant.Params.BODY
import com.intfocus.template.constant.Params.CODE
import com.intfocus.template.util.*
import com.intfocus.template.util.ApiHelper.deleteHeadersFile
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

/**
 * @author liuruilin
 * @data 2017/11/15
 * @describe
 */
class WebPageModelImpl : ReportModelImpl() {
    private var jsUrl = ""
    private var htmlUrl = ""
    private var jsFileName = ""

    companion object {
        private val TAG = "WebPageModelImpl"
        private var sINSTANCE: WebPageModelImpl? = null
        private var observable: Subscription? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): WebPageModelImpl {
            return sINSTANCE ?: WebPageModelImpl()
                    .apply { sINSTANCE = this }
        }

        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic
        fun destroyInstance() {
            unSubscribe()
            sINSTANCE = null
        }

        /**
         * 取消订阅
         */
        private fun unSubscribe() {
            observable?.unsubscribe() ?: return
        }
    }

    /**
     * 获取报表数据
     */
    fun getReportData(reportId: String, templateId: String, groupID: String, callback: LoadDataCallback<String>) {
        var groupId = groupID
        // 模板 4 的 groupID 为 0
        if (Integer.valueOf(templateId) == 4) {
            groupId = "0"
        }
        jsUrl = String.format(K.K_REPORT_ZIP_DATA, ConfigConstants.kBaseUrl, URLs.MD5(K.ANDROID_API_KEY + K.K_REPORT_BASE_API + K.ANDROID_API_KEY), groupId, templateId, reportId)
        jsFileName = String.format("group_%s_template_%s_report_%s.js", groupId, templateId, reportId)
        htmlUrl = String.format(K.K_REPORT_HTML, ConfigConstants.kBaseUrl, groupId, templateId, reportId)

        observable = Observable.just(jsFileName)
                .subscribeOn(Schedulers.io())
                .map {
                    val reportPath: String

                    val htmlResponse = generateHtml()
                    reportPath = htmlResponse["path"] ?: ""
                    if (reportPath.isNotEmpty()) {
                        if (check(jsUrl)) {
                            getData(callback)
                        }
                    }

                    reportPath
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<String>() {
                    override fun onCompleted() {
                    }

                    override fun onNext(t: String?) {
                        callback.onSuccess("file://" + t!!)
                    }

                    override fun onError(e: Throwable?) {
                        Log.i(TAG, e.toString())
                    }

                })
    }

    /**
     * 获取 PDF 文件
     */
    fun getPdfFilePath(url: String, callback: LoadDataCallback<String>) {
        Observable.just(jsFileName)
                .subscribeOn(Schedulers.io())
                .map {
                    var reportPath = ""
                    if (url.toLowerCase().endsWith(".pdf")) {
                        reportPath = downloadPdfFile(url)
                    }
                    reportPath
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<String>() {
                    override fun onCompleted() {
                    }

                    override fun onNext(t: String?) {
                        callback.onSuccess(t!!)
                    }

                    override fun onError(e: Throwable?) {
                        Log.i(TAG, e.toString())
                    }
                })
    }

    /**
     * 获取报表最新数据
     */
    fun getData(callback: LoadDataCallback<String>) {
        val assetsPath = FileUtil.sharedPath(globalContext)
        val outputPath = FileUtil.dirPath(globalContext, K.K_CACHED_DIR_NAME, String.format("%s.zip", jsFileName))
        val response = download(jsUrl, outputPath)

        //添加code字段是否存在。原因:网络不好的情况下response为{}
        if (!response.containsKey(CODE) || !response[CODE].equals("200")) {
            callback.onError(Throwable("下载数据失败"))
        }

        try {
            val contentDis = response["Content-Disposition"]
            val jsZipFileName = contentDis!!.substring(contentDis.indexOf("\"") + 1, contentDis.lastIndexOf("\""))

            val javascriptPath = String.format("%s/assets/javascripts/%s", assetsPath, jsZipFileName.replace(".zip", ""))

            ApiHelper.storeResponseHeader(jsUrl, response)

            val zipStream = FileInputStream(outputPath)
            FileUtil.unZip(zipStream, FileUtil.dirPath(globalContext, K.K_CACHED_DIR_NAME), true)
            zipStream.close()
            val jsFilePath = FileUtil.dirPath(globalContext, K.K_CACHED_DIR_NAME, jsFileName)
            val jsFile = File(jsFilePath)
            if (jsFile.exists()) {
                FileUtil.copyFile(jsFilePath, javascriptPath)
                jsFile.delete()
            }
            File(outputPath).delete()

            val searchItemsPath = String.format("%s.search_items", javascriptPath)
            val searchItemsFile = File(searchItemsPath)
            if (searchItemsFile.exists()) {
                searchItemsFile.delete()
            }

        } catch (e: IOException) {
            e.printStackTrace()
            deleteHeadersFile()
            callback.onError(Throwable("下载数据失败"))
        }
    }

    /**
     * 下载报表 html 页面
     */
    private fun generateHtml(): Map<String, String> {
        val retMap = HashMap<String, String>(16)
        val htmlName = HttpUtil.urlToFileName(htmlUrl)
        val htmlPath = String.format("%s/%s", FileUtil.dirPath(globalContext, K.K_HTML_DIR_NAME), htmlName)
        val relativeAssetsPath = "../../Shared/assets"

        val headers = ApiHelper.checkResponseHeader(htmlUrl)
        val response = HttpUtil.httpGet(globalContext, htmlUrl, headers)
        val statusCode = response[CODE] ?: "400"
        var htmlContent = response[BODY] ?: ""
        retMap.put(CODE, statusCode)

        when (statusCode) {
            "200" -> {
                File(htmlPath).delete()
                ApiHelper.storeResponseHeader(htmlUrl, response)

                htmlContent = htmlContent.replace("/javascripts/", String.format("%s/javascripts/", relativeAssetsPath))
                htmlContent = htmlContent.replace("/stylesheets/", String.format("%s/stylesheets/", relativeAssetsPath))
                htmlContent = htmlContent.replace("/images/", String.format("%s/images/", relativeAssetsPath))
                FileUtil.writeFile(htmlPath, htmlContent)
                retMap.put("path", htmlPath)
            }
            "304" -> {
                retMap.put("path", htmlPath)
            }
            else -> {
            }
        }

        return retMap
    }

    private fun downloadPdfFile(pdfUrl: String): String {
        val outputPath = String.format("%s/%s/%s.pdf", FileUtil.basePath(globalContext), K.K_CACHED_DIR_NAME, URLs.MD5(pdfUrl))
        val pdfFile = File(outputPath)
        ApiHelper.downloadFile(globalContext, pdfUrl, pdfFile)
        return outputPath
    }
}
