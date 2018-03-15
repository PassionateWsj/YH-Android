package com.intfocus.template.dashboard.kpi

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.blankj.utilcode.util.BarUtils
import com.intfocus.template.BuildConfig
import com.intfocus.template.ConfigConstants
import com.intfocus.template.R
import com.intfocus.template.constant.Params.GROUP_ID
import com.intfocus.template.constant.Params.ROLE_ID
import com.intfocus.template.constant.Params.USER_BEAN
import com.intfocus.template.constant.Params.USER_NUM
import com.intfocus.template.dashboard.kpi.bean.KpiBean
import com.intfocus.template.dashboard.mine.adapter.KpiAdapter
import com.intfocus.template.dashboard.mine.bean.InstituteDataBean
import com.intfocus.template.general.net.ApiException
import com.intfocus.template.general.net.CodeHandledSubscriber
import com.intfocus.template.general.net.RetrofitUtil
import com.intfocus.template.model.response.home.HomeMsgResult
import com.intfocus.template.model.response.home.KpiResult
import com.intfocus.template.ui.RefreshFragment
import com.intfocus.template.ui.view.CustomLinearLayoutManager
import com.intfocus.template.ui.view.DefaultRefreshView
import com.intfocus.template.util.DisplayUtil
import com.intfocus.template.util.ErrorUtils
import com.intfocus.template.util.ToastUtils
import kotlinx.android.synthetic.main.common_error_view.*
import kotlinx.android.synthetic.main.fragment_kpi.*
import java.util.*

/**
 * Created by CANC on 2017/7/27.
 */
class KpiFragment : RefreshFragment(), KpiAdapter.HomePageListener {
    lateinit var mAdapter: KpiAdapter
    var mKpiData: MutableList<KpiBean>? = null
    lateinit var mUserSP: SharedPreferences
    private lateinit var userId: String
    private lateinit var roleId: String
    private lateinit var groupId: String
    private lateinit var queryMap: MutableMap<String, String>
    lateinit var ctx: Context
    private var hasBanner = false

    override fun onAttach(context: Context) {
        ctx = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_kpi, container, false)
//        x.view().inject(this, mView)
        setRefreshLayout()
        mUserSP = mActivity.getSharedPreferences(USER_BEAN, Context.MODE_PRIVATE)
        userId = mUserSP.getString(USER_NUM, "")
        roleId = mUserSP.getString(ROLE_ID, "0")
        groupId = mUserSP.getString(GROUP_ID, "0")
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        getData(true)
        initShow()
    }

    private fun initShow() {
        bannerSetting.visibility = if (ConfigConstants.SCAN_ENABLE_KPI) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun initView() {
        queryMap = mutableMapOf()
        queryMap.put(GROUP_ID, groupId)
        queryMap.put(ROLE_ID, roleId)
        if (Build.VERSION.SDK_INT >= 21 && ConfigConstants.ENABLE_FULL_SCREEN_UI) {
            fl_title.post {
                val titleTopParams = FrameLayout.LayoutParams(fl_title.layoutParams.width, fl_title.layoutParams.height + BarUtils.getStatusBarHeight())
                fl_title.layoutParams = titleTopParams
            }
            rl_action_bar.post {
                BarUtils.addMarginTopEqualStatusBarHeight(rl_action_bar)
            }
        }
        recycler_view.layoutManager = CustomLinearLayoutManager(context!!)
        mAdapter = KpiAdapter(context!!, mKpiData, this)

        recycler_view.adapter = mAdapter

        val headerView = DefaultRefreshView(mActivity)
        headerView.setArrowResource(R.drawable.loading_up)
        refresh_layout.setHeaderView(headerView)
        refresh_layout.setEnableLoadmore(false)
        //监听
        recycler_view.addOnScrollListener(KpiScrollerListener(context!!, recycler_view, title_top))
    }

    override fun getData(isShowDialog: Boolean) {
//        if (!HttpUtil.isConnected(mActivity)) {
//            ToastUtils.show(mActivity, "请检查网络链接")
//            finishRequest()
//            isEmpty = mKpiData == null || mKpiData!!.size == 0
//            ErrorUtils.viewProcessing(refresh_layout, ll_empty, ll_retry, "无更多文章了", tv_errorMsg, iv_error,
//                    isEmpty!!, false, R.drawable.pic_3, {
//                getData(true)
//            })
//            return
//        }
        if (isShowDialog) {
            if (loadingDialog == null || !loadingDialog!!.isShowing) {
                showLoading()
            }
        }

        RetrofitUtil.getHttpService(context).getHomeIndex(queryMap)
                .compose(RetrofitUtil.CommonOptions<KpiResult>())
                .subscribe(object : CodeHandledSubscriber<KpiResult>() {
                    override fun onCompleted() {
                    }

                    override fun onError(apiException: ApiException) {
                        ToastUtils.show(mActivity, apiException.displayMessage)
                        getHomeMsg()
                    }

                    override fun onBusinessNext(data: KpiResult) {
                        finishRequest()

                        if (mKpiData == null) {
                            mKpiData = ArrayList()
                        }

                        mKpiData!!.clear()
                        val datas = data.data
//                        val result = LoadAssetsJsonUtil.getAssetsJsonData("kpi_test.json")
//                        val datas = JSON.parseObject(result, KpiResult::class.java).data

                        var topBean: KpiBean? = null
                        if (datas != null) {
                            for (kpiResultData in datas) {
                                when (kpiResultData.dashboard_type) {
                                    "number1" -> {
                                        hasBanner = true
                                        topBean = KpiBean()
                                        topBean.group_name = "轮播图"
                                        topBean.index = 0
                                        topBean.data = kpiResultData.data
                                    }
                                    "number2" -> {
                                        val homeBean = KpiBean()
                                        homeBean.group_name = kpiResultData.group_name
                                        homeBean.index = 2
                                        homeBean.data = kpiResultData.data
                                        mKpiData!!.add(homeBean)

                                    }
                                    "number3" -> {
                                        val homeBean = KpiBean()
                                        homeBean.group_name = kpiResultData.group_name
                                        homeBean.index = 3
                                        homeBean.data = kpiResultData.data
                                        mKpiData!!.add(homeBean)
                                    }
                                }
//                                if ("top_data" == kpiResultData.group_name) {
////                                    mKpiData!!.add(homeBean)
//                                }
//
//                                if ("number2" == kpiResultData.data!![0].dashboard_type) {
//                                }
//
//                                if ("number3" == kpiResultData.data!![0].dashboard_type) {
//                                }
                            }
                        }
                        if ("yonghuitest" == BuildConfig.FLAVOR ||
                                "yh_android" == BuildConfig.FLAVOR ||
                                "yhdev" == BuildConfig.FLAVOR ||
                                "shengyiplus" == BuildConfig.FLAVOR) {
                            val homeBean = KpiBean()
                            homeBean.group_name = "滚动文字"
                            homeBean.index = 1
                            mKpiData!!.add(0, homeBean)
                        }

                        val homeBean1 = KpiBean()
                        homeBean1.group_name = "底部信息"
                        homeBean1.index = 4
                        mKpiData!!.add(homeBean1)
                        topBean?.let { mKpiData!!.add(0, it) }
//                        ListUtils.sort(mKpiData, true, "index")
//                        mKpiData.add(0,)
                        getHomeMsg()
                    }
                })
    }

    fun getHomeMsg() {
//        if (!HttpUtil.isConnected(mActivity)) {
//            ToastUtils.show(mActivity, "请检查网络链接")
//            finishRequest()
//            isEmpty = mKpiData == null || mKpiData!!.size == 0
//            ErrorUtils.viewProcessing(refresh_layout, ll_empty, ll_retry, "无更多文章了", tv_errorMsg, iv_error,
//                    isEmpty!!, false, R.drawable.pic_3, {
//                getData(true)
//            })
//            return
//        }
        RetrofitUtil.getHttpService(context).getHomeMsg(queryMap)
                .compose(RetrofitUtil.CommonOptions<HomeMsgResult>())
                .subscribe(object : CodeHandledSubscriber<HomeMsgResult>() {
                    override fun onCompleted() {
                        finishRequest()
                    }

                    override fun onError(apiException: ApiException?) {
                        finishRequest()
                        resetTitleView()
                        ToastUtils.show(mActivity, apiException!!.displayMessage)
                    }

                    override fun onBusinessNext(data: HomeMsgResult?) {
                        if (mKpiData == null) {
                            mKpiData = ArrayList()
                        }

                        mKpiData!!
                                .filter { 1 == it.index }
                                .forEach {
                                    if (data?.data != null && data.data!!.isNotEmpty()) {
                                        it.data = data.data
                                    } else {
                                        mKpiData?.remove(it)
                                    }
                                }
//                        ListUtils.sort(mKpiData, true, "index")
                        mAdapter.setData(mKpiData)

                        resetTitleView()

                        isEmpty = mKpiData == null || mKpiData!!.size == 0
                        ErrorUtils.viewProcessing(refresh_layout, ll_empty, ll_retry, "无更多文章了", tv_errorMsg, iv_error,
                                isEmpty!!, true, R.drawable.pic_3, {
                            getData(true)
                        })
                    }
                })
    }

    private fun resetTitleView() {
        if (hasBanner) {
            rl_action_bar.setBackgroundColor(ContextCompat.getColor(ctx, R.color.transparent))
            tv_banner_title.setTextColor(ContextCompat.getColor(ctx, R.color.co10_syr))
            bannerSetting.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.nav_scan))
        } else {
            val contentPadding = if (Build.VERSION.SDK_INT >= 21 && ConfigConstants.ENABLE_FULL_SCREEN_UI) {
                DisplayUtil.dip2px(context, 48f) + BarUtils.getStatusBarHeight()
            } else {
                DisplayUtil.dip2px(context, 48f)
            }
            ll_kpi_content.setPadding(0, contentPadding, 0, 0)
            rl_action_bar.setBackgroundColor(ContextCompat.getColor(ctx, R.color.co10_syr))
            tv_banner_title.setTextColor(ContextCompat.getColor(ctx, R.color.color6))
            bannerSetting.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.nav_scanback))
        }
    }

    fun finishRequest() {
        refresh_layout.finishRefreshing()
        refresh_layout.finishLoadmore()
        dismissLoading()
    }

    override fun itemClick(instituteDataBean: InstituteDataBean) {
    }
}
