package com.intfocus.template.filter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup

/**
 * Created by CANC on 2017/8/9.
 */
class FragmentAdapter(fm: FragmentManager, private var mFragments: List<Fragment>?, private var mTitles: List<String>?) : FragmentStatePagerAdapter(fm) {

    fun updateTitles(titles: List<String>) {
        mTitles = titles
        this.notifyDataSetChanged()
    }

    fun updateFragments(fragments: List<Fragment>) {
        mFragments = fragments
        this.notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment = mFragments!![position]

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)
    }

    override fun getCount(): Int = mFragments?.size ?: 0

    override fun getPageTitle(position: Int): CharSequence = mTitles!![position]

    override fun getItemPosition(`object`: Any): Int = POSITION_NONE
}
