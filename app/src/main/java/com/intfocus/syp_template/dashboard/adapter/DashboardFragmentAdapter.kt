package com.intfocus.syp_template.dashboard.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup

/**
 * Created by liuruilin on 2017/3/23.
 */

class DashboardFragmentAdapter(fragmentManager: FragmentManager,val data:ArrayList<Fragment>) : FragmentPagerAdapter(fragmentManager) {

    override fun getCount(): Int = data.size

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }

    override fun getItem(position: Int): Fragment {
         return data[position]
    }
}
