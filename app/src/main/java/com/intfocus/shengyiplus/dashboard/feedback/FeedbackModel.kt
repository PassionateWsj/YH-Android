package com.intfocus.shengyiplus.dashboard.feedback

import com.intfocus.shengyiplus.model.callback.LoadDataCallback
import com.intfocus.shengyiplus.model.response.mine_page.FeedbackContent
import com.intfocus.shengyiplus.model.response.mine_page.FeedbackList

/**
 * @author liuruilin
 * @data 2017/12/5
 * @describe
 */
interface FeedbackModel {
    fun getList(callback: LoadDataCallback<FeedbackList>)
    fun getContent(id: Int, callback: LoadDataCallback<FeedbackContent>)
    fun submit()
}
