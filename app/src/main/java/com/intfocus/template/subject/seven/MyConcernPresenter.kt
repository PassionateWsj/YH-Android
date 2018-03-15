package com.intfocus.template.subject.seven

import com.intfocus.template.model.callback.LoadDataCallback
import com.intfocus.template.subject.one.entity.Filter
import com.intfocus.template.subject.seven.bean.ConcernComponentBean

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 下午0:45
 * e-mail: PassionateWsj@outlook.com
 * name: 我的关注页面 - presenter
 * desc:
 * ****************************************************
 */
class MyConcernPresenter(
        private val mModel: MyConcernModel<List<ConcernComponentBean.ConcernComponent>>,
        private val mView: MyConcernContract.View
) : MyConcernContract.Presenter {
    init {
        mView.presenter = this
    }

    override fun start() {

    }

    override fun loadData(reportId: String) {
        loadData(reportId, "")
    }

    override fun loadFilterData(repCode: String, reportId: String) {
        mModel.getFilterData(repCode, reportId, object : LoadDataCallback<Filter> {
            override fun onSuccess(data: Filter) {
                mView.initFilterView(data)
                data.default_id?.let { loadData(reportId, it) }
            }

            override fun onError(e: Throwable) {
                e.message?.let { mView.showErrorMsg(it) }
            }

            override fun onComplete() {
            }

        })
    }

    override fun loadData(reportId: String, objNum: String) {
        mModel.getData(reportId, objNum, object : LoadDataCallback<List<ConcernComponentBean.ConcernComponent>> {
            override fun onSuccess(data: List<ConcernComponentBean.ConcernComponent>) {
                mView.generateReportItemViews(data)
            }

            override fun onError(e: Throwable) {
                e.message?.let { mView.showErrorMsg(it) }
            }

            override fun onComplete() {
            }

        })
    }

}