package com.intfocus.syp_template.general.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import com.intfocus.syp_template.business.dashboard.DashboardActivity
import com.intfocus.syp_template.business.scanner.BarCodeScannerActivity
import com.intfocus.syp_template.business.subject.template.five.TemplateFiveActivity
import com.intfocus.syp_template.business.subject.template.one.TemplateOneActivity
import com.intfocus.syp_template.business.subject.template.three.TemplateThreeActivity
import com.intfocus.syp_template.business.subject.template.two.SubjectActivity
import com.intfocus.syp_template.business.subject.webapplication.WebApplicationActivity
import com.intfocus.syp_template.business.subject.webapplication.WebApplicationActivityV6
import com.intfucos.yhdev.collection.CollectionActivity
import com.intfocus.syp_template.general.constant.ConfigConstants
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

    private var objectTypeName = arrayOf("生意概况", "报表", "工具箱")
    /**
     * 页面跳转事件
     */
    fun pageLink(context: Context, objTitle: String, link: String, objectId: String, templateId: String, objectType: String) {
        pageLink(context, objTitle, link, objectId, templateId, objectType, HashMap())
    }

    fun pageLink(context: Context, objTitle: String, link: String, objectId: String, templateId: String, objectType: String, paramsMappingBean: HashMap<String, String>) {
        try {
            val userSP = context.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
            val groupID = userSP.getString(URLs.kGroupId, "0")

            val urlString: String
            val intent: Intent

            when (templateId) {
                TEMPLATE_ONE -> {
                    savePageLink(context, objTitle, link, objectId, templateId, objectType)
                    intent = Intent(context, TemplateOneActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(URLs.kBannerName, objTitle)
                    intent.putExtra(URLs.kObjectId, objectId)
                    intent.putExtra(URLs.kObjectType, objectType)
                    intent.putExtra(URLs.kGroupId, groupID)
                    intent.putExtra(URLs.kLink, link)
                    intent.putExtra(URLs.kTemplatedId, templateId)
                    context.startActivity(intent)
                }
                TEMPLATE_TWO -> {
                    savePageLink(context, objTitle, link, objectId, templateId, objectType)
                    intent = Intent(context, SubjectActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(URLs.kBannerName, objTitle)
                    intent.putExtra(URLs.kLink, link)
                    intent.putExtra(URLs.kObjectId, objectId)
                    intent.putExtra(URLs.kObjectType, objectType)
                    intent.putExtra(URLs.kTemplatedId, templateId)
                    intent.putExtra("groupID", groupID)
                    context.startActivity(intent)
                }
                TEMPLATE_THREE -> {
                    savePageLink(context, objTitle, link, objectId, templateId, objectType)
                    intent = Intent(context, TemplateThreeActivity::class.java)
                    urlString = String.format("%s/api/v1/group/%s/template/%s/report/%s/json",
                            ConfigConstants.kBaseUrl, groupID, "3", objectId)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(URLs.kBannerName, objTitle)
                    intent.putExtra(URLs.kObjectId, objectId)
                    intent.putExtra(URLs.kObjectType, objectType)
                    intent.putExtra(URLs.kTemplatedId, templateId)
                    intent.putExtra("groupID", groupID)
                    intent.putExtra("urlString", urlString)
                    context.startActivity(intent)
                }
                TEMPLATE_FOUR -> {
                    savePageLink(context, objTitle, link, objectId, templateId, objectType)
                    intent = Intent(context, SubjectActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(URLs.kBannerName, objTitle)
                    intent.putExtra(URLs.kLink, link)
                    intent.putExtra(URLs.kObjectId, objectId)
                    intent.putExtra(URLs.kObjectType, objectType)
                    intent.putExtra(URLs.kTemplatedId, templateId)
                    intent.putExtra("groupID", groupID)
                    context.startActivity(intent)
                }
                TEMPLATE_FIVE -> {
                    savePageLink(context, objTitle, link, objectId, templateId, objectType)
                    intent = Intent(context, TemplateFiveActivity::class.java)
                    urlString = String.format("%s/api/v1/group/%s/template/%s/report/%s/json",
                            ConfigConstants.kBaseUrl, groupID, "5", objectId)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(URLs.kBannerName, objTitle)
                    intent.putExtra(URLs.kObjectId, objectId)
                    intent.putExtra(URLs.kObjectType, objectType)
                    intent.putExtra(URLs.kTemplatedId, templateId)
                    intent.putExtra("groupID", groupID)
                    intent.putExtra("urlString", urlString)
                    context.startActivity(intent)
                }
                TEMPLATE_SIX -> {
                    savePageLink(context, objTitle, link, objectId, templateId, objectType)
                    intent = Intent(context, WebApplicationActivityV6::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(URLs.kBannerName, objTitle)
                    intent.putExtra(URLs.kLink, link)
                    intent.putExtra(URLs.kObjectId, objectId)
                    intent.putExtra(URLs.kObjectType, objectType)
                    intent.putExtra(URLs.kTemplatedId, templateId)
                    context.startActivity(intent)
                }
                TEMPLATE_NINE -> {
                    savePageLink(context, objTitle, link, objectId, templateId, objectType)
                    intent = Intent(context, CollectionActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(URLs.kBannerName, objTitle)
                    intent.putExtra(URLs.kLink, link)
                    intent.putExtra(URLs.kGroupId, groupID)
                    intent.putExtra(URLs.kObjectId, objectId)
                    intent.putExtra(URLs.kObjectType, objectType)
                    intent.putExtra(URLs.kTemplatedId, templateId)
                    context.startActivity(intent)
                }
                EXTERNAL_LINK -> {
                    var urlString = link
                    for ((key, value) in paramsMappingBean) {
                        if (key == "user_num") {
                            continue
                        }
                        urlString = splitUrl(userSP, urlString, key, value)
                    }
                    savePageLink(context, objTitle, link, objectId, templateId, objectType)
                    intent = Intent(context, WebApplicationActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(URLs.kBannerName, objTitle)
                    intent.putExtra(URLs.kLink, link)
                    intent.putExtra(URLs.kObjectId, objectId)
                    intent.putExtra(URLs.kObjectType, objectType)
                    intent.putExtra(URLs.kTemplatedId, templateId)
                    context.startActivity(intent)
                }
                SCANNER -> {
                    savePageLink(context, objTitle, link, objectId, templateId, objectType)
                    intent = Intent(context, BarCodeScannerActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    context.startActivity(intent)
                }
                else -> showTemplateErrorDialog(context)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val logParams = JSONObject()
        if ("-1" == templateId) {
            logParams.put(URLs.kAction, "点击/" + objectTypeName[objectType.toInt() - 1] + "/链接")
        } else {
            logParams.put(URLs.kAction, "点击/" + objectTypeName[objectType.toInt() - 1] + "/报表")
        }

        logParams.put(URLs.kObjTitle, objTitle)
        logParams.put("obj_id", objectId)
        logParams.put("obj_link", link)
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
        val pageLinkManagerSP = context.getSharedPreferences("PageLinkManager", Context.MODE_PRIVATE)
        val pageLinkManagerSPED = pageLinkManagerSP.edit()
        pageLinkManagerSPED.putBoolean("pageSaved", true)
        pageLinkManagerSPED.putString("objTitle", objTitle)
        pageLinkManagerSPED.putString("link", link)
        pageLinkManagerSPED.putString("objectId", objectId)
        pageLinkManagerSPED.putString("templateId", templateId)
        pageLinkManagerSPED.putString("objectType", objectType).apply()
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
