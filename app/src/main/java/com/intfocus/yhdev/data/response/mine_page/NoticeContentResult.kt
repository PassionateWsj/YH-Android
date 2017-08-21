package com.intfocus.yhdev.data.response.mine_page

import com.google.gson.annotations.SerializedName
import com.intfocus.yhdev.dashboard.mine.bean.NoticeContentBean
import com.intfocus.yhdev.data.response.BaseResult

/**
 * Created by liuruilin on 2017/8/13.
 */
class NoticeContentResult: BaseResult() {
    @SerializedName("data")
    var data: NoticeContentBean? = null
}
