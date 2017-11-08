package com.intfocus.syp_template.general.util

import android.content.Context
import android.content.Intent
import com.intfocus.syp_template.business.dashboard.DashboardActivity
import com.intfocus.syp_template.business.scanner.BarCodeScannerActivity
import com.intfocus.syp_template.business.subject.template.five.TemplateFiveActivity
import com.intfocus.syp_template.business.subject.template.one.TemplateOneActivity
import com.intfocus.syp_template.business.subject.template.three.TemplateThreeActivity
import com.intfocus.syp_template.business.subject.template.two.SubjectActivity
import com.intfocus.syp_template.business.subject.webapplication.WebApplicationActivity
import com.intfocus.syp_template.business.subject.webapplication.WebApplicationActivityV6
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
    private var objectTypeName = arrayOf("生意概况", "报表", "工具箱")
    /*
     * 页面跳转事件
     */
    fun pageLink(context: Context, objTitle: String, link: String, objectId: String, templateId: String, objectType: String) {
        try {
            val groupID = context.getSharedPreferences("UserBean", Context.MODE_PRIVATE).getString(URLs.kGroupId, "0")

            savePageLink(context, objTitle, link, objectId, templateId, objectType)
            val urlString: String
            val intent: Intent

            when (templateId) {
                "1" -> {
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
                "2" -> {
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
                "3" -> {
                    intent = Intent(context, TemplateThreeActivity::class.java)
                    urlString = String.format("%s/api/v1/group/%s/template/%s/report/%s/json",
                            K.kBaseUrl, groupID, "3", objectId)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(URLs.kBannerName, objTitle)
                    intent.putExtra(URLs.kObjectId, objectId)
                    intent.putExtra(URLs.kObjectType, objectType)
                    intent.putExtra(URLs.kTemplatedId, templateId)
                    intent.putExtra("groupID", groupID)
                    intent.putExtra("urlString", urlString)
                    context.startActivity(intent)
                }
                "4" -> {
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
                "5" -> {
                    intent = Intent(context, TemplateFiveActivity::class.java)
                    urlString = String.format("%s/api/v1/group/%s/template/%s/report/%s/json",
                            K.kBaseUrl, groupID, "5", objectId)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(URLs.kBannerName, objTitle)
                    intent.putExtra(URLs.kObjectId, objectId)
                    intent.putExtra(URLs.kObjectType, objectType)
                    intent.putExtra(URLs.kTemplatedId, templateId)
                    intent.putExtra("groupID", groupID)
                    intent.putExtra("urlString", urlString)
                    context.startActivity(intent)
                }
                "6" -> {
                    intent = Intent(context, WebApplicationActivityV6::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(URLs.kBannerName, objTitle)
                    intent.putExtra(URLs.kLink, link)
                    intent.putExtra(URLs.kObjectId, objectId)
                    intent.putExtra(URLs.kObjectType, objectType)
                    intent.putExtra(URLs.kTemplatedId, templateId)
                    context.startActivity(intent)
                }
                "-1" -> {
                    intent = Intent(context, WebApplicationActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(URLs.kBannerName, objTitle)
                    intent.putExtra(URLs.kLink, link)
                    intent.putExtra(URLs.kObjectId, objectId)
                    intent.putExtra(URLs.kObjectType, objectType)
                    intent.putExtra(URLs.kTemplatedId, templateId)
                    context.startActivity(intent)
                }
                "-2" -> {
                    intent = Intent(context, BarCodeScannerActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    context.startActivity(intent)
                }
                else -> {
                }
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

    private fun savePageLink(context: Context, objTitle: String, link: String, objectId: String, templateId: String, objectType: String) {
        val pageLinkManagerSP = context.getSharedPreferences("PageLinkManager", Context.MODE_PRIVATE)
        val pageLinkManagerSPED = pageLinkManagerSP.edit()
        pageLinkManagerSPED.putBoolean("pageSaved",true)
        pageLinkManagerSPED.putString("objTitle",objTitle)
        pageLinkManagerSPED.putString("link",link)
        pageLinkManagerSPED.putString("objectId",objectId)
        pageLinkManagerSPED.putString("templateId",templateId)
        pageLinkManagerSPED.putString("objectType",objectType).apply()
    }
    fun pageBackIntent(context: Context){
        val pageLinkManager = context.getSharedPreferences("PageLinkManager", Context.MODE_PRIVATE)
        val pageSaved = pageLinkManager.getBoolean("pageSaved", false)
        if (pageSaved) {
            pageLinkManager.edit().putBoolean("pageSaved", false).apply()
            context.startActivity(Intent(context, DashboardActivity::class.java))
        }
    }
}
