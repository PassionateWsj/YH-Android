package com.intfocus.shengyiplus.dashboard.workbox

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.shengyiplus.ConfigConstants
import com.intfocus.shengyiplus.R
import com.intfocus.shengyiplus.model.response.home.WorkBoxResult
import com.intfocus.shengyiplus.subject.one.WorkBoxContract
import com.intfocus.shengyiplus.subject.one.WorkBoxImpl
import com.intfocus.shengyiplus.subject.one.WorkBoxPresenter
import com.intfocus.shengyiplus.ui.BaseFragment
import com.intfocus.shengyiplus.util.HttpUtil
import com.intfocus.shengyiplus.util.ToastUtils
import kotlinx.android.synthetic.main.fragment_work_box.*

/**
 * Created by liuruilin on 2017/7/28.
 */
class WorkBoxFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, WorkBoxContract.View {

    var rootView: View? = null
    var datas: List<WorkBoxItem>? = null

    override lateinit var presenter: WorkBoxContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WorkBoxPresenter(WorkBoxImpl.getInstance(), this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_work_box, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.loadData(ctx)
        initSwipeLayout()
        initShow()
    }

    private fun initShow() {
        bannerSetting.visibility = if (ConfigConstants.SCAN_ENABLE_WORKBOX) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun initSwipeLayout() {
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


    override fun dataLoaded(data: WorkBoxResult) {
        datas = data.data
        gv_work_box.adapter = WorkBoxAdapter(ctx, datas)
        swipe_container.isRefreshing = false
    }
}
