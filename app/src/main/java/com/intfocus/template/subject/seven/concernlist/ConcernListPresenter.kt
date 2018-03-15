package com.intfocus.template.subject.seven.concernlist

import com.intfocus.template.model.callback.LoadDataCallback
import com.intfocus.template.subject.seven.bean.ConcernListBean

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

    override fun loadData(concerned: Boolean, reportId: String) {
        mModel.updateConcernListData(reportId, object : LoadDataCallback<Boolean> {
            override fun onSuccess(data: Boolean) {
                loadData("", concerned, reportId)
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
            }
        })
    }

    override fun loadData(keyWord: String, concerned: Boolean, reportId: String) {
        mModel.getData(keyWord, concerned, reportId, object : ConcernListModel.LoadDataCallback {
            override fun onDataLoaded(dataList: List<ConcernListBean>) {
                mView.onResultSuccess(dataList)
            }

            override fun onDataNotAvailable(e: Throwable) {
                mView.onResultFailure(e)
            }
        })
    }

    override fun concernOrCancelConcern(attentionItemId: String, attentionItemName: String, reportId: String) {
        mModel.concernOrCancelConcern(attentionItemId, attentionItemName, reportId, object : ConcernListModel.ConcernCallback {
            override fun onConcernResult(isConcernSuccess: Boolean) {
                mView.concernOrCancelConcernResult(isConcernSuccess)
            }
        })
    }

}