package com.intfocus.template.scanner

import android.content.Context
import com.google.gson.Gson
import com.intfocus.template.BuildConfig
import com.intfocus.template.util.FileUtil
import com.intfocus.template.util.HttpUtil
import com.intfocus.template.util.K
import com.zbl.lib.baseframe.core.AbstractMode
import org.greenrobot.eventbus.EventBus
import org.xutils.common.Callback
import org.xutils.common.Callback.CancelledException
import org.xutils.common.task.PriorityExecutor
import org.xutils.http.RequestParams
import org.xutils.x
import java.io.File
import java.util.*


/**
 * 扫码结果页 model
 * @author Liurl21
 * create at 2017/7/6 下午3:18
 */
class ScannerMode(var ctx: Context) : AbstractMode() {
    var result: String? = null
    var mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
    var gson = Gson()
    var jsUrl = ""
    var htmlUrl = ""
    var store_id = ""
    var currentBarcode = ""

    fun requestData(barcode: String, storeId: String) {
        currentBarcode = barcode
        jsUrl = String.format(K.API_BAR_CODE_SCAN_DATA, BuildConfig.BASE_URL, storeId, barcode)
        htmlUrl = String.format(K.API_BAR_CODE_SCAN_VIEW, BuildConfig.BASE_URL, storeId, barcode)
        store_id = storeId
        requestData()
    }

    override fun requestData() {
        if (!jsUrl.isEmpty()) {
            val params = RequestParams(jsUrl)
            val jsFileName = String.format("store_%s_barcode_%s.js", store_id, currentBarcode)
            val jsPath = String.format("%s/assets/javascripts/%s", FileUtil.sharedPath(ctx), jsFileName)
            params.isAutoRename = false
            params.saveFilePath = jsPath
            params.executor = PriorityExecutor(2, true)
            x.http().get(params, object : Callback.CommonCallback<File> {
                override fun onCancelled(p0: CancelledException?) {}

                override fun onError(p0: Throwable?, p1: Boolean) {
                    val result1 = ScannerRequest(false, 400)
                    result1.errorInfo = p0.toString()
                    EventBus.getDefault().post(result1)
                }

                override fun onFinished() {
                }

                override fun onSuccess(p0: File?) {
                    getHtml()
                }
            })
        }
    }

    private fun getHtml() {
        Thread(Runnable {
            val htmlName = String.format("mobile_v2_store_%s_barcode_%s.html", store_id, currentBarcode)
            val htmlPath = String.format("%s/%s", FileUtil.dirPath(ctx, K.K_HTML_DIR_NAME), htmlName)

            val response = HttpUtil.httpGet(ctx, htmlUrl, HashMap<String, String>())

            if (response["code"].equals("200")) {
                var htmlContent = response["body"]
                htmlContent = htmlContent!!.replace("/javascripts/", String.format("%s/javascripts/", "../../Shared/assets"))
                htmlContent = htmlContent.replace("/stylesheets/", String.format("%s/stylesheets/", "../../Shared/assets"))
                htmlContent = htmlContent.replace("/images/", String.format("%s/images/", "../../Shared/assets"))
                FileUtil.writeFile(htmlPath, htmlContent)
                val result1 = ScannerRequest(true, 200)
                result1.htmlPath = htmlPath
                EventBus.getDefault().post(result1)
            } else {
                val result1 = ScannerRequest(false, 400)
                result1.errorInfo = response["code"] + response["body"]
                EventBus.getDefault().post(result1)
            }
        }).start()
    }
}
