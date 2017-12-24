package com.intfocus.template.subject.seven.attention

import com.intfocus.template.model.response.attention.AttentionItem

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 下午0:45
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class AttentionPresenter(
        private val mModel: AttentionModelImpl,
        private val mView: AttentionContract.View
) : AttentionContract.Presenter {

    init {
        mView.presenter = this
    }

    override fun start() {

    }

    override fun loadData(concerned: Boolean) {
        loadData("", concerned)
    }

    override fun loadData(keyWord: String, concerned: Boolean) {
        mModel.getData(keyWord, concerned, object : AttentionModel.LoadDataCallback {
            override fun onDataLoaded(dataList: List<AttentionItem>) {
                mView.onResultSuccess(dataList)
            }

            override fun onDataNotAvailable(e: Throwable) {
                mView.onResultFailure(e)
            }
        })
    }

    override fun concernOrCancelConcern(attentionItemId: String, attentionItemName: String) {
        mModel.concernOrCancelConcern(attentionItemId, attentionItemName, object : AttentionModel.ConcernCallback {
            override fun onConcernResult(isConcernSuccess: Boolean) {
                mView.concernOrCancelConcernResult(isConcernSuccess)
            }
        })
    }

}