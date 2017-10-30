package com.intfocus.yhdev.dashboard.kpi

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.yhdev.R
import com.intfocus.yhdev.base.BaseModeFragment
import com.intfocus.yhdev.dashboard.DashboardActivity
import com.intfocus.yhdev.dashboard.kpi.adapter.KpiItemAdapter
import com.intfocus.yhdev.dashboard.kpi.adapter.KpiStickAdapter
import com.intfocus.yhdev.dashboard.kpi.bean.KpiGroup
import com.intfocus.yhdev.dashboard.kpi.bean.KpiRequest
import com.intfocus.yhdev.dashboard.kpi.mode.KpiMode
import com.intfocus.yhdev.listen.CustPagerTransformer
import com.intfocus.yhdev.util.DisplayUtil
import com.intfocus.yhdev.util.ErrorUtils
import com.intfocus.yhdev.view.DefaultRefreshView
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.fragment_kpi.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import sumimakito.android.advtextswitcher.Switcher
import java.util.*

/**
 * Created by liuruilin on 2017/6/20.
 */
class KpiFragment : BaseModeFragment<KpiMode>(), ViewPager.OnPageChangeListener, NestedScrollView.OnScrollChangeListener {
    private lateinit var mViewPagerAdapter: KpiStickAdapter
    private var rootView: View? = null
    //  private var gson = Gson()
    private lateinit var mUserSP: SharedPreferences
    private val FIRST_PAGE_INDEX: Int = 0
    private var stickSzize: Int = 0
    private var timer = Timer()
    private lateinit var stickCycle: StickCycleTask

    override fun setSubject(): Subject {
        mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        return KpiMode(ctx)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        initAffiche()
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        if (rootView == null) {
            rootView = inflater!!.inflate(R.layout.fragment_kpi, container, false)
            model.requestData()
        }
        return rootView
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun initView(result: KpiRequest) {
        trl_refresh_layout.finishLoadmore()
        trl_refresh_layout.finishRefreshing()
        stickCycle = StickCycleTask()

        val topFragment: MutableList<Fragment> = mutableListOf()
        val kpiDatas: MutableList<KpiGroup> = mutableListOf()
        val datas = result.kpi_data

        for (kpiGroupDatas in datas!!.data!!.iterator()) {
            if (kpiGroupDatas.group_name.equals("top_data")) {
                for (kpiGroupItem in kpiGroupDatas.data!!.iterator()) {
                    topFragment.add(NumberOneFragment.newInstance(kpiGroupItem))
                }
            } else {
                kpiDatas.add(kpiGroupDatas)
            }
        }

        stickSzize = topFragment.size
        mViewPagerAdapter = KpiStickAdapter(childFragmentManager, topFragment)
        mViewPagerAdapter.switchTo(FIRST_PAGE_INDEX)

        vp_kpi_stick.setPageTransformer(false, CustPagerTransformer(act.applicationContext))
        vp_kpi_stick.adapter = mViewPagerAdapter
        vp_kpi_stick.addOnPageChangeListener(this)
        vp_kpi_stick.currentItem = 0

        indicator.setViewPager(vp_kpi_stick)
        timer.schedule(stickCycle, 0, 5000)


        val layoutManager: StaggeredGridLayoutManager
        layoutManager = object : StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL) {
            override fun canScrollVertically(): Boolean = false
        }

        rc_kpi_groups.layoutManager = layoutManager
        val recycleAdapter = KpiItemAdapter(ctx, kpiDatas)
        rc_kpi_groups.adapter = recycleAdapter

        val headerView = DefaultRefreshView(ctx)
        headerView.setArrowResource(R.drawable.loading_up)
        trl_refresh_layout.setHeaderView(headerView)
        trl_refresh_layout.setOnRefreshListener(object : RefreshListenerAdapter(), ErrorUtils.ErrorLisenter {
            override fun retry() {
                model.requestData()
            }

            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                model.requestData()
                super.onRefresh(refreshLayout)
            }

        })

        rootView!!.invalidate()
    }

    /**
     * 初始化公告控件
     */
    private fun initAffiche() {
        ll_kpi_notice.visibility = View.GONE
        Thread(Runnable {
            val texts = model.getMessage()
            if (texts != null) {
                activity!!.runOnUiThread {
                    tv_kpi_notice.setTexts(texts)
                    tv_kpi_notice.setCallback(activity as DashboardActivity)
                    Switcher().attach(tv_kpi_notice).setDuration(5000).start()
                    ll_kpi_notice.visibility = View.VISIBLE
                }
            }
        }).start()
    }

    override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        var alpha = 0
        val scale: Float
        val height = DisplayUtil.dip2px(ctx, 129f)
        if (scrollY <= height) {
            scale = (scrollY / height).toFloat()
            alpha = (255 * scale).toInt()
            rl_action_bar.setBackgroundColor(Color.argb(alpha, 255, 0, 0))
        } else {
            if (alpha < 255) {
                alpha = 255
                rl_action_bar.setBackgroundColor(Color.argb(alpha, 255, 0, 0))
            }
        }
    }

    //重写ViewPager页面切换的处理方法
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        mViewPagerAdapter.switchTo(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    inner class StickCycleTask : TimerTask() {
        override fun run() {
            act.runOnUiThread {
                if (vp_kpi_stick != null) {
                    if (vp_kpi_stick.currentItem + 1 < stickSzize) {
                        vp_kpi_stick.currentItem = vp_kpi_stick.currentItem + 1
                    } else {
                        vp_kpi_stick.currentItem = 0
                    }
                }
            }
        }
    }
}
