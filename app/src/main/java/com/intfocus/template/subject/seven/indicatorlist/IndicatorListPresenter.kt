package com.intfocus.template.subject.seven.indicatorlist

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

    override fun loadConcernedList() {
        mModel.getConcernedListByUser()
    }

    override fun loadConcernedListData(filterId: String, concernedId: Array<String>) {
    }

    override fun updateConcernedList() {
    }
}