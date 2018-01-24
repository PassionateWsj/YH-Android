package com.intfocus.template.subject.two

import android.app.AlertDialog
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.intfocus.template.SYPApplication
import com.intfocus.template.SYPApplication.globalContext
import com.intfocus.template.constant.Params.ACTION
import com.intfocus.template.constant.Params.OBJECT_ID
import com.intfocus.template.constant.Params.OBJECT_TITLE
import com.intfocus.template.constant.Params.OBJECT_TYPE
import com.intfocus.template.constant.Params.USER_BEAN
import com.intfocus.template.constant.Params.USER_ID
import com.intfocus.template.constant.Params.USER_LOCATION
import com.intfocus.template.constant.Params.USER_NUM
import com.intfocus.template.constant.ToastColor
import com.intfocus.template.general.net.ApiException
import com.intfocus.template.general.net.CodeHandledSubscriber
import com.intfocus.template.general.net.RetrofitUtil
import com.intfocus.template.model.request.CommentBody
import com.intfocus.template.model.response.BaseResult
import com.intfocus.template.model.response.filter.MenuResult
import com.intfocus.template.util.ActionLogUtil
import com.intfocus.template.util.FileUtil
import com.intfocus.template.util.K
import com.intfocus.template.util.ToastUtils
import org.json.JSONObject
import java.io.File

/**
 * @author liuruilin
 * @data 2017/11/20
 * @describe
 */
class CustomJavaScriptsInterface constructor(
        var mView: WebPageActivity
) {

    /**
     * javascript 异常时通知原生代码，提交服务器
     */
    @JavascriptInterface
    fun jsException(ex: String) {
        var errorPagePath = FileUtil.sharedPath(SYPApplication.globalContext) + "/loading/400.html"
        mView.showError("file://" + errorPagePath)

        /*
         * 用户行为记录, 单独异常处理，不可影响用户体验
         */
        var logParams = JSONObject()
        logParams.put(ACTION, "JS异常")
        logParams.put(OBJECT_ID, mView.reportId)
        logParams.put(OBJECT_TYPE, mView.objectType)
        logParams.put(OBJECT_TITLE, String.format("主题页面/%s/%s", mView.bannerName, ex))
        ActionLogUtil.actionLog(logParams)
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
    fun getLocation(): String = globalContext.getSharedPreferences(USER_BEAN, Context.MODE_PRIVATE).getString(USER_LOCATION, "0,0")

    /**
     * 获取 user_id
     * @return location
     */
    @JavascriptInterface
    fun getUserId(): String = globalContext.getSharedPreferences(USER_BEAN, Context.MODE_PRIVATE).getString(USER_ID, "0")

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
        var tabIndex: Int
        val filePath = FileUtil.dirPath(globalContext, K.K_CONFIG_DIR_NAME, K.K_TAB_INDEX_CONFIG_FILE_NAME)

        var config = JSONObject()
        if (File(filePath).exists()) {
            val fileContent = FileUtil.readFile(filePath)
            config = JSONObject(fileContent)
        }
        tabIndex = config.getInt(pageName)
        return if (tabIndex < 0) 0 else tabIndex
    }

    /**
     * 提交评论
     */
    @JavascriptInterface
    fun writeComment(content: String) {
        val commentBody = CommentBody()
        commentBody.user_num = globalContext.getSharedPreferences(USER_BEAN, Context.MODE_PRIVATE).getString(USER_NUM, "0")
        commentBody.content = content
        commentBody.object_type = mView.objectType
        commentBody.object_id = mView.reportId
        commentBody.object_title = mView.bannerName

        RetrofitUtil.getHttpService(globalContext).submitComment(commentBody)
                .compose(RetrofitUtil.CommonOptions())
                .subscribe(object : CodeHandledSubscriber<BaseResult>() {
                    override fun onError(apiException: ApiException) {
                        ToastUtils.show(globalContext, apiException.displayMessage)
                    }

                    override fun onBusinessNext(data: BaseResult) {
                        ToastUtils.show(globalContext, data.message!!, ToastColor.SUCCESS)
                    }

                    override fun onCompleted() {
                        mView.refresh()
                    }
                })

        /*
         * 用户行为记录, 单独异常处理，不可影响用户体验
         */
        val logParams = JSONObject()
        logParams.put(ACTION, "评论")
        logParams.put(OBJECT_TITLE, mView.bannerName)
        ActionLogUtil.actionLog(logParams)
    }
}
