package com.intfocus.template.ui

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.intfocus.template.R
import com.intfocus.template.util.ToastUtils
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout

/**
 * Created by CANC on 2017/7/31.
 */
abstract class RefreshActivity : BaseActivity() {
    lateinit var mActivity: Activity
    lateinit var refreshLayout: TwinklingRefreshLayout
    lateinit var recyclerView: RecyclerView
    //错误界面
    lateinit var llError: LinearLayout
    lateinit var tvErrorMsg: TextView
    lateinit var ivError: ImageView
    lateinit var llRetry: LinearLayout
    //是否是空数据
    var isEmpty: Boolean? = true
    //页码信息
    var totalPage: Int? = 0
    var page: Int? = 1
    var pageSize: Int = 10
    //是否需要清空数据
    var isRefresh: Boolean? = true
    //是否最后一页
    var isLasePage: Boolean? = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = this
    }

    fun setRefreshLayout() {
        refreshLayout = findViewById(R.id.refresh_layout)
        recyclerView = findViewById(R.id.recycler_view)
        llError = findViewById(R.id.ll_empty)
        tvErrorMsg = findViewById(R.id.tv_errorMsg)
        ivError = findViewById(R.id.iv_error)
        llRetry = findViewById(R.id.ll_retry)

        refreshLayout.setOnRefreshListener(object : RefreshListenerAdapter() {
            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                super.onRefresh(refreshLayout)
                isRefresh = true
                page = 1
                getData(false)
            }

            override fun onLoadMore(refreshLayout: TwinklingRefreshLayout?) {
                super.onLoadMore(refreshLayout)
                if (isLasePage!!) {
                    ToastUtils.show(mActivity, "已经是最后一页")
                    refreshLayout!!.finishRefreshing()
                    refreshLayout.finishLoadmore()
                    return
                }
                page = page!! + 1
                isRefresh = false
                getData(false)
            }
        })
    }

    /**
     * 获取数据
     * @param isShowDialog
     */
    protected abstract fun getData(isShowDialog: Boolean)

}
