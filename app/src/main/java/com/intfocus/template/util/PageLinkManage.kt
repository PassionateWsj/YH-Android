package com.intfocus.template.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import com.intfocus.template.ConfigConstants
import com.intfocus.template.constant.Params.ACTION
import com.intfocus.template.constant.Params.BANNER_NAME
import com.intfocus.template.constant.Params.GROUP_ID
import com.intfocus.template.constant.Params.LINK
import com.intfocus.template.constant.Params.OBJECT_ID
import com.intfocus.template.constant.Params.OBJECT_TITLE
import com.intfocus.template.constant.Params.OBJECT_TYPE
import com.intfocus.template.constant.Params.TEMPLATE_ID
import com.intfocus.template.dashboard.DashboardActivity
import com.intfocus.template.scanner.BarCodeScannerActivity
import com.intfocus.template.subject.nine.CollectionActivity
import com.intfocus.template.subject.one.NativeReportActivity
import com.intfocus.template.subject.three.MultiIndexActivity
import com.intfocus.template.subject.two.WebPageActivity
import org.json.JSONException
import org.json.JSONObject

/**
 * ****************************************************
 * author jameswong
 * created on: 17/11/08 上午10:51
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
object PageLinkManage {
    private val EXTERNAL_LINK = "-1"
    private val SCANNER = "-2"
    private val TEMPLATE_ONE = "1"
    private val TEMPLATE_TWO = "2"
    private val TEMPLATE_THREE = "3"
    private val TEMPLATE_FOUR = "4"
    private val TEMPLATE_FIVE = "5"
    private val TEMPLATE_SIX = "6"
    private val TEMPLATE_NINE = "9"
    private val TEMPLATE_TEN = "10"

    private var objectTypeName = arrayOf("生意概况", "报表", "工具箱")

    fun pageLink(context: Context, objTitle: String, link: String) {
        pageLink(context, objTitle, link, "-1", "-1", "-1")
    }

    /**
     * 页面跳转事件
     */
    fun pageLink(context: Context, objTitle: String, link: String, objectId: String, templateId: String, objectType: String) {
        pageLink(context, objTitle, link, objectId, templateId, objectType, HashMap())
    }

    fun pageLink(context: Context, objTitle: String, link: String, objectId: String, templateId: String, objectType: String, paramsMappingBean: HashMap<String, String>) {
        try {
            val userSP = context.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
            val groupID = userSP.getString(GROUP_ID, "0")

            val urlString: String
            val intent: Intent

            when (templateId) {
                TEMPLATE_ONE,TEMPLATE_TEN -> {
                    savePageLink(context, objTitle, link, objectId, templateId, objectType)
                    intent = Intent(context, NativeReportActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(GROUP_ID, groupID)
                    intent.putExtra(TEMPLATE_ID, templateId)
                    intent.putExtra(BANNER_NAME, objTitle)
                    intent.putExtra(LINK, link)
                    intent.putExtra(OBJECT_ID, objectId)
                    intent.putExtra(OBJECT_TYPE, objectType)
                    context.startActivity(intent)
                }
                TEMPLATE_TWO, TEMPLATE_FOUR -> {
                    savePageLink(context, objTitle, link, objectId, templateId, objectType)
                    intent = Intent(context, WebPageActivity::class.java)
                    intent.putExtra(GROUP_ID, groupID)
                    intent.putExtra(TEMPLATE_ID, templateId)
                    intent.putExtra(BANNER_NAME, objTitle)
                    intent.putExtra(LINK, link)
                    intent.putExtra(OBJECT_ID, objectId)
                    intent.putExtra(OBJECT_TYPE, objectType)
                    context.startActivity(intent)
                }
                TEMPLATE_THREE -> {
                    savePageLink(context, objTitle, link, objectId, templateId, objectType)
                    intent = Intent(context, MultiIndexActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(GROUP_ID, groupID)
                    intent.putExtra(TEMPLATE_ID, templateId)
                    intent.putExtra(BANNER_NAME, objTitle)
                    intent.putExtra(LINK, link)
                    intent.putExtra(OBJECT_ID, objectId)
                    intent.putExtra(OBJECT_TYPE, objectType)
                    context.startActivity(intent)
                }
                TEMPLATE_NINE -> {
                    savePageLink(context, objTitle, link, objectId, templateId, objectType)
                    intent = Intent(context, CollectionActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(GROUP_ID, groupID)
                    intent.putExtra(TEMPLATE_ID, templateId)
                    intent.putExtra(BANNER_NAME, objTitle)
                    intent.putExtra(LINK, link)
                    intent.putExtra(OBJECT_ID, objectId)
                    intent.putExtra(OBJECT_TYPE, objectType)
                    context.startActivity(intent)
                }
                EXTERNAL_LINK, TEMPLATE_SIX -> {
                    var urlString = link
                    for ((key, value) in paramsMappingBean) {
                        urlString = splitUrl(userSP, urlString, key, value)
                    }
                    savePageLink(context, objTitle, link, objectId, templateId, objectType)
                    intent = Intent(context, WebPageActivity::class.java)
                    intent.putExtra(GROUP_ID, groupID)
                    intent.putExtra(TEMPLATE_ID, templateId)
                    intent.putExtra(BANNER_NAME, objTitle)
                    intent.putExtra(LINK, link)
                    intent.putExtra(OBJECT_ID, objectId)
                    intent.putExtra(OBJECT_TYPE, objectType)
                    context.startActivity(intent)
                }
                SCANNER -> {
                    var urlString = link
                    for ((key, value) in paramsMappingBean) {
//                        if (key == "user_num") {
//                            continue
//                        }
                        urlString = splitUrl(userSP, urlString, key, value)
                    }
                    savePageLink(context, objTitle, urlString, objectId, templateId, objectType)
                    intent = Intent(context, BarCodeScannerActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(LINK,urlString)
                    context.startActivity(intent)
                }
                else -> showTemplateErrorDialog(context)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val logParams = JSONObject()
        if ("-1" == templateId && "-1" != objectType) {
            logParams.put(ACTION, "点击/" + objectTypeName[objectType.toInt() - 1] + "/链接")
        } else if ("-1" != objectType){
            logParams.put(ACTION, "点击/" + objectTypeName[objectType.toInt() - 1] + "/报表")
        }

        logParams.put(OBJECT_TITLE, objTitle)
        logParams.put(OBJECT_ID, objectId)
        logParams.put(LINK, link)
        ActionLogUtil.actionLog(context, logParams)
    }

    private fun showTemplateErrorDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("温馨提示")
                .setMessage("当前版本暂不支持该模板, 请升级应用后查看")
                .setPositiveButton("前去升级") { _, _ ->
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ConfigConstants.kPgyerUrl))
                    context.startActivity(browserIntent)
                }
                .setNegativeButton("稍后升级") { _, _ ->
                    // 返回 LoginActivity

                }
        builder.show()
    }

    private fun savePageLink(context: Context, objTitle: String, link: String, objectId: String, templateId: String, objectType: String) {
        if (ConfigConstants.REVIEW_LAST_PAGE) {
            val pageLinkManagerSP = context.getSharedPreferences("PageLinkManager", Context.MODE_PRIVATE)
            val pageLinkManagerSPED = pageLinkManagerSP.edit()
            pageLinkManagerSPED.putBoolean("pageSaved", true)
            pageLinkManagerSPED.putString("objTitle", objTitle)
            pageLinkManagerSPED.putString("link", link)
            pageLinkManagerSPED.putString("objectId", objectId)
            pageLinkManagerSPED.putString("templateId", templateId)
            pageLinkManagerSPED.putString("objectType", objectType).apply()
        }
    }

    fun pageBackIntent(context: Context) {
        val pageLinkManager = context.getSharedPreferences("PageLinkManager", Context.MODE_PRIVATE)
        val pageSaved = pageLinkManager.getBoolean("pageSaved", false)
        if (pageSaved) {
            pageLinkManager.edit().putBoolean("pageSaved", false).apply()
            context.startActivity(Intent(context, DashboardActivity::class.java))
        }
    }

    private fun splitUrl(userSP: SharedPreferences, urlString: String, paramsKey: String, paramsValue: String): String {
        val params = paramsValue + "=" + userSP.getString(paramsKey, "null")
        val splitString = if (urlString.contains("?")) "&" else "?"
        return String.format("%s%s%s", urlString, splitString, params)
    }
}
