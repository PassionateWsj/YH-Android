package com.intfocus.hxtest.business.dashboard.app

import android.support.v4.widget.SwipeRefreshLayout
import com.intfocus.hxtest.business.dashboard.app.mode.AppListMode
import com.intfocus.hxtest.general.base.BaseModeFragment

/**
 * 主页 - 专题
 * Created by liuruilin on 2017/6/15.
 */
class AppFragment : BaseModeFragment<AppListMode>(), SwipeRefreshLayout.OnRefreshListener {
    override fun onRefresh() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
//    var rootView: View? = null
//    var datas: List<CategoryBean>? = null
//
//    override fun setSubject(): Subject = AppListMode(ctx, "app")
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        EventBus.getDefault().register(this)
//        if (rootView == null) {
//            rootView = inflater!!.inflate(R.layout.fragment_app, container, false)
//            model.requestData()
//        }
//        return rootView
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        initSwipeLayout()
//        super.onActivityCreated(savedInstanceState)
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        EventBus.getDefault().unregister(this)
//    }
//
//    private fun initSwipeLayout() {
//        swipe_container.setOnRefreshListener(this)
//        swipe_container.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
//                android.R.color.holo_orange_light, android.R.color.holo_red_light)
//        swipe_container.setDistanceToTriggerSync(300)// 设置手指在屏幕下拉多少距离会触发下拉刷新
//        swipe_container.setSize(SwipeRefreshLayout.DEFAULT)
//    }
//
//    override fun onRefresh() {
//        if (HttpUtil.isConnected(context)) {
//            model.requestData()
//        } else {
//            swipe_container.isRefreshing = false
//            ToastUtils.show(context!!, "请检查网络")
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun initView(requestReport: AppListPageRequest) {
//        if (requestReport.isSuccess) {
//            datas = requestReport.categroy_list
//            val mLayoutManager = LinearLayoutManager(ctx)
//            mLayoutManager.orientation = LinearLayoutManager.VERTICAL
//            rv_app_list.layoutManager = mLayoutManager
//            rv_app_list.adapter = AppListAdapter(ctx, datas!![0].data)
//        }
//        swipe_container.isRefreshing = false
//    }
//
//    internal fun showTemplateErrorDialog() {
//        val builder = AlertDialog.Builder(activity)
//        builder.setTitle("温馨提示")
//                .setMessage("当前版本暂不支持该模板, 请升级应用后查看")
//                .setPositiveButton("前去升级") { _, _ ->
//                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(K.kPgyerUrl))
//                    startActivity(browserIntent)
//                }
//                .setNegativeButton("稍后升级") { _, _ ->
//                    // 返回 LoginActivity
//                }
//        builder.show()
//    }
}
