package com.intfocus.syp_template.filter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.*
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import com.intfocus.syp_template.R
import com.intfocus.syp_template.model.response.filter.MenuItem

/**
 * Created by CANC on 2017/8/9.
 * 筛选专用dialogfragment,支持所有数据深度
 */
class FilterDialogFragment : DialogFragment(), FilterFragment.NewFilterFragmentListener {

    lateinit var mView: View
    lateinit var ivClose: ImageView
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    var currentPosition: Int? = 0
    lateinit var adapter: FragmentAdapter
    var titleList = ArrayList<String>()
    var mFragmentList = ArrayList<Fragment>()
    var mDataList: List<MenuItem>? = null
    var mListener: FilterListener? = null
    var selectedDatas = ArrayList<MenuItem>()

    companion object {

        val FILTER_DATA = ""

        fun newInstance(dataList: ArrayList<MenuItem>): FilterDialogFragment? {
            if (!dataList.isNotEmpty()) {
                return null
            }
            val args = Bundle()
            args.putSerializable(FILTER_DATA, dataList)
            val fragment = FilterDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as FilterListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataList = arguments!!.getSerializable(FILTER_DATA) as ArrayList<MenuItem>
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //放置位置
        dialog.window.setGravity(Gravity.LEFT)
        dialog.window.setGravity(Gravity.BOTTOM)
        //设置布局
        val mView = LayoutInflater.from(activity).inflate(R.layout.dialog_fragment_filter, null)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        dialog.window.setWindowAnimations(R.style.anim_popup_bottombar)
        this.mView = mView
        ivClose = mView.findViewById(R.id.iv_close)
        tabLayout = mView.findViewById(R.id.tab_layout)
        viewPager = mView.findViewById(R.id.view_pager)
        tabLayout.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.co10_syr))
        tabLayout.setSelectedTabIndicatorHeight(3)
        tabLayout.setTabTextColors(ContextCompat.getColor(activity!!, R.color.co4_syr), ContextCompat.getColor(activity!!, R.color.co1_syr))
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(activity!!, R.color.co1_syr))
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                currentPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        ivClose.setOnClickListener { this.dismiss() }
        var currentStr = "请选择"
        mDataList!!.filter { it.arrorDirection }
                .forEach { currentStr = it.name!! }
        titleList.add(currentStr)
        mFragmentList.add(FilterFragment(mDataList!!, this))
        adapter = FragmentAdapter(childFragmentManager, mFragmentList, titleList)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        viewPager.currentItem = currentPosition!!
        return mView
    }

    override fun itemClick(position: Int, menuDatas: List<MenuItem>) {
        /**
         * 还有下一节点，继续添加新界面,没有下一节点，调用完成方法
         */
        if (menuDatas[position].data != null && menuDatas[position].data!!.size > 0) {
            titleList[currentPosition!!] = menuDatas[position].name!!
            val menuItem = MenuItem(menuDatas[position].id, menuDatas[position].name)
            if (selectedDatas.size >= currentPosition!!) {
                selectedDatas.add(menuItem)
            } else {
                selectedDatas[currentPosition!!] = menuItem
            }
            //用于返回上级后的点击列表处理
            var i = titleList.size - 1
            while (i > currentPosition!!) {
                titleList.removeAt(i)
                mFragmentList.removeAt(i)
                selectedDatas.removeAt(i)
                i--
            }
            titleList.add("请选择")
            currentPosition = currentPosition!! + 1
            for (data in menuDatas[position].data!!) {
                data.arrorDirection = false
            }
            mFragmentList.add(FilterFragment(menuDatas[position].data!!, this))

            adapter.updateFragments(mFragmentList)
            adapter.updateTitles(titleList)
            tabLayout.setupWithViewPager(viewPager)
            viewPager.currentItem = currentPosition!!
        } else {
            titleList[currentPosition!!] = menuDatas[position].name!!
            val menuItem = MenuItem(menuDatas[position].id, menuDatas[position].name)
            if (selectedDatas.size >= currentPosition!!) {
                selectedDatas.add(menuItem)
            } else {
                selectedDatas[currentPosition!!] = menuItem
            }

            adapter.updateFragments(mFragmentList)
            adapter.updateTitles(titleList)
            tabLayout.setupWithViewPager(viewPager)
            viewPager.currentItem = currentPosition!!
            mListener!!.complete(selectedDatas)
            this.dismiss()
        }
    }

    override fun onResume() {
        val params = dialog.window!!.attributes
        params.width = LayoutParams.MATCH_PARENT
        params.height = LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params
        super.onResume()
    }

    interface FilterListener {
        fun complete(menuItems: ArrayList<MenuItem>)
    }
}
