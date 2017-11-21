package com.intfocus.syp_template.business.subject.template.two

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent.getIntent
import android.os.Environment
import android.text.TextUtils
import android.view.View
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.intfocus.syp_template.YHApplication
import com.intfocus.syp_template.YHApplication.globalContext
import com.intfocus.syp_template.general.data.response.filter.MenuResult
import com.intfocus.syp_template.general.net.RetrofitUtil
import com.intfocus.syp_template.general.util.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * @author liuruilin
 * @data 2017/11/20
 * @describe
 */
class SubjectJavaScriptsInterface constructor(
        var mView: SubjectActivity2
) {
    /**
     * javascript 异常时通知原生代码，或提交服务器，或 popup 提示用户
     */
    @JavascriptInterface
    fun jsException(ex: String) {
        /*
         * 用户行为记录, 单独异常处理，不可影响用户体验
         */
        var logParams = JSONObject()
        logParams.put(URLs.kAction, "JS异常")
        logParams.put("obj_id", mView.reportId)
        logParams.put(URLs.kObjType, mView.objectType)
        logParams.put(URLs.kObjTitle, String.format("主题页面/%s/%s", mView.bannerName, ex))
        ActionLogUtil.actionLog(globalContext, logParams)
    }

    @JavascriptInterface
    fun reportSearchItems(arrayString: String) {
    }

    /**
     * 设置报表标题
     */
    @JavascriptInterface
    fun setBannerTitle(bannerTitle: String) {
        mView.runOnUiThread { mView.setBannerTitle(bannerTitle) }
    }

    /**
     * 显示/隐藏报表标题栏
     */
    @JavascriptInterface
    fun toggleShowBanner(state: String) {
        mView.runOnUiThread { mView.setBannerVisibility(if ("show" == state) View.VISIBLE else View.GONE) }
    }

    /**
     * 显示/隐藏报表标题栏返回按钮
     */
    @JavascriptInterface
    fun toggleShowBannerBack(state: String) {
        mView.runOnUiThread { mView.setBannerBackVisibility(if ("show" == state) View.VISIBLE else View.GONE) }
    }

    /**
     * 显示/隐藏报表标题栏 "+" 按钮
     */
    @JavascriptInterface
    fun toggleShowBannerMenu(state: String) {
        mView.runOnUiThread { mView.setBannerMenuVisibility(if ("show" == state) View.VISIBLE else View.GONE) }
    }

    /**
     * 刷新报表
     */
    @JavascriptInterface
    fun refreshBrowser() {
        mView.runOnUiThread { mView.refresh() }
    }

    /**
     * 获取当前坐标
     * @return location
     */
    @JavascriptInterface
    fun getLocation(): String = globalContext.getSharedPreferences("UserBean", Context.MODE_PRIVATE).getString("location", "0,0")

    @JavascriptInterface
    fun goBack(info: String) {
        mView.runOnUiThread { mView.goBack() }
    }

    /**
     * 关闭当前报表
     */
    @JavascriptInterface
    fun closeSubjectView() {
        mView.runOnUiThread { mView.finishActivity() }
    }

    @JavascriptInterface
    fun showAlert(title: String, content: String) {
        mView.runOnUiThread {
            val builder = AlertDialog.Builder(mView)
            builder.setTitle(title)
                    .setMessage(content)
                    .setPositiveButton("确定") { dialog, _ -> dialog.dismiss() }
            builder.show()
        }
    }

    /**
     * 已筛选项
     */
    @JavascriptInterface
    fun reportSelectedItem(): String {
        var item = ""
        val selectedItemPath = String.format("%s.selected_item", FileUtil.reportJavaScriptDataPath(globalContext, mView.groupId, mView.templateId, mView.reportId))
        if (File(selectedItemPath).exists()) {
            item = FileUtil.readFile(selectedItemPath)
            val filterText = item
            mView.runOnUiThread { mView.setAddressFilterText(filterText) }
        }

        return item
    }

    /**
     * 供筛选的数据
     */
    @JavascriptInterface
    fun reportSearchItemsV2(arrayString: String) {
        if (!TextUtils.isEmpty(arrayString)) {
            val msg = Gson().fromJson(arrayString, MenuResult::class.java)
            if (msg != null && msg.data.isNotEmpty()) {
                for (menu in msg.data) {
                    if (menu.data!!.isNotEmpty()) {
                        if ("location" == menu.type) {
                            mView.locationDataList = menu.data!!
                            val selectedItemPath = String.format("%s.selected_item", FileUtil.reportJavaScriptDataPath(globalContext, mView.groupId, mView.templateId, mView.reportId))
                            if (!File(selectedItemPath).exists()) {
                                if (mView.locationDataList != null) {
                                    mView.runOnUiThread {
                                        mView.setAddressFilterText(menu.current_location!!.display!!)
                                    }
                                }
                            }
                        }
                    }
                    if ("faster_select" == menu.type) {
                        mView.menuDatas = menu.data!!
                    }
                }
            }
        }
    }

    @JavascriptInterface
    fun saveParam(isSave: String, local: Int) {
    }

    @JavascriptInterface
    fun storeTabIndex(pageName: String, tabIndex: Int) {
        val filePath = FileUtil.dirPath(globalContext, K.K_CONFIG_DIR_NAME, K.K_TAB_INDEX_CONFIG_FILE_NAME)

        var config = JSONObject()
        if (File(filePath).exists()) {
            val fileContent = FileUtil.readFile(filePath)
            config = JSONObject(fileContent)
        }
        config.put(pageName, tabIndex)

        FileUtil.writeFile(filePath, config.toString())
    }

    @JavascriptInterface
    fun restoreTabIndex(pageName: String): Int {
        var tabIndex = 0
        val filePath = FileUtil.dirPath(globalContext, K.K_CONFIG_DIR_NAME, K.K_TAB_INDEX_CONFIG_FILE_NAME)

        var config = JSONObject()
        if (File(filePath).exists()) {
            val fileContent = FileUtil.readFile(filePath)
            config = JSONObject(fileContent)
        }
        tabIndex = config.getInt(pageName)
        return if (tabIndex < 0) 0 else tabIndex
    }

}
