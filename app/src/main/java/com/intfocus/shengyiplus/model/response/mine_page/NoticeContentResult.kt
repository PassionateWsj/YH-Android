package com.intfocus.shengyiplus.model.response.mine_page

import com.google.gson.annotations.SerializedName
import com.intfocus.shengyiplus.dashboard.mine.bean.NoticeContentBean
import com.intfocus.shengyiplus.model.response.BaseResult

/**
 * Created by liuruilin on 2017/8/13.
 */
class NoticeContentResult : BaseResult() {
    @SerializedName("data")
    var data: NoticeContentBean? = null
}
