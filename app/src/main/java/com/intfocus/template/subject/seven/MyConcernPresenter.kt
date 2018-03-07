package com.intfocus.template.subject.seven

import android.content.Context
import com.intfocus.template.model.callback.LoadDataCallback
import com.intfocus.template.model.entity.Report
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

    override fun loadData(userNum: String) {
        loadData(userNum, "")
    }

    override fun loadFilterData() {
        mModel.getFilterData(object : LoadDataCallback<Filter> {
            override fun onSuccess(data: Filter) {
                mView.initFilterView(data)
                data.default_id?.let { loadData("", it) }
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
            }

        })
    }

    override fun loadData(uuid: String, obj_num: String) {
        mModel.getData(uuid, obj_num, object : LoadDataCallback<List<ConcernComponentBean.ConcernComponent>> {
            override fun onSuccess(data: List<ConcernComponentBean.ConcernComponent>) {
                mView.generateReportItemViews(data)
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
            }

        })
    }

    override fun loadData(ctx: Context, groupId: String, templateId: String, reportId: String) {
        mModel.getData(ctx, groupId, templateId, reportId, object : MyConcernModel.LoadReportsDataCallback {
            override fun onReportsDataLoaded(reports: List<Report>) {
            }

            override fun onFilterDataLoaded(filter: Filter) {
                mView.initFilterView(filter)
            }

            override fun onDataNotAvailable(e: Throwable) {

            }
        })
    }
}