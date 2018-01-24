package com.intfocus.template.dashboard.feedback

import com.intfocus.template.model.callback.LoadDataCallback
import com.intfocus.template.model.response.mine_page.FeedbackContent
import com.intfocus.template.model.response.mine_page.FeedbackList

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
