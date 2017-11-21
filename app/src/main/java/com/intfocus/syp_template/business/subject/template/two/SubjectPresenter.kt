package com.intfocus.syp_template.business.subject.template.two

import com.intfocus.syp_template.collection.callback.LoadDataCallback

/**
 * @author liuruilin
 * @data 2017/11/15
 * @describe
 */
class SubjectPresenter(
        var mModel: SubjectModelImpl,
        var mView: SubjectContract.View
) : SubjectContract.Presenter {

    init {
        mView.presenter = this
    }

    override fun start() {
    }

    override fun load(reportId: String, templateId: String, groupId: String, url: String) {
        when (templateId) {
            "2", "4" -> {
                mModel.getReportData(reportId, templateId, groupId, LoadDataCallBack())
            }
            else -> {
                if (url.toLowerCase().endsWith(".pdf")) {
                    mModel.getPdfFilePath(url, LoadDataCallBack())
                }
                else {
                    mView.show(url)
                }
            }
        }
    }

    inner class LoadDataCallBack: LoadDataCallback<String> {
        override fun onSuccess(path: String) {
            mView.show(path)
        }

        override fun onError(e: Throwable) {

        }

        override fun onComplete() {}
    }
}

