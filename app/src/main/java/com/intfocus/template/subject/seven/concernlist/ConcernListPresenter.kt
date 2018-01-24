package com.intfocus.template.subject.seven.concernlist

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
class ConcernListPresenter(
        private val mModel: ConcernListModelImpl,
        private val mView: ConcernListContract.View
) : ConcernListContract.Presenter {

    init {
        mView.presenter = this
    }

    override fun start() {

    }

    override fun loadData(concerned: Boolean) {
        loadData("", concerned)
    }

    override fun loadData(keyWord: String, concerned: Boolean) {
        mModel.getData(keyWord, concerned, object : ConcernListModel.LoadDataCallback {
            override fun onDataLoaded(dataList: List<AttentionItem>) {
                mView.onResultSuccess(dataList)
            }

            override fun onDataNotAvailable(e: Throwable) {
                mView.onResultFailure(e)
            }
        })
    }

    override fun concernOrCancelConcern(attentionItemId: String, attentionItemName: String) {
        mModel.concernOrCancelConcern(attentionItemId, attentionItemName, object : ConcernListModel.ConcernCallback {
            override fun onConcernResult(isConcernSuccess: Boolean) {
                mView.concernOrCancelConcernResult(isConcernSuccess)
            }
        })
    }

}