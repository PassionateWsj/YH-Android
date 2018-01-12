package com.intfocus.template.dashboard.workbox

import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.template.ConfigConstants
import com.intfocus.template.R
import com.intfocus.template.model.response.home.WorkBoxResult
import com.intfocus.template.subject.one.WorkBoxContract
import com.intfocus.template.subject.one.WorkBoxImpl
import com.intfocus.template.subject.one.WorkBoxPresenter
import com.intfocus.template.ui.BaseFragment
import com.intfocus.template.util.HttpUtil
import com.intfocus.template.util.LogUtil
import com.intfocus.template.util.ToastUtils
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
        initView()
        initShow()
    }

    private fun initView() {
        LogUtil.d(this, "GridView requestFocus : " + gv_work_box.requestFocus())
//        gv_work_box.requestFocus()
        gv_work_box.numColumns = if (ctx.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ConfigConstants.WORK_BOX_NUM_COLUMNS_LAND
        } else {
            ConfigConstants.WORK_BOX_NUM_COLUMNS_PORT
        }
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
//        val dataList = ArrayList<WorkBoxItem>()
//        dataList.addAll(data.data!!)
//        if (BuildConfig.FLAVOR == "template") {
//            val item = WorkBoxItem()
//            item.name = "阿里云数据可视化"
//            item.obj_title = "阿里云数据可视化"
//            item.template_id = "-1"
//            item.obj_link = "https://datav.aliyun.com/share/31ae546cd064046699a979b24607a9d5"
//            dataList.add(item)
//            val item2 = WorkBoxItem()
//            item2.name = "浏览器版本信息"
//            item2.obj_title = "浏览器版本信息"
//            item2.template_id = "-1"
//            item2.obj_link = "https://faisalman.github.io/ua-parser-js/"
//            dataList.add(item2)
//        }
        gv_work_box.adapter = WorkBoxAdapter(ctx, datas)
//        LogUtil.d(this, "gv_work_box hasFocus :" + gv_work_box.hasFocus())
        swipe_container.isRefreshing = false
    }
}
