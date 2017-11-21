package com.intfocus.syp_template.business.dashboard.mine.widget

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.intfocus.syp_template.R
import com.intfocus.syp_template.business.dashboard.mine.adapter.InstituteAdapter
import com.intfocus.syp_template.business.dashboard.mine.bean.InstituteDataBean
import com.intfocus.syp_template.business.subject.webapplication.WebApplicationActivity
import com.intfocus.syp_template.general.base.RefreshFragment
import com.intfocus.syp_template.general.constant.ConfigConstants
import com.intfocus.syp_template.general.constant.ToastColor
import com.intfocus.syp_template.general.data.request.RequestFavourite
import com.intfocus.syp_template.general.data.response.BaseResult
import com.intfocus.syp_template.general.data.response.article.ArticleResult
import com.intfocus.syp_template.general.net.ApiException
import com.intfocus.syp_template.general.net.CodeHandledSubscriber
import com.intfocus.syp_template.general.net.RetrofitUtil
import com.intfocus.syp_template.general.util.*
import com.lcodecore.tkrefreshlayout.footer.LoadingView
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView
import org.xutils.x

/**
 * Created by CANC on 2017/7/31.
 */

class DataCollegeFragment : RefreshFragment(), InstituteAdapter.NoticeItemListener {

    lateinit var adapter: InstituteAdapter
    var datas: MutableList<InstituteDataBean>? = null
    private lateinit var queryMap: MutableMap<String, String>
    private lateinit var statusMap: MutableMap<String, String>
    lateinit var userNum: String
    private var keyWord: String? = ""
    private lateinit var editSearch: EditText
    lateinit var mUserSP: SharedPreferences


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_instiute, container, false)
        x.view().inject(this, mView)
        setRefreshLayout()
        initView()
        mUserSP = mActivity.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        userNum = mUserSP.getString(URLs.kUserNum, "")
        getData(true)
        return mView
    }

    fun initView() {
        queryMap = mutableMapOf()
        statusMap = mutableMapOf()
        editSearch = mView!!.findViewById(R.id.edit_search)
        val mLayoutManager = LinearLayoutManager(mActivity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = mLayoutManager
        adapter = InstituteAdapter(mActivity, null, this)
        recyclerView.adapter = adapter
        val headerView = SinaRefreshView(mActivity)
        headerView.setArrowResource(R.drawable.loading_up)
        val bottomView = LoadingView(mActivity)
        refreshLayout.setHeaderView(headerView)
        refreshLayout.setBottomView(bottomView)

        editSearch.setOnEditorActionListener({ textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                keyWord = textView.text.toString().trim()
                getData(true)
            }
            false
        })
    }

    /**
     * 获取数据
     */
    override fun getData(isShowDialog: Boolean) {
        if (!HttpUtil.isConnected(mActivity)) {
            ToastUtils.show(mActivity, "请检查网络链接")
            finshRequest()
            isEmpty = datas == null || datas!!.size == 0
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

        queryMap.put("user_num", userNum)
        queryMap.put("page", page.toString())
        queryMap.put("limit", pagesize.toString())
        queryMap.put("keyword", keyWord.toString())
        RetrofitUtil.getHttpService(context).getArticleList(queryMap)
                .compose(RetrofitUtil.CommonOptions<ArticleResult>())
                .subscribe(object : CodeHandledSubscriber<ArticleResult>() {
                    override fun onCompleted() {
                        finshRequest()
                    }

                    override fun onError(apiException: ApiException) {
                        finshRequest()
                        ToastUtils.show(mActivity, apiException.displayMessage)
                    }

                    override fun onBusinessNext(data: ArticleResult) {
                        finshRequest()
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

    /**
     * 操作收藏/取消收藏
     */
    private fun articleOperating(articleId: String, status: String) {
        if (!HttpUtil.isConnected(mActivity)) {
            ToastUtils.show(mActivity, "请检查网络链接")
            return
        }
        showLoading()
        val body = RequestFavourite()
        body.user_num = userNum
        body.article_id = articleId
        body.favourite_status = status
        RetrofitUtil.getHttpService(context).articleOperating(body)
                .compose(RetrofitUtil.CommonOptions<BaseResult>())
                .subscribe(object : CodeHandledSubscriber<BaseResult>() {
                    override fun onCompleted() {
                    }

                    override fun onError(apiException: ApiException) {
                        dismissLoading()
                        ToastUtils.show(mActivity, apiException.displayMessage)
                    }

                    override fun onBusinessNext(data: BaseResult) {
                        getData(true)
                        ToastUtils.show(mActivity, data.message + "", ToastColor.SUCCESS)
                    }
                })
    }

    fun finshRequest() {
        refreshLayout.finishRefreshing()
        refreshLayout.finishLoadmore()
        dismissLoading()
    }

    /**
     * 详情
     */
    override fun itemClick(instituteDataBean: InstituteDataBean) {
        val intent = Intent(mActivity, WebApplicationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val link = String.format("%s/mobile/v2/user/%s/article/%s", ConfigConstants.kBaseUrl, mUserSP.getString(K.K_USER_ID, "0").toString(), instituteDataBean.id.toString())
        intent.putExtra(URLs.kBannerName, "数据学院")
        intent.putExtra(URLs.kLink, link)
        intent.putExtra("hideBannerSetting", true)
        startActivity(intent)
    }

    /**
     * 收藏
     */
    override fun addCollection(instituteDataBean: InstituteDataBean) {
        articleOperating(instituteDataBean.id.toString(), "1")
    }

    /**
     * 取消收藏
     */
    override fun cancelCollection(instituteDataBean: InstituteDataBean) {
        articleOperating(instituteDataBean.id.toString(), "2")
    }
}