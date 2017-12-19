package com.intfocus.template.subject.seven

import com.intfocus.template.model.response.attention.Test2
import com.intfocus.template.subject.one.entity.Filter

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
            override fun onDataLoaded(data: Test2,filter: Filter) {
                mView.onUpdateData(data,filter)
            }

            override fun onDataNotAvailable(e: Throwable) {
            }
        })
    }
}