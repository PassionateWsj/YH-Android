package com.intfocus.syp_template.filter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.syp_template.R
import com.intfocus.syp_template.dashboard.mine.adapter.FilterAdapter
import com.intfocus.syp_template.model.response.filter.MenuItem
import com.intfocus.syp_template.ui.view.CustomLinearLayoutManager

/**
 * Created by CANC on 2017/8/8.
 * 筛选界面
 */
class FilterFragment(menuDatas: List<MenuItem>, myLisenter: NewFilterFragmentListener) : Fragment(), FilterAdapter.FilterMenuListener {

    lateinit var mAdapter: FilterAdapter
    var datas = menuDatas
    lateinit var mView: View
    lateinit var recyclerView: RecyclerView
    var lisenter = myLisenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater!!.inflate(R.layout.fragment_filter, container, false)
        initView()
        return mView
    }

    fun initView() {
        recyclerView = mView.findViewById(R.id.recycler_view)
        mAdapter = FilterAdapter(context!!, datas, this)
        val layoutManager = CustomLinearLayoutManager(context!!)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = mAdapter
        mAdapter.update()
    }

    override fun itemClick(position: Int) {
        for (i in 0 until datas.size) {
            if (i == position) {
                datas[position].arrorDirection = true
            } else {
                datas[i].arrorDirection = false
            }
        }
        mAdapter.setData(datas)
        lisenter.itemClick(position, datas)
    }

    interface NewFilterFragmentListener {
        fun itemClick(position: Int, menuDatas: List<MenuItem>)
    }
}
