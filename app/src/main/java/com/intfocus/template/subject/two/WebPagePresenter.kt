package com.intfocus.template.subject.two

import com.intfocus.template.SYPApplication
import com.intfocus.template.model.callback.LoadDataCallback
import com.intfocus.template.util.FileUtil

/**
 * @author liuruilin
 * @data 2017/11/15
 * @describe
 */
class WebPagePresenter(
        var mModel: WebPageModelImpl,
        var mView: WebPageContract.View
) : WebPageContract.Presenter {

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
                } else {
                    mView.show(url,false)
                }
            }
        }
    }

    inner class LoadDataCallBack : LoadDataCallback<String> {
        override fun onSuccess(path: String) {
            mView.show(path,false)
        }

        override fun onError(e: Throwable) {
            val errorPagePath = FileUtil.sharedPath(SYPApplication.globalContext) + "/loading/400.html"
            mView.showError("file://" + errorPagePath)
        }

        override fun onComplete() {}
    }
}

