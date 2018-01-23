package com.intfocus.template.subject.one

import android.content.Context
import com.intfocus.template.model.response.home.WorkBoxResult

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/25 下午5:08
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class WorkBoxPresenter(
        private val mModel: WorkBoxImpl,
        private val mView: WorkBoxContract.View
) : WorkBoxContract.Presenter {

    init {
        mView.presenter = this
    }

    override fun start() {
    }

    override fun loadData(ctx: Context) {
        mModel.getData(ctx, object : WorkBoxModel.LoadDataCallback {
            override fun onDataLoaded(data: WorkBoxResult) {
                mView.dataLoaded(data)
            }

            override fun onDataNotAvailable(e: Throwable) {
                mView.loadDataFailure()
            }
        })
    }
}
