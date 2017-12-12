package com.intfocus.template.dashboard.feedback.content

import android.view.View
import com.intfocus.template.dashboard.feedback.FeedbackModelImpl
import com.intfocus.template.model.callback.LoadDataCallback
import com.intfocus.template.model.response.mine_page.FeedbackContent

/**
 * @author liuruilin
 * @data 2017/12/6
 * @describe
 */
class FeedbackContentPresenter : FeedbackContentContract.Presenter {
    private var mView: FeedbackContentContract.View? = null
    private var mModel: FeedbackModelImpl? = null

    constructor(model: FeedbackModelImpl, view: FeedbackContentContract.View) {
        this.mView = view
        this.mModel = model
    }

    override fun start() = Unit

    override fun getContent(id: Int) {
        mModel!!.getContent(id, callback = object : LoadDataCallback<FeedbackContent> {
            override fun onSuccess(data: FeedbackContent) {
                mView!!.showContent(data)
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
            }
        })
    }
}
