package com.intfocus.syp_template.general.mode

import android.content.Context
import com.google.gson.Gson
import com.intfocus.syp_template.business.dashboard.mine.bean.NoticeListBean
import com.intfocus.syp_template.general.util.K
import com.intfocus.syp_template.general.util.URLs
import com.zbl.lib.baseframe.core.AbstractMode

/**
 * Created by liuruilin on 2017/6/12.
 */
class NoticeMode(var ctx: Context) : AbstractMode() {
//    lateinit var urlString: String
//    var result: String? = null
//    var mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
//    var mNoticeListBean: NoticeListBean? = null
//    var page = 1
//    var gson = Gson()
//    var typeStr: String? = null
//    var errorMsg: String? = "未知异常"
//
//    fun getUrl(): String {
//        val url = String.format(K.K_NOTICE_LIST_PATH, K.kBaseUrl,
//                mUserSP.getString(URLs.kUserNum, ""), typeStr, page, 10.toString())
//        return url
//    }
//
//    fun requestData(page: Int, typeStr: String) {
//        this.page = page
//        this.typeStr = typeStr
//        requestData()
//    }

    override fun requestData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
//    lateinit var urlString: String
//    var result: String? = null
//    var mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
//    var mNoticeListBean: NoticeListBean? = null
//    var page = 1
//    var gson = Gson()
//    var typeStr: String? = null
//    var errorMsg: String? = "未知异常"
//
//    fun getUrl(): String {
//        var url = String.format(K.K_NOTICE_LIST_PATH, K.kBaseUrl,
//                mUserSP.getString(URLs.kUserNum, ""), typeStr, page, 10.toString())
//        return url
//    }
//
//    fun requestData(page: Int, typeStr: String) {
//        this.page = page
//        this.typeStr = typeStr
//        requestData()
//    }
//
//    override fun requestData() {
//        Thread(Runnable {
//            urlString = getUrl()
//            if (!urlString.isEmpty()) {
//                val response = HttpUtil.httpGet(ctx, urlString, HashMap<String, String>())
//                result = response["body"]
//                if (StringUtil.isEmpty(result)) {
//                    val result1 = NoticeListRquest(true, 400, "数据为空")
//                    result1.noticeListBean = mNoticeListBean
//                    EventBus.getDefault().post(result1)
//                    return@Runnable
//                }
//                analysisData(result)
//            } else {
//                val result1 = NoticeListRquest(true, 400, "请求链接为空")
//                result1.noticeListBean = mNoticeListBean
//                EventBus.getDefault().post(result1)
//                return@Runnable
//            }
//        }).start()
//    }
//
//    /**
//     * 解析数据
//     * @param result
//     */
//    private fun analysisData(result: String?): NoticeListRquest {
//        try {
//            val jsonObject = JSONObject(result)
//            if (jsonObject.has("code")) {
//                val code = jsonObject.getInt("code")
//                if (code != 200) {
//                    if (jsonObject.has("message")) {
//                        errorMsg = jsonObject.getString("message")
//                    }
//                    val result1 = NoticeListRquest(true, code, errorMsg!!)
//                    result1.noticeListBean = mNoticeListBean
//                    EventBus.getDefault().post(result1)
//                    return result1
//                }
//            } else {
//                val result1 = NoticeListRquest(true, 404, errorMsg!!)
//                result1.noticeListBean = mNoticeListBean
//                EventBus.getDefault().post(result1)
//                return result1
//            }
//
//            var mNoticeList = gson.fromJson(jsonObject.toString(), NoticeListBean::class.java)
//            val result1 = NoticeListRquest(true, 200, errorMsg!!)
//            result1.noticeListBean = mNoticeList
//            EventBus.getDefault().post(result1)
//            return result1
//        } catch (e: JSONException) {
//            e.printStackTrace()
//            val result1 = NoticeListRquest(true, -1, "解析出错")
//            result1.noticeListBean = mNoticeListBean
//            EventBus.getDefault().post(result1)
//        }
//
//        val result1 = NoticeListRquest(true, 0, errorMsg!!)
//        result1.noticeListBean = mNoticeListBean
//        EventBus.getDefault().post(result1)
//        return result1
//    }
}
