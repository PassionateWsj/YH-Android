package com.intfocus.template.subject.seven

import com.intfocus.template.model.response.attention.Test2

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 下午0:45
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class MyAttentionPresenter(
        private val mModel: MyAttentionModelImpl,
        private val mView: MyAttentionContract.View
) : MyAttentionContract.Presenter {

    init {
        mView.presenter = this
    }

    override fun start() {

    }

    override fun loadData(user_num: String) {
        mModel.getData(user_num,object :MyAttentionModel.LoadDataCallback{
            override fun onDataLoaded(data: Test2) {
                mView.onUpdateData(data)
            }

            override fun onDataNotAvailable(e: Throwable) {
            }
        })
    }
}