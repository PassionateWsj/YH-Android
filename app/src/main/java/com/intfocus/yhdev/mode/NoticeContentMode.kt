package com.intfocus.yhdev.mode

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.intfocus.yhdev.dashboard.mine.bean.NoticeContentBean
import com.intfocus.yhdev.dashboard.mine.bean.NoticeContentRequest
import com.intfocus.yhdev.data.response.mine_page.NoticeContentResult
import com.intfocus.yhdev.net.ApiException
import com.intfocus.yhdev.net.CodeHandledSubscriber
import com.intfocus.yhdev.net.RetrofitUtil
import com.intfocus.yhdev.util.K
import com.zbl.lib.baseframe.core.AbstractMode
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by liuruilin on 2017/6/15.
 */
class NoticeContentMode(var ctx: Context) : AbstractMode() {
    lateinit var urlString: String
    var result: String? = null
    val mNoticeContentSP: SharedPreferences = ctx.getSharedPreferences("NoticeContent", Context.MODE_PRIVATE)
    var mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
    var gson = Gson()
    var id = ""

    fun getUrl(): String {
        var url = K.kBaseUrl + "/api/v1/user/" + mUserSP.getString(K.kUserId, "0") + "/notice/" + id
        return url
    }

    fun requestData(id: String) {
        this.id = id
        requestData()
    }

    override fun requestData() {
        RetrofitUtil.getHttpService(ctx).getNoticeContent(id, mUserSP.getString(K.kUserId, "0"))
                .compose(RetrofitUtil.CommonOptions<NoticeContentResult>())
                .subscribe(object : CodeHandledSubscriber<NoticeContentResult>() {
                    override fun onError(apiException: ApiException?) {
                        val result1 = NoticeContentRequest(false, -1)
                        EventBus.getDefault().post(result1)
                    }

                    override fun onCompleted() {
                    }

                    override fun onBusinessNext(data: NoticeContentResult?) {
                        val result1 = NoticeContentRequest(true, 200)
                        result1.noticeContent = data!!.data
                        EventBus.getDefault().post(result1)
                    }
                })
    }

    /**
     * 解析数据
     * @param result
     */
    private fun analysisData(result: String?): NoticeContentRequest {
        try {
            val jsonObject = JSONObject(result)
            if (jsonObject.has("code")) {
                val code = jsonObject.getInt("code")
                if (code != 200) {
                    val result1 = NoticeContentRequest(false, code)
                    EventBus.getDefault().post(result1)
                    return result1
                }
            }

            if (jsonObject.has("data")) {
                var resultStr = jsonObject.get("data").toString()
                mNoticeContentSP.edit().putString("NoticeContent", resultStr).commit()
                var noticeContent = gson.fromJson(resultStr, NoticeContentBean::class.java)
                val result1 = NoticeContentRequest(true, 200)
                result1.noticeContent = noticeContent
                EventBus.getDefault().post(result1)
                return result1
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            val result1 = NoticeContentRequest(false, -1)
            EventBus.getDefault().post(result1)
        }

        val result1 = NoticeContentRequest(false, 0)
        EventBus.getDefault().post(result1)
        return result1
    }
}
