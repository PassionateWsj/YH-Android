package com.intfocus.template.dashboard.mine.widget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.template.R
import com.intfocus.template.constant.Params.USER_BEAN
import com.intfocus.template.constant.Params.USER_NUM
import com.intfocus.template.dashboard.mine.activity.NoticeContentActivity
import com.intfocus.template.dashboard.mine.adapter.NoticeListAdapter
import com.intfocus.template.dashboard.mine.adapter.NoticeMenuAdapter
import com.intfocus.template.dashboard.mine.bean.NoticeMenuBean
import com.intfocus.template.general.net.ApiException
import com.intfocus.template.general.net.CodeHandledSubscriber
import com.intfocus.template.general.net.RetrofitUtil
import com.intfocus.template.model.response.notice.Notice
import com.intfocus.template.model.response.notice.NoticesResult
import com.intfocus.template.ui.RefreshFragment
import com.intfocus.template.util.ActionLogUtil
import com.intfocus.template.util.ErrorUtils
import com.intfocus.template.util.HttpUtil
import com.intfocus.template.util.ToastUtils
import com.lcodecore.tkrefreshlayout.footer.LoadingView
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView
import org.xutils.x

/**
 * Created by liuruilin on 2017/6/7.
 */

class AnnouncementWarningFragment : RefreshFragment(), NoticeListAdapter.NoticeItemListener, NoticeMenuAdapter.NoticeItemListener {

    private lateinit var userId: String
    private var datas: MutableList<Notice>? = null
    //    var id = ""
    private lateinit var adapter: NoticeListAdapter
    private lateinit var queryMap: MutableMap<String, String>
    /**
     *菜单
     */
    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var noticeMenuAdapter: NoticeMenuAdapter //筛选适配器
    private var noticeMenuDatas: MutableList<NoticeMenuBean>? = null//筛选数据
    private var typeStr: String? = null //筛选条件

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_notice, container, false)
        x.view().inject(this, mView)
        setRefreshLayout()
        userId = mActivity.getSharedPreferences(USER_BEAN, Context.MODE_PRIVATE).getString(USER_NUM, "")
        initView()
        getData(true)
        return mView
    }

    fun initView() {
        if (TextUtils.isEmpty(typeStr)) {
            typeStr = "0,1,2,3"
        }
        queryMap = mutableMapOf()

        val mLayoutManager = LinearLayoutManager(mActivity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = mLayoutManager
        adapter = NoticeListAdapter(mActivity, null, this)
        recyclerView.adapter = adapter
        val headerView = SinaRefreshView(mActivity)
        headerView.setArrowResource(R.drawable.loading_up)
        val bottomView = LoadingView(mActivity)
        refreshLayout.setHeaderView(headerView)
        refreshLayout.setBottomView(bottomView)

        //数据为空(即第一次打开此界面)才初始化Menu数据）,默认全部选中
        if (noticeMenuDatas == null || noticeMenuDatas!!.size == 0) {
            val noticeMenuBean = NoticeMenuBean(0, true)
            val noticeMenuBean1 = NoticeMenuBean(1, true)
            val noticeMenuBean2 = NoticeMenuBean(2, true)
            val noticeMenuBean3 = NoticeMenuBean(3, true)
            noticeMenuDatas = ArrayList()
            noticeMenuDatas!!.add(noticeMenuBean)
            noticeMenuDatas!!.add(noticeMenuBean1)
            noticeMenuDatas!!.add(noticeMenuBean2)
            noticeMenuDatas!!.add(noticeMenuBean3)
        }
        menuRecyclerView = mView!!.findViewById(R.id.menu_recycler_view)
        val mMenuLayoutManager = LinearLayoutManager(mActivity)
        mMenuLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        menuRecyclerView.layoutManager = mMenuLayoutManager
        noticeMenuAdapter = NoticeMenuAdapter(mActivity, noticeMenuDatas, this)
        menuRecyclerView.adapter = noticeMenuAdapter
    }

    override fun getData(isShowDialog: Boolean) {
        if (!HttpUtil.isConnected(mActivity)) {
            ToastUtils.show(mActivity, "请检查网络链接")
            finishRequest()
            isEmpty = datas == null || datas!!.size == 0
            ErrorUtils.viewProcessing(refreshLayout, llError, llRetry, "无更多公告", tvErrorMsg, ivError,
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
        queryMap.put("user_num", userId)
        queryMap.put("type", typeStr.toString())
        queryMap.put("page", page.toString())
        queryMap.put("limit", pagesize.toString())
        RetrofitUtil.getHttpService(context).getNoticeList(queryMap)
                .compose(RetrofitUtil.CommonOptions<NoticesResult>())
                .subscribe(object : CodeHandledSubscriber<NoticesResult>() {
                    override fun onCompleted() {
                        finishRequest()
                    }

                    override fun onError(apiException: ApiException?) {
                        finishRequest()
                        ToastUtils.show(mActivity, apiException!!.displayMessage)
                    }

                    override fun onBusinessNext(data: NoticesResult) {
                        finishRequest()
                        total = data.total_page
                        isLasePage = page == total
                        if (datas == null) {
                            datas = ArrayList()
                        }
                        if (isRefresh!!) {
                            datas!!.clear()
                        }

                        datas!!.addAll(data.data)
                        adapter.setData(datas)
                        isEmpty = datas == null || datas!!.size == 0
                        ErrorUtils.viewProcessing(refreshLayout, llError, llRetry, "无更多文章了", tvErrorMsg, ivError,
                                isEmpty!!, true, R.drawable.pic_3, null)
                    }
                })
    }

    fun finishRequest() {
        refreshLayout.finishRefreshing()
        refreshLayout.finishLoadmore()
        dismissLoading()
    }

    /**
     * 进入详情
     */
    override fun itemClick(position: Int) {
        val intent = Intent(mActivity, NoticeContentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("notice_id", position.toString())
        startActivity(intent)

        ActionLogUtil.actionLog("点击/公告预警")
    }

    /**
     *点击筛选
     */
    override fun menuClick(noticeMenuBean: NoticeMenuBean) {
        typeStr = ""
        if (noticeMenuDatas != null) {
            for (data in noticeMenuDatas!!.iterator()) {
                if (data == noticeMenuBean) {
                    data.isSelected = !data.isSelected
                }
                if (data.isSelected) {
                    typeStr = if (!TextUtils.isEmpty(typeStr)) {
                        typeStr + "," + data.code
                    } else {
                        "" + data.code
                    }
                }
            }
            noticeMenuAdapter.setData(noticeMenuDatas)
        }

        getData(true)
    }
}
