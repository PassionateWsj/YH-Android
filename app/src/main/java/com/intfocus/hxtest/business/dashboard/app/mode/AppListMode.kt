package com.intfocus.hxtest.business.dashboard.app.mode

import android.content.Context
import com.zbl.lib.baseframe.core.AbstractMode

/**
 * 主页 - 专题 Model
 * Created by liuruilin on 2017/6/15.
 */
class AppListMode(var ctx: Context, var type: String?) : AbstractMode() {
    override fun requestData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
//    lateinit var urlString: String
//    var result: String? = null
//    val mListPageSP: SharedPreferences = ctx.getSharedPreferences("ListPage", Context.MODE_PRIVATE)
//    var mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
//    var gson = Gson()
//
//    fun getUrl(): String {
//        val url = String.format(K.K_APP_LIST_PATH, K.kBaseUrl,
//                mUserSP.getString(kGroupId, "0"), mUserSP.getString(kRoleId, "0"))
//        return url
//    }
//
//    override fun requestData() {
//        Thread(Runnable {
//            urlString = getUrl()
//            if (!urlString.isEmpty()) {
//                val response = HttpUtil.httpGet(ctx, urlString, HashMap<String, String>())
//                result = response["body"]
//                if (StringUtil.isEmpty(result)) {
//                    val result1 = AppListPageRequest(false, 400)
//                    EventBus.getDefault().post(result1)
//                    return@Runnable
//                }
//                analysisData(result)
//            } else {
//                val result1 = AppListPageRequest(false, 400)
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
//    private fun analysisData(result: String?): AppListPageRequest {
//        try {
//            val jsonObject = JSONObject(result)
//            if (jsonObject.has("code")) {
//                val code = jsonObject.getInt("code")
//                if (code != 200) {
//                    val result1 = AppListPageRequest(false, code)
//                    EventBus.getDefault().post(result1)
//                    return result1
//                }
//            }
//
//            if (jsonObject.has("data")) {
//                val resultStr = jsonObject.toString()
//                mListPageSP.edit().putString(type, resultStr).apply()
//                val listPageData = gson.fromJson(resultStr, ListPageBean::class.java)
//                val result1 = AppListPageRequest(true, 200)
//                result1.categroy_list = listPageData.data
//                EventBus.getDefault().post(result1)
//                return result1
//            }
//        } catch (e: JSONException) {
//            e.printStackTrace()
//            val result1 = AppListPageRequest(false, -1)
//            EventBus.getDefault().post(result1)
//        }
//
//        val result1 = AppListPageRequest(false, 0)
//        EventBus.getDefault().post(result1)
//        return result1
//    }
}
