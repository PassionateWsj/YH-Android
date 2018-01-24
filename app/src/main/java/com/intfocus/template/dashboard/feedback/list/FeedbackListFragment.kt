package com.intfocus.template.dashboard.feedback.list

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.template.R
import com.intfocus.template.dashboard.feedback.FeedbackInputActivity
import com.intfocus.template.dashboard.feedback.FeedbackModelImpl
import com.intfocus.template.model.response.mine_page.FeedbackList
import com.intfocus.template.ui.BaseFragment
import com.intfocus.template.ui.view.RecyclerItemDecoration
import com.intfocus.template.util.ToastUtils
import kotlinx.android.synthetic.main.fragment_feedback_list.*

/**
 * @author liuruilin
 * @data 2017/12/4
 * @describe
 */
class FeedbackListFragment : BaseFragment(), FeedbackListContract.View {
    override lateinit var presenter: FeedbackListContract.Presenter

    private var rootView: View? = null

    private var mLayoutManager: LinearLayoutManager? = null
    lateinit var feedbackListAdapter: FeedbackListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        if (rootView == null) {
        rootView = inflater.inflate(R.layout.fragment_feedback_list, container, false)
        feedbackListAdapter = FeedbackListAdapter(ctx)
        mLayoutManager = LinearLayoutManager(ctx)
        mLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
//        }

        showDialog(ctx)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onResume() {
        super.onResume()
        if (this::presenter.isInitialized) {
            presenter.getList()
        } else {
            presenter = FeedbackListPresenter(FeedbackModelImpl.getInstance(), this)
            presenter.getList()
        }
//        srl_feedback_list.isRefreshing = true
    }

    fun initView() {
        rv_feedback_list.layoutManager = mLayoutManager
        rv_feedback_list.adapter = feedbackListAdapter
        if (rv_feedback_list.itemDecorationCount == 0) {
            rv_feedback_list.addItemDecoration(RecyclerItemDecoration(ctx))
        }
        btn_submit.setOnClickListener { startFeedbackInput() }
        srl_feedback_list.setColorSchemeResources(R.color.co1_syr)
        srl_feedback_list.setOnRefreshListener {
            presenter.getList()
        }
    }

    override fun showList(data: FeedbackList) {
        srl_feedback_list.isRefreshing = false
        hideLoading()
        data.data?.let {
            feedbackListAdapter.setData(it)
        }
    }

    private fun startFeedbackInput() {
        val intent = Intent(ctx, FeedbackInputActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    override fun showNullPage() {
        srl_feedback_list.isRefreshing = false
        ToastUtils.show(ctx, "暂无数据")
    }
}
