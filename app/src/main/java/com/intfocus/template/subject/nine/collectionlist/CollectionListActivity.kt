package com.intfocus.template.subject.nine.collectionlist

import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.LinearInterpolator
import com.chad.library.adapter.base.BaseQuickAdapter
import com.intfocus.template.R
import com.intfocus.template.listener.NoDoubleClickListener
import com.intfocus.template.model.entity.Collection
import com.intfocus.template.subject.nine.collectionlist.adapter.CollectionListAdapter
import com.intfocus.template.ui.BaseActivity
import com.intfocus.template.ui.view.DefaultRefreshView
import com.intfocus.template.util.SnackbarUtil
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import kotlinx.android.synthetic.main.activity_collection_list.*
import kotlinx.android.synthetic.main.content_collection_list.*
import java.util.*


class CollectionListActivity : BaseActivity(), CollectionListContract.View {

    override lateinit var presenter: CollectionListContract.Presenter
    private lateinit var mAdapter: CollectionListAdapter
    private var startSync = false
    private var animator: ObjectAnimator? = null
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_list)

        initView()
        initAdapter()
        initListener()
        initData()
    }

    private fun initView() {
        val headerView = DefaultRefreshView(this)
        headerView.setArrowResource(R.drawable.loading_up)
        refresh_collection_list_layout.setHeaderView(headerView)
        refresh_collection_list_layout.setEnableLoadmore(false)
    }

    private fun initListener() {
        fab_collection_list.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(view: View) {
                startSync = !startSync
                if (startSync) {
                    SnackbarUtil.shortSnackbar(view,
                            "正在同步",
                            ContextCompat.getColor(this@CollectionListActivity,R.color.co10_syr),
                            ContextCompat.getColor(this@CollectionListActivity,R.color.co1_syr))
                            .show()
                    timer = Timer()
                    timer?.schedule(object : TimerTask() {
                        override fun run() {
                            runOnUiThread {
                                startSyncAnim(view)
                            }
                        }
                    }, 0, 1000)

                } else {
                    SnackbarUtil.shortSnackbar(view,
                            "取消同步",
                            ContextCompat.getColor(this@CollectionListActivity,R.color.co10_syr),
                            ContextCompat.getColor(this@CollectionListActivity,R.color.co11_syr))
                            .show()
                    stopSyncAnim()
                }
            }
        })
        edit_collection_list_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                presenter.loadData(s.toString())
            }
        })
        refresh_collection_list_layout.setOnRefreshListener(object : RefreshListenerAdapter() {
            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                super.onRefresh(refreshLayout)
                presenter.loadData()
            }
        })
    }

    private fun startSyncAnim(view: View) {
        animator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f)
        animator?.interpolator = LinearInterpolator()
        animator?.duration = 1000
        animator?.start()
    }

    private fun stopSyncAnim() {
        animator?.cancel()
        timer?.cancel()
    }

    private fun initAdapter() {
        mAdapter = CollectionListAdapter()
        mAdapter.openLoadAnimation()
        // 默认提供5种方法（渐显、缩放、从下到上，从左到右、从右到左）
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT)
        rv_collection_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_collection_list.adapter = mAdapter
    }

    private fun initData() {
        CollectionListPresenter(CollectionListModelImpl.getInstance(), this)
        presenter.loadData()
    }

    override fun updateData(dataList: List<Collection>) {
        mAdapter.setNewData(dataList)
        finishRequest()
    }

    private fun finishRequest() {
        refresh_collection_list_layout.finishRefreshing()
    }

    override fun dismissActivity(v: View) {
        super.dismissActivity(v)
        stopSyncAnim()
    }

}
