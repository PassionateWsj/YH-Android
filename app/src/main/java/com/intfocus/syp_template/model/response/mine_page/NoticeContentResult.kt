package com.intfocus.syp_template.model.response.mine_page

import com.google.gson.annotations.SerializedName
import com.intfocus.syp_template.dashboard.mine.bean.NoticeContentBean
import com.intfocus.syp_template.model.response.BaseResult

/**
 * Created by liuruilin on 2017/8/13.
 */
class NoticeContentResult : BaseResult() {
    @SerializedName("data")
    var data: NoticeContentBean? = null
}
