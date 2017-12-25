package com.intfocus.template.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import com.alibaba.fastjson.TypeReference
import com.intfocus.template.BuildConfig
import com.intfocus.template.ConfigConstants
import com.intfocus.template.constant.Params.ACTION
import com.intfocus.template.constant.Params.BANNER_NAME
import com.intfocus.template.constant.Params.GROUP_ID
import com.intfocus.template.constant.Params.LINK
import com.intfocus.template.constant.Params.OBJECT_ID
import com.intfocus.template.constant.Params.OBJECT_TITLE
import com.intfocus.template.constant.Params.OBJECT_TYPE
import com.intfocus.template.constant.Params.TEMPLATE_ID
import com.intfocus.template.constant.Params.TIME_STAMP
import com.intfocus.template.constant.Params.USER_BEAN
import com.intfocus.template.constant.Params.USER_NUM
import com.intfocus.template.dashboard.DashboardActivity
import com.intfocus.template.dashboard.feedback.FeedbackActivity
import com.intfocus.template.dashboard.mine.activity.ShowPushMessageActivity
import com.intfocus.template.model.entity.DashboardItem
import com.intfocus.template.model.entity.PushMsgBean
import com.intfocus.template.scanner.BarCodeScannerActivity
import com.intfocus.template.subject.nine.CollectionActivity
import com.intfocus.template.subject.one.NativeReportActivity
import com.intfocus.template.subject.seven.MyConcernActivity
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
    private val PUSH_MESSAGE_LIST = "-4"
    private val FEEDBACK = "-3"
    private val SCANNER = "-2"
    private val EXTERNAL_LINK = "-1"
    private val TEMPLATE_ONE = "1"
    private val TEMPLATE_TWO = "2"
    private val TEMPLATE_THREE = "3"
    private val TEMPLATE_FOUR = "4"
    private val TEMPLATE_FIVE = "5"
    private val TEMPLATE_SIX = "6"
    private val TEMPLATE_SEVEN = "7"
    private val TEMPLATE_NINE = "9"
    private val TEMPLATE_TEN = "10"

    private var objectTypeName = arrayOf("生意概况", "报表", "工具箱", "推送通知")
    private var mClickTemplateName = ""

    /**
     * 图表点击事件统一处理方法
     */
    fun pageLink(context: Context, items: DashboardItem?) {
        if (items != null) {
            val link = items.obj_link
            val objTitle = items.obj_title
            val objectId = items.obj_id
            val templateId = items.template_id
            val objectType = items.objectType
            val paramsMappingBean = items.paramsMappingBean ?: HashMap()

            PageLinkManage.pageLink(context, objTitle!!, link!!, objectId!!, templateId!!, objectType!!, paramsMappingBean)
        } else {
            ToastUtils.show(context, "没有指定链接")
        }
    }

    fun pageLink(context: Context, pushMsg: PushMsgBean) {
        val paramsMappingBean = com.alibaba.fastjson.JSONObject.parseObject(pushMsg.params_mapping, object : TypeReference<java.util.HashMap<String, String>>() {
        })
        var templateId = ""
        if (pushMsg.template_id == null || "" == pushMsg.template_id) {
            val temp = pushMsg.url.split("/")
            for (i in 0..temp.size) {
                if ("template" == temp[i] && i + 1 < temp.size) {
                    templateId = temp[i + 1]
                    break
                }
            }
        } else {
            templateId = pushMsg.template_id
        }
        if (templateId == "") {
            return
        }
        val userSP = context.getSharedPreferences(USER_BEAN, Context.MODE_PRIVATE)
        val userNum = userSP.getString(USER_NUM, "")

        if (userNum != "") {
            pageLink(context, pushMsg.title, pushMsg.url, pushMsg.obj_id, templateId, "4", paramsMappingBean, true)
        } else {
            ToastUtils.show(context, "请先登录")
        }

    }

    /**
     * 页面跳转事件
     */
    fun pageLink(context: Context, objTitle: String, link: String) {
        pageLink(context, objTitle, link, "-1", "-1", "-1")
    }

    fun pageLink(context: Context, objTitle: String, link: String, objectId: String, templateId: String, objectType: String) {
        pageLink(context, objTitle, link, objectId, templateId, objectType, HashMap())
    }

    fun pageLink(context: Context, objTitle: String, link: String, objectId: String, templateId: String, objectType: String, paramsMappingBean: HashMap<String, String>) {
        pageLink(context, objTitle, link, objectId, templateId, objectType, paramsMappingBean, false)
    }

    fun pageLink(context: Context, objTitle: String, link: String, objectId: String, templateId: String, objectType: String, paramsMappingBean: HashMap<String, String>, fromPushMsg: Boolean) {
        try {
            val userSP = context.getSharedPreferences(USER_BEAN, Context.MODE_PRIVATE)
            val groupID = userSP.getString(GROUP_ID, "0")
            userSP.edit().putString(TIME_STAMP, "" + System.currentTimeMillis()).commit()
            //更新本地定位信息
            MapUtil.getInstance(context).updateSPLocation()

            var urlString: String
            val intent: Intent

            when (templateId) {
                TEMPLATE_TWO -> {
//                TEMPLATE_SEVEN -> {
                    mClickTemplateName = "模板七"
                    intent = Intent(context, MyConcernActivity::class.java)
                    intent.flags = if (fromPushMsg) {
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    } else {
                        savePageLink(context, objTitle, link, objectId, templateId, objectType)
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    intent.putExtra(GROUP_ID, groupID)
                    intent.putExtra(TEMPLATE_ID, templateId)
                    intent.putExtra(BANNER_NAME, objTitle)
                    intent.putExtra(LINK, link)
                    intent.putExtra(OBJECT_ID, objectId)
                    intent.putExtra(OBJECT_TYPE, objectType)
                    context.startActivity(intent)
                }
                TEMPLATE_ONE -> {
                    mClickTemplateName = "模板一"
                    savePageLink(context, objTitle, link, objectId, templateId, objectType)
                    intent = Intent(context, NativeReportActivity::class.java)
                    intent.flags = if (fromPushMsg) {
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    } else {
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    intent.putExtra(GROUP_ID, groupID)
                    intent.putExtra(TEMPLATE_ID, templateId)
                    intent.putExtra(BANNER_NAME, objTitle)
                    intent.putExtra(LINK, link)
                    intent.putExtra(OBJECT_ID, objectId)
                    intent.putExtra(OBJECT_TYPE, objectType)
                    context.startActivity(intent)
                }
                TEMPLATE_TEN -> {
                    mClickTemplateName = "模板十"
                    savePageLink(context, objTitle, link, objectId, templateId, objectType)
                    intent = Intent(context, NativeReportActivity::class.java)
                    intent.flags = if (fromPushMsg) {
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    } else {
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    intent.putExtra(GROUP_ID, groupID)
                    intent.putExtra(TEMPLATE_ID, templateId)
                    intent.putExtra(BANNER_NAME, objTitle)
                    intent.putExtra(LINK, link)
                    intent.putExtra(OBJECT_ID, objectId)
                    intent.putExtra(OBJECT_TYPE, objectType)
                    context.startActivity(intent)
                }
                TEMPLATE_TWO -> {
                    mClickTemplateName = "模板二"
                    savePageLink(context, objTitle, link, objectId, templateId, objectType)
                    intent = Intent(context, WebPageActivity::class.java)
                    if (fromPushMsg) {
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    intent.putExtra(GROUP_ID, groupID)
                    intent.putExtra(TEMPLATE_ID, templateId)
                    intent.putExtra(BANNER_NAME, objTitle)
                    intent.putExtra(LINK, link)
                    intent.putExtra(OBJECT_ID, objectId)
                    intent.putExtra(OBJECT_TYPE, objectType)
                    context.startActivity(intent)
                }
                TEMPLATE_FOUR -> {
                    mClickTemplateName = "模板四"
                    intent = Intent(context, WebPageActivity::class.java)
                    if (fromPushMsg) {
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    } else {
                        savePageLink(context, objTitle, link, objectId, templateId, objectType)
                    }
                    intent.putExtra(GROUP_ID, groupID)
                    intent.putExtra(TEMPLATE_ID, templateId)
                    intent.putExtra(BANNER_NAME, objTitle)
                    intent.putExtra(LINK, link)
                    intent.putExtra(OBJECT_ID, objectId)
                    intent.putExtra(OBJECT_TYPE, objectType)
                    context.startActivity(intent)
                }
                TEMPLATE_THREE -> {
                    mClickTemplateName = "模板三"
                    intent = Intent(context, MultiIndexActivity::class.java)
                    intent.flags = if (fromPushMsg) {
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    } else {
                        savePageLink(context, objTitle, link, objectId, templateId, objectType)
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    intent.putExtra(GROUP_ID, groupID)
                    intent.putExtra(TEMPLATE_ID, templateId)
                    intent.putExtra(BANNER_NAME, objTitle)
                    intent.putExtra(LINK, link)
                    intent.putExtra(OBJECT_ID, objectId)
                    intent.putExtra(OBJECT_TYPE, objectType)
                    context.startActivity(intent)
                }
                TEMPLATE_NINE -> {
                    mClickTemplateName = "模板九"
                    intent = Intent(context, CollectionActivity::class.java)
                    intent.flags = if (fromPushMsg) {
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    } else {
                        savePageLink(context, objTitle, link, objectId, templateId, objectType)
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    intent.putExtra(GROUP_ID, groupID)
                    intent.putExtra(TEMPLATE_ID, templateId)
                    intent.putExtra(BANNER_NAME, objTitle)
                    intent.putExtra(LINK, link)
                    intent.putExtra(OBJECT_ID, objectId)
                    intent.putExtra(OBJECT_TYPE, objectType)
                    context.startActivity(intent)
                }
                EXTERNAL_LINK, TEMPLATE_SIX -> {
                    mClickTemplateName = "外部链接"
                    urlString = link
                    for ((key, value) in paramsMappingBean) {
                        urlString = splitUrl(userSP, urlString, key, value)
                    }
                    savePageLink(context, objTitle, urlString, objectId, templateId, objectType)
                    intent = Intent(context, WebPageActivity::class.java)
                    if (fromPushMsg) {
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    intent.putExtra(GROUP_ID, groupID)
                    intent.putExtra(TEMPLATE_ID, templateId)
                    intent.putExtra(BANNER_NAME, objTitle)
                    intent.putExtra(LINK, urlString)
                    intent.putExtra(OBJECT_ID, objectId)
                    intent.putExtra(OBJECT_TYPE, objectType)
                    context.startActivity(intent)
                }
                SCANNER -> {
                    mClickTemplateName = "扫一扫"
                    urlString = link
                    for ((key, value) in paramsMappingBean) {
                        urlString = splitUrl(userSP, urlString, key, value)
                    }
                    savePageLink(context, objTitle, urlString, objectId, templateId, objectType)
                    intent = Intent(context, BarCodeScannerActivity::class.java)
                    intent.flags = if (fromPushMsg) {
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    } else {
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    intent.putExtra(BarCodeScannerActivity.INTENT_FOR_RESULT, false)
                    intent.putExtra(LINK, urlString)
                    context.startActivity(intent)
                }
                PUSH_MESSAGE_LIST -> {
                    mClickTemplateName = "消息列表"
                    intent = Intent(context, ShowPushMessageActivity::class.java)
                    intent.flags = if (fromPushMsg) {
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    } else {
                        savePageLink(context, objTitle, link, objectId, templateId, objectType)
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    context.startActivity(intent)
                }
                FEEDBACK -> {
                    mClickTemplateName = "问题反馈"
                    intent = Intent(context, FeedbackActivity::class.java)
                    intent.flags = if (fromPushMsg) {
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    } else {
                        savePageLink(context, objTitle, link, objectId, templateId, objectType)
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    context.startActivity(intent)
                }

                else -> showTemplateErrorDialog(context)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val logParams = JSONObject()
        if ("-1" == templateId && "-1" != objectType) {
            logParams.put(ACTION, "点击/" + objectTypeName[objectType.toInt() - 1] + "/" + mClickTemplateName)
        } else if ("-1" != objectType) {
            logParams.put(ACTION, "点击/" + objectTypeName[objectType.toInt() - 1] + "/" + mClickTemplateName)
        }

        logParams.put(OBJECT_TITLE, objTitle)
        logParams.put(OBJECT_ID, objectId)
        logParams.put(LINK, link)
        logParams.put(OBJECT_TYPE, objectType)
        ActionLogUtil.actionLog(logParams)
    }

    private fun showTemplateErrorDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("温馨提示")
                .setMessage("当前版本暂不支持该模板, 请升级应用后查看")
                .setPositiveButton("前去升级") { _, _ ->
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.BASE_URL))
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
