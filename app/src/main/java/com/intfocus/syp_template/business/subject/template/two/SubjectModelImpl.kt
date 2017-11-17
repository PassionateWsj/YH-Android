package com.intfocus.syp_template.business.subject.template.two

import android.text.TextUtils
import android.util.Log
import com.intfocus.syp_template.YHApplication.globalContext
import com.intfocus.syp_template.business.subject.template.model.ReportModelImpl
import com.intfocus.syp_template.general.constant.ConfigConstants
import com.intfocus.syp_template.general.util.*
import com.intfocus.syp_template.general.util.ApiHelper.deleteHeadersFile
import com.intfocus.syp_template.collection.callback.LoadDataCallback
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.HashMap

/**
 * @author liuruilin
 * @data 2017/11/15
 * @describe
 */
class SubjectModelImpl : ReportModelImpl() {
    private var url = ""
    private var jsFileName = ""

    companion object {
        private val TAG = "SubjectModelImpl"
        private var INSTANCE: SubjectModelImpl? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): SubjectModelImpl {
            return INSTANCE ?: SubjectModelImpl()
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

    /**
     * 检测报表是否需要更新数据
     */
    fun checkReportData(reportId: String, templateId: String, groupId: String, callback: LoadDataCallback<String>) {
        url = String.format(K.K_REPORT_ZIP_DATA, ConfigConstants.kBaseUrl, URLs.MD5(K.ANDROID_API_KEY + K.K_REPORT_BASE_API + K.ANDROID_API_KEY), groupId, templateId, reportId)
        jsFileName = String.format("group_%s_template_%s_report_%s.js", groupId, templateId, reportId)

        Observable.just(jsFileName)
                .subscribeOn(Schedulers.io())
                .map {
                    var htmlResponse = generateHtml()
                    var htmlPath = htmlResponse["path"]?: ""
                    if (htmlPath.isNotEmpty()) {
                        if (checkUpdate(url)) {
                            getData(callback)
                        }
                    }
                    htmlPath
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
        var assetsPath = FileUtil.sharedPath(globalContext)
        var outputPath = FileUtil.dirPath(globalContext, K.K_CACHED_DIR_NAME, String.format("%s.zip", jsFileName))
        var response = download(url, outputPath)

        //添加code字段是否存在。原因:网络不好的情况下response为{}
        if (!response.containsKey(URLs.kCode) || !response[URLs.kCode].equals("200")) {
            callback.onError(Throwable("下载数据失败"))
        }

        try {
            val contentDis = response["Content-Disposition"]
            val jsZipFileName = contentDis!!.substring(contentDis.indexOf("\"") + 1, contentDis.lastIndexOf("\""))

            val javascriptPath = String.format("%s/assets/javascripts/%s", assetsPath, jsZipFileName.replace(".zip", ""))

            ApiHelper.storeResponseHeader(url, response)

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
        var retMap = HashMap<String, String>(16)
        var htmlName = HttpUtil.urlToFileName(url)
        var htmlPath = String.format("%s/%s", FileUtil.dirPath(globalContext, K.K_HTML_DIR_NAME), htmlName)
        val relativeAssetsPath = "../../Shared/assets"
        val urlKey = if (url.contains("?")) TextUtils.split(url, "?")[0] else url

        val headers = ApiHelper.checkResponseHeader(urlKey)
        val response = HttpUtil.httpGet(globalContext, urlKey, headers)
        val statusCode = response[URLs.kCode] ?: "400"
        var htmlContent = response[URLs.kBody] ?: ""
        retMap.put(URLs.kCode, statusCode)

        if (statusCode == "200" && htmlContent.isNotEmpty()) {
            File(htmlPath).delete()
            ApiHelper.storeResponseHeader(urlKey, response)

            htmlContent = htmlContent!!.replace("/javascripts/", String.format("%s/javascripts/", relativeAssetsPath))
            htmlContent = htmlContent.replace("/stylesheets/", String.format("%s/stylesheets/", relativeAssetsPath))
            htmlContent = htmlContent.replace("/images/", String.format("%s/images/", relativeAssetsPath))
            FileUtil.writeFile(htmlPath, htmlContent)
            retMap.put("path", htmlPath)
        }

        return retMap
    }
}
