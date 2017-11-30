package com.intfocus.template.dashboard.report

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.intfocus.template.ConfigConstants
import com.intfocus.template.R
import com.intfocus.template.dashboard.report.adapter.ReportsLeftListAdapter
import com.intfocus.template.dashboard.report.adapter.ReportsRightRVAdapter
import com.intfocus.template.dashboard.report.mode.CategoryBean
import com.intfocus.template.model.response.home.ReportListResult
import com.intfocus.template.subject.one.ReportContract
import com.intfocus.template.ui.BaseFragment
import com.intfocus.template.util.HttpUtil
import com.intfocus.template.util.ToastUtils
import kotlinx.android.synthetic.main.fragment_reports.*

/**
 * Created by liuruilin on 2017/6/15.
 */
class ReportFragment : BaseFragment(), ReportContract.View, ReportsLeftListAdapter.ReportLeftListListener, SwipeRefreshLayout.OnRefreshListener {
    var rootView: View? = null
    var datas: List<CategoryBean>? = null
    lateinit var reportsRightAdapter: ReportsRightRVAdapter
    lateinit var reportsLeftAdapter: ReportsLeftListAdapter

    override lateinit var presenter: ReportContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_reports, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.loadData(ctx)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initSwipeLayout()
        initShow()
        super.onActivityCreated(savedInstanceState)
    }

    private fun initShow() {
        bannerSetting.visibility = if (ConfigConstants.SCAN_ENABLE_REPORT) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun initSwipeLayout() {
        swipe_container.setOnRefreshListener(this)
        swipe_container.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light)
        swipe_container.setDistanceToTriggerSync(300)// 设置手指在屏幕下拉多少距离会触发下拉刷新
        swipe_container.setSize(SwipeRefreshLayout.DEFAULT)
    }

    override fun onRefresh() {
        if (HttpUtil.isConnected(context)) {
            presenter.loadData(ctx)
        } else {
            swipe_container.isRefreshing = false
            ToastUtils.show(context!!, "请检查网络")
        }
    }

    override fun dataLoaded(data: ReportListResult) {
        datas = data.data
        reportsLeftAdapter = ReportsLeftListAdapter(ctx, datas, this)
        ll_reports_category_list.adapter = reportsLeftAdapter
        val mLayoutManager = LinearLayoutManager(ctx)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_reports_group_list.layoutManager = mLayoutManager
        reportsRightAdapter = ReportsRightRVAdapter(ctx, datas!![0].data)
        rv_reports_group_list.adapter = reportsRightAdapter
        swipe_container.isRefreshing = false
    }

    override fun reportLeftItemClick(sign: ImageView, position: Int) {
        reportsRightAdapter.setData(datas!![position].data)
        reportsLeftAdapter.refreshListItemState(position)
    }
}
