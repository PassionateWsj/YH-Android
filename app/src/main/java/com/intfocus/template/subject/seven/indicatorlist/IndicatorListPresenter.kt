package com.intfocus.template.subject.seven.indicatorlist

import com.intfocus.template.subject.seven.bean.ConcernGroupBean
import com.intfocus.template.subject.seven.bean.ConcernItemsBean

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/26 下午1:47
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class IndicatorListPresenter(
        private val mModel: IndicatorListModel,
        private val mView: IndicatorListContract.View
) : IndicatorListContract.Presenter {
    init {
        mView.presenter = this
    }

    override fun start() {

    }

    override fun loadConcernedList(id: String, rep: String) {
        mModel.getConcernedListByUser(id, rep, object : IndicatorListModel.OnConcernedListResult {
            override fun onLoadListDataSuccess(data: List<ConcernItemsBean.ConcernItem>) {
                mView.showConcernedListData(data)
            }

            override fun onLoadItemListDataSuccess(data: List<List<ConcernGroupBean.ConcernGroup>>) {
            }

            override fun onLoadDataFailure(e: Throwable) {
            }
        })
    }

    override fun loadConcernedListData(filterId: String, concernedId: Array<String>) {
    }

    override fun updateConcernedList() {
    }
}