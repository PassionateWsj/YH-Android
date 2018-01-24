package com.intfocus.template.subject.seven

import android.content.Context
import com.intfocus.template.model.entity.Report
import com.intfocus.template.subject.one.entity.Filter

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
        private val mModel: MyConcernModel,
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

    override fun loadData(userNum: String, filterId: String) {
//        mModel.getData(userNum, filterId, object : MyConcernModel.LoadDataCallback {
//            override fun onDataLoaded(data: Test2, filter: Filter) {
//                mView.initFilterView(data, filter)
//            }
//
//            override fun onDataNotAvailable(e: Throwable) {
//            }
//        })
    }

    override fun loadData(ctx: Context, groupId: String, templateId: String, reportId: String) {
        mModel.getData(ctx, groupId, templateId, reportId,object : MyConcernModel.LoadReportsDataCallback{
            override fun onReportsDataLoaded(reports: List<Report>) {
                mView.generateReportItemView(reports)
            }

            override fun onFilterDataLoaded(filter: Filter) {
                mView.initFilterView(filter)
            }

            override fun onDataNotAvailable(e: Throwable) {

            }
        })
//        mModel.getData(reportId, templateId, groupId, object : ModeModel.LoadDataCallback {
//            override fun onDataLoaded(reports: List<String>, filter: Filter) {
//                mView.initFilterView(filter)
//                mModel.queryPageData(reportId + templateId + groupId,pageId)
//            }
//
//            override fun onDataNotAvailable(e: Throwable) {
//            }
//        })
    }
}