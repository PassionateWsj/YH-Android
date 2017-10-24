package com.intfocus.yhdev.subject.template_v1.singlevalue

import com.intfocus.yhdev.subject.template_v1.entity.MDRPUnitSingleValue

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

    override fun loadData(mParam: String) {
        mModel.getData(mParam, object : SingleValueModel.LoadDataCallback {
            override fun onDataLoaded(data: MDRPUnitSingleValue) {
                mView.showData(data)
            }

            override fun onDataNotAvailable() {

            }

        })
    }

}
