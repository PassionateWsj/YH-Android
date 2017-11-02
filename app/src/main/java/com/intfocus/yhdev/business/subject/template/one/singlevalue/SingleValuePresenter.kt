package com.intfocus.yhdev.business.subject.templateone.singlevalue

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
            override fun onDataLoaded(data: com.intfocus.yhdev.business.subject.template.one.entity.MDRPUnitSingleValue) {
                mView.showData(data)
            }

            override fun onDataNotAvailable(e: Throwable?) {

            }

        })
    }

}