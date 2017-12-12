package com.intfocus.shengyiplus.subject.two

import com.intfocus.shengyiplus.SYPApplication
import com.intfocus.shengyiplus.model.callback.LoadDataCallback
import com.intfocus.shengyiplus.util.FileUtil

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
                    mView.show(url)
                }
            }
        }
    }

    inner class LoadDataCallBack : LoadDataCallback<String> {
        override fun onSuccess(path: String) {
            mView.show(path)
        }

        override fun onError(e: Throwable) {
            var errorPagePath = FileUtil.sharedPath(SYPApplication.globalContext) + "/loading/400.html"
            mView.showError("file://" + errorPagePath)
        }

        override fun onComplete() {}
    }
}

