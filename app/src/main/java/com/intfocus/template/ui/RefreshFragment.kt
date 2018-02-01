package com.intfocus.template.ui

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.intfocus.template.R
import com.intfocus.template.util.LoadingUtils
import com.intfocus.template.util.ToastUtils
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout

/**
 * Created by CANC on 2017/7/31.
 */
abstract class RefreshFragment : Fragment() {
    lateinit var mActivity: Activity
    var loadingDialog: Dialog? = null

    lateinit var refreshLayout: TwinklingRefreshLayout
    //错误界面
    //是否是空数据
    var isEmpty: Boolean? = true
    //页码信息
    var total: Int? = 0
    var page: Int? = 1
    var pagesize: Int? = 10
    //是否需要清空数据
    var isRefresh: Boolean? = true
    //是否最后一页
    var isLasePage: Boolean? = true
    var mView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity!!
    }

    fun showLoading() {
        loadingDialog = LoadingUtils.createLoadingDialog(mActivity)
        loadingDialog!!.show()
    }

    fun dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog!!.dismiss()
        }
    }

    fun setRefreshLayout() {
        refreshLayout = mView!!.findViewById(R.id.refresh_layout)
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
