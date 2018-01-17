package com.intfocus.template.dashboard.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.view.ViewGroup
import com.intfocus.template.BuildConfig
import com.intfocus.template.dashboard.workbox.WorkBoxFragment

/**
 * Created by liuruilin on 2017/3/23.
 */

class DashboardFragmentAdapter(fragmentManager: FragmentManager, val data: ArrayList<Fragment>) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int = data.size

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }

    override fun getItem(position: Int): Fragment = data[position]

    override fun getItemPosition(`object`: Any): Int = if (`object` is WorkBoxFragment && "baozhentv" == BuildConfig.FLAVOR) {
        PagerAdapter.POSITION_NONE
    } else {
        super.getItemPosition(`object`)
    }
}
