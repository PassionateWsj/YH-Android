package com.intfocus.syp_template.subject.templateone.singlevalue

import com.intfocus.syp_template.subject.one.entity.SingleValue

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/20 下午4:04
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class SingleValuePresenter(
        private val mModel: SingleValueImpl,
        private val mView: SingleValueContract.View
) : SingleValueContract.Presenter {
    init {
        mView.presenter = this
    }

    override fun start() {

    }

    override fun loadData(rootId: Int, index: Int) {
        mModel.getData(rootId, index, object : SingleValueModel.LoadDataCallback {
            override fun onDataLoaded(data: SingleValue) {
                mView.showData(data)
            }

            override fun onDataNotAvailable(e: Throwable?) {

            }

        })
    }
}
