package com.intfocus.hx.general.data.response.mine_page

import com.google.gson.annotations.SerializedName
import com.intfocus.hx.business.dashboard.mine.bean.NoticeContentBean
import com.intfocus.hx.general.data.response.BaseResult

/**
 * Created by liuruilin on 2017/8/13.
 */
class NoticeContentResult : BaseResult() {
    @SerializedName("data")
    var data: NoticeContentBean? = null
}
