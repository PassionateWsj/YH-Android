package com.intfocus.template.dashboard.feedback.list

import com.intfocus.template.dashboard.feedback.FeedbackModelImpl
import com.intfocus.template.model.callback.LoadDataCallback
import com.intfocus.template.model.response.mine_page.FeedbackList

/**
 * @author liuruilin
 * @data 2017/12/5
 * @describe
 */
class FeedbackListPresenter : FeedbackListContract.Presenter {
    private var mModel: FeedbackModelImpl
    private var mView: FeedbackListContract.View

    constructor(model: FeedbackModelImpl, view: FeedbackListContract.View) {
        this.mView = view
        this.mModel = model
    }

    override fun start() {}

    override fun getList() {
        mModel.getList(callback = object: LoadDataCallback<FeedbackList> {
            override fun onSuccess(data: FeedbackList) {
                mView.showList(data)
            }

            override fun onError(e: Throwable) {
                mView.showNullPage()
            }

            override fun onComplete() {
            }
        })
    }
}
