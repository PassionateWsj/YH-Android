package com.intfocus.syp_template.business.subject.template.two

import com.intfucos.yhdev.collection.callback.LoadDataCallback

/**
 * @author liuruilin
 * @data 2017/11/15
 * @describe
 */
class SubjectPresenter(
        var mModel: SubjectModelImpl,
        var mView: SubjectContract.View
): SubjectContract.Presenter {

    init {
        mView.presenter = this
    }

    override fun start() {
    }

    override fun load(reportId: String, templateId: String, groupId: String) {
        when(templateId) {
            "2", "4" -> {
                mModel.checkReportData(reportId, templateId, groupId, object: LoadDataCallback<String>{
                    override fun onSuccess(path: String) {
                        mView.show(path)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {}
                })
            }
            else -> {
                mView.show("")
            }
        }
    }
}
