package com.intfocus.template.dashboard.mine.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.intfocus.template.R
import com.intfocus.template.dashboard.mine.adapter.InstituteAdapter
import com.intfocus.template.dashboard.mine.bean.InstituteDataBean
import com.intfocus.template.ui.RefreshActivity
import com.intfocus.template.ConfigConstants
import com.intfocus.template.constant.Params.BANNER_NAME
import com.intfocus.template.constant.Params.LINK
import com.intfocus.template.constant.Params.USER_NUM
import com.intfocus.template.constant.ToastColor
import com.intfocus.template.model.request.RequestFavourite
import com.intfocus.template.model.response.BaseResult
import com.intfocus.template.model.response.article.ArticleResult
import com.intfocus.template.general.net.ApiException
import com.intfocus.template.general.net.CodeHandledSubscriber
import com.intfocus.template.general.net.RetrofitUtil
import com.intfocus.template.util.*
import com.intfocus.template.ui.view.CommonPopupWindow
import com.intfocus.template.subject.two.WebPageActivity
import com.lcodecore.tkrefreshlayout.footer.LoadingView
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView

class FavoriteArticleActivity : RefreshActivity(), InstituteAdapter.NoticeItemListener {

    lateinit var adapter: InstituteAdapter
    var datas: MutableList<InstituteDataBean>? = null
    lateinit var userNum: String
    private lateinit var statusMap: MutableMap<String, String>
    private lateinit var queryMap: MutableMap<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        setRefreshLayout()
        userNum = mActivity.getSharedPreferences("UserBean", Context.MODE_PRIVATE).getString(USER_NUM, "")
        init()
    }

    fun init() {
        statusMap = mutableMapOf()
        queryMap = mutableMapOf()
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
        getData(true)
    }

    /**
     * 获取数据
     */
    override fun getData(isShowDialog: Boolean) {
        if (!HttpUtil.isConnected(mActivity)) {
            finishRequest()
            isEmpty = datas == null || datas!!.size == 0
            ErrorUtils.viewProcessing(refreshLayout, llError, llRetry, "无更多文章了", tvErrorMsg, ivError,
                    isEmpty!!, false, R.drawable.pic_3, {
                getData(true)
            })
            ToastUtils.show(mActivity, "请检查网络链接")
            return
        }
        if (isShowDialog && (loadingDialog == null || !loadingDialog!!.isShowing)) {
            showDialog(this)
        }
        queryMap.put("user_num", userNum)
        queryMap.put("page", page.toString())
        queryMap.put("limit", pageSize.toString())
        RetrofitUtil.getHttpService(applicationContext).getMyFavouritedList(queryMap)
                .compose(RetrofitUtil.CommonOptions<ArticleResult>())
                .subscribe(object : CodeHandledSubscriber<ArticleResult>() {
                    override fun onCompleted() {
                        finishRequest()
                    }

                    override fun onError(apiException: ApiException) {
                        finishRequest()
                        ToastUtils.show(mActivity, apiException.displayMessage)
                    }

                    override fun onBusinessNext(data: ArticleResult) {
                        finishRequest()
                        totalPage = data.total_page
                        isLasePage = page == totalPage
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
        hideLoading()
    }


    /**
     * 操作收藏/取消收藏
     */
    fun articleOperating(articleId: String, status: String) {
        if (!HttpUtil.isConnected(mActivity)) {
            ToastUtils.show(mActivity, "请检查网络链接")
            return
        }
        showDialog(this)
        val body = RequestFavourite()
        body.user_num = userNum
        body.article_id = articleId
        body.favourite_status = status
        RetrofitUtil.getHttpService(applicationContext).articleOperating(body)
                .compose(RetrofitUtil.CommonOptions<BaseResult>())
                .subscribe(object : CodeHandledSubscriber<BaseResult>() {
                    override fun onCompleted() {
                    }

                    override fun onError(apiException: ApiException) {
                        hideLoading()
                        ToastUtils.show(mActivity, apiException.displayMessage)
                    }

                    override fun onBusinessNext(data: BaseResult) {
                        getData(true)
                        ToastUtils.show(mActivity, data.message + "", ToastColor.SUCCESS)
                    }
                })
    }

    override fun itemClick(instituteDataBean: InstituteDataBean) {
        val intent = Intent(mActivity, WebPageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val link = String.format("%s/mobile/v2/user/%s/article/%s", ConfigConstants.kBaseUrl, mUserSP.getString(K.K_USER_ID, "0").toString(), instituteDataBean.acticleId.toString())
        intent.putExtra(BANNER_NAME, instituteDataBean.title.toString())
        intent.putExtra(LINK, link)
        intent.putExtra("hideBannerSetting", true)
        startActivity(intent)
    }

    /**
     * 加入收藏
     */
    override fun addCollection(instituteDataBean: InstituteDataBean) {
        articleOperating(instituteDataBean.acticleId.toString(), "1")
    }

    /**
     * 取消收藏
     */
    override fun cancelCollection(instituteDataBean: InstituteDataBean) {
        CommonPopupWindow().showPopupWindow(mActivity, "取消收藏", R.color.co11_syr, "继续收藏", R.color.co3_syr,
                object : CommonPopupWindow.ButtonLisenter {
                    override fun btn1Click() {
                        articleOperating(instituteDataBean.acticleId.toString(), "2")
                    }

                    override fun btn2Click() {
                    }
                })
    }

    fun back(view: View) {
        finish()
    }
}
