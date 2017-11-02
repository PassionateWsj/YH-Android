package com.intfocus.yhdev.business.dashboard.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup
import com.intfocus.yhdev.business.dashboard.DashboardActivity
import com.intfocus.yhdev.business.dashboard.kpi.KpiFragment

import com.intfocus.yhdev.business.dashboard.mine.MinePageFragment
import com.intfocus.yhdev.business.dashboard.report.ReportFragment
import com.intfocus.yhdev.business.dashboard.workbox.WorkBoxFragment

/**
 * Created by liuruilin on 2017/3/23.
 */

class DashboardFragmentAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    private val PAGER_COUNT = 4
    //    private var mKpiFragment = KpiFragment()
    private var mMeterFragment = KpiFragment()
    private var mAnalysisFragment = ReportFragment()
    private var mAppFragment = WorkBoxFragment()
    private var mMessageFragment = MinePageFragment()

    override fun getCount(): Int = PAGER_COUNT

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            DashboardActivity.PAGE_KPI -> return mMeterFragment
            DashboardActivity.PAGE_REPORTS -> return mAnalysisFragment
            DashboardActivity.PAGE_APP -> return mAppFragment
            DashboardActivity.PAGE_MINE -> return mMessageFragment
        }
        return mMeterFragment
    }
}
