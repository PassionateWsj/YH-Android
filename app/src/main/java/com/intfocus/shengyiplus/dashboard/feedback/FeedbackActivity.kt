package com.intfocus.shengyiplus.dashboard.feedback

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.View
import com.intfocus.shengyiplus.R
import com.intfocus.shengyiplus.dashboard.feedback.content.FeedbackContentFragment
import com.intfocus.shengyiplus.dashboard.feedback.list.FeedbackListFragment
import com.intfocus.shengyiplus.ui.BaseActivity
import kotlinx.android.synthetic.main.item_action_bar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author liuruilin
 * @data 2017/11/28
 * @describe 问题反馈页面
 */
class FeedbackActivity : BaseActivity() {
    private lateinit var mFragmentManager: FragmentManager
    private lateinit var mFragmentTransaction: FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        EventBus.getDefault().register(this)
        initView()
        onCreateFinish()
    }

    private fun initView() {
        tv_banner_title.text = resources.getText(R.string.activity_feedback)
        iv_banner_setting.visibility = View.INVISIBLE

        mFragmentManager = supportFragmentManager
    }

    private fun onCreateFinish() {
        showList()
    }

    override fun onBackPressed() {
        Log.i("fragment", "mFragmentManager:    " + mFragmentManager.fragments.size)
        Log.i("fragment", "backStackEntryCount:    " + mFragmentManager.backStackEntryCount)

        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun showList() {
        mFragmentTransaction = mFragmentManager.beginTransaction()
        mFragmentTransaction.add(R.id.fl_container, FeedbackListFragment()).commit()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun showContent(content: EventFeedbackContent) {
        mFragmentTransaction = mFragmentManager.beginTransaction()
        mFragmentTransaction.replace(R.id.fl_container, FeedbackContentFragment.getInstance(content.id)).commit()
        mFragmentTransaction.addToBackStack("list").commit()
    }
}
