package com.intfocus.syp_template.business.dashboard.kpi

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.intfocus.syp_template.R
import com.intfocus.syp_template.business.dashboard.kpi.bean.KpiBean
import com.intfocus.syp_template.business.dashboard.mine.adapter.KpiAdapter
import com.intfocus.syp_template.business.dashboard.mine.bean.InstituteDataBean
import com.intfocus.syp_template.general.base.RefreshFragment
import com.intfocus.syp_template.general.constant.ConfigConstants
import com.intfocus.syp_template.general.data.response.home.HomeMsgResult
import com.intfocus.syp_template.general.data.response.home.KpiResult
import com.intfocus.syp_template.general.net.ApiException
import com.intfocus.syp_template.general.net.CodeHandledSubscriber
import com.intfocus.syp_template.general.net.RetrofitUtil
import com.intfocus.syp_template.general.util.*
import com.intfocus.syp_template.general.view.DefaultRefreshView
import com.intfocus.syp_template.general.view.MyLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_kpi.*
import org.xutils.x

/**
 * Created by CANC on 2017/7/27.
 */
class KpiFragment : RefreshFragment(), KpiAdapter.HomePageListener {
    lateinit var titleTop: LinearLayout
    lateinit var mAdapter: KpiAdapter
    var mKpiData: MutableList<KpiBean>? = null
    lateinit var mUserSP: SharedPreferences
    private lateinit var userId: String
    private lateinit var roleId: String
    private lateinit var groupId: String
    private lateinit var queryMap: MutableMap<String, String>
    lateinit var ctx: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_kpi, container, false)
        x.view().inject(this, mView)
        setRefreshLayout()
        mUserSP = mActivity.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        userId = mUserSP.getString(URLs.kUserNum, "")
        roleId = mUserSP.getString(URLs.kRoleId, "0")
        groupId = mUserSP.getString(URLs.kGroupId, "0")
        initView()

        getData(true)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initShow()
    }
    private fun initShow() {
        bannerSetting.visibility =if (ConfigConstants.SCAN_ENABLE_KPI) {
             View.VISIBLE
        } else {
             View.GONE
        }
    }

    fun initView() {
        queryMap = mutableMapOf()
        queryMap.put("group_id", groupId)
        queryMap.put("role_id", roleId)
        titleTop = mView!!.findViewById(R.id.title_top)
        recyclerView.layoutManager = MyLinearLayoutManager(context!!)
        mAdapter = KpiAdapter(context!!, mKpiData, this)

        recyclerView.adapter = mAdapter

        val headerView = DefaultRefreshView(mActivity)
        headerView.setArrowResource(R.drawable.loading_up)
        refreshLayout.setHeaderView(headerView)
        refreshLayout.setEnableLoadmore(false)
        //监听
        recyclerView.addOnScrollListener(KpiScrollerListener(context!!, recyclerView, titleTop))
    }

    override fun getData(isShowDialog: Boolean) {
        if (!HttpUtil.isConnected(mActivity)) {
            ToastUtils.show(mActivity, "请检查网络链接")
            finishRequest()
            isEmpty = mKpiData == null || mKpiData!!.size == 0
            ErrorUtils.viewProcessing(refreshLayout, llError, llRetry, "无更多文章了", tvErrorMsg, ivError,
                    isEmpty!!, false, R.drawable.pic_3, {
                getData(true)
            })
            return
        }
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
                        if (datas != null) {
                            for (KpiResultData in datas) {
                                if ("top_data" == KpiResultData.group_name) {
                                    val homeBean = KpiBean()
                                    homeBean.group_name = "轮播图"
                                    homeBean.index = 0
                                    homeBean.data = KpiResultData.data
                                    mKpiData!!.add(homeBean)
                                }

                                if ("number2" == KpiResultData.data!![0].dashboard_type) {
                                    val homeBean = KpiBean()
                                    homeBean.group_name = KpiResultData.group_name
                                    homeBean.index = 2
                                    homeBean.data = KpiResultData.data
                                    mKpiData!!.add(homeBean)
                                }

                                if ("number3" == KpiResultData.data!![0].dashboard_type) {
                                    val homeBean = KpiBean()
                                    homeBean.group_name = KpiResultData.group_name
                                    homeBean.index = 3
                                    homeBean.data = KpiResultData.data
                                    mKpiData!!.add(homeBean)
                                }
                            }
                        }

                        val homeBean = KpiBean()
                        homeBean.group_name = "滚动文字"
                        homeBean.index = 1
                        mKpiData!!.add(homeBean)

                        val homeBean1 = KpiBean()
                        homeBean1.group_name = "底部信息"
                        homeBean1.index = 4
                        mKpiData!!.add(homeBean1)
                        ListUtils.sort(mKpiData, true, "index")

                        getHomeMsg()
                    }
                })
    }

    fun getHomeMsg() {
        if (!HttpUtil.isConnected(mActivity)) {
            ToastUtils.show(mActivity, "请检查网络链接")
            finishRequest()
            isEmpty = mKpiData == null || mKpiData!!.size == 0
            ErrorUtils.viewProcessing(refreshLayout, llError, llRetry, "无更多文章了", tvErrorMsg, ivError,
                    isEmpty!!, false, R.drawable.pic_3, {
                getData(true)
            })
            return
        }
        RetrofitUtil.getHttpService(context).getHomeMsg(queryMap)
                .compose(RetrofitUtil.CommonOptions<HomeMsgResult>())
                .subscribe(object : CodeHandledSubscriber<HomeMsgResult>() {
                    override fun onCompleted() {
                        finishRequest()
                    }

                    override fun onError(apiException: ApiException?) {
                        finishRequest()
                        ToastUtils.show(mActivity, apiException!!.displayMessage)
                    }

                    override fun onBusinessNext(data: HomeMsgResult?) {
                        if (mKpiData == null) mKpiData = ArrayList()

                        mKpiData!!
                                .filter { 1 == it.index }
                                .forEach { it.data = data!!.data }
                        ListUtils.sort(mKpiData, true, "index")
                        mAdapter.setData(mKpiData)

                        isEmpty = mKpiData == null || mKpiData!!.size == 0
                        ErrorUtils.viewProcessing(refreshLayout, llError, llRetry, "无更多文章了", tvErrorMsg, ivError,
                                isEmpty!!, true, R.drawable.pic_3, {
                            getData(true)
                        })
                    }
                })
    }

    fun finishRequest() {
        refreshLayout.finishRefreshing()
        refreshLayout.finishLoadmore()
        dismissLoading()
    }

    override fun itemClick(instituteDataBean: InstituteDataBean) {
    }
}
