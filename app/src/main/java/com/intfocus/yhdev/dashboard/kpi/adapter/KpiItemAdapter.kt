package com.intfocus.yhdev.dashboard.kpi.adapter

import android.content.Context
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.intfocus.yhdev.R
import com.intfocus.yhdev.dashboard.kpi.bean.KpiGroup
import com.intfocus.yhdev.dashboard.old_kpi.MarginDecoration
import com.intfocus.yhdev.util.DisplayUtil

/**
 * Created by liuruilin on 2017/7/10.
 */
class KpiItemAdapter(var ctx: Context, private var itemDatas: MutableList<KpiGroup>) : RecyclerView.Adapter<KpiItemAdapter.KpiItemHolder>() {
    var inflater = LayoutInflater.from(ctx)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KpiItemHolder {
        val contentView = inflater.inflate(R.layout.fragment_kpi_group, parent, false)
        return KpiItemHolder(contentView)
    }

    override fun onBindViewHolder(holder: KpiItemHolder, position: Int) {
        holder.tvKpiGroupName.text = itemDatas[position].group_name
        val recyclerView = holder.rcKpiItem
        val offset = DisplayUtil.dip2px(ctx, -3.5f)
        recyclerView.setPadding(offset, 0 - offset, offset, 0 - offset + 3)

        if (itemDatas[position].data!![0].dashboard_type.equals("number2")) {
            val mLayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)

            //设置布局管理器
            recyclerView.layoutManager = mLayoutManager
            //设置Adapter
            val recycleAdapter = NumberTwoItemAdapter(ctx, itemDatas[position].data)
            recyclerView.adapter = recycleAdapter
            //设置分隔线
            recyclerView.addItemDecoration(MarginDecoration(ctx))
            //设置增加或删除条目的动画
            recyclerView.itemAnimator = DefaultItemAnimator()
        } else {
            val mLayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
            //设置布局管理器
            recyclerView.layoutManager = mLayoutManager
            //设置Adapter
            val recycleAdapter = NumberThreeItemAdapter(ctx, itemDatas[position].data)
            recyclerView.adapter = recycleAdapter
            //设置分隔线
            recyclerView.addItemDecoration(MarginDecoration(ctx))
            //设置增加或删除条目的动画
            recyclerView.itemAnimator = DefaultItemAnimator()
        }
    }

    override fun getItemViewType(position: Int): Int = position

    override fun getItemCount(): Int =            itemDatas.size

    inner class KpiItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvKpiGroupName:TextView = view.findViewById (R.id.tv_kpi_group_name)
        var rcKpiItem:RecyclerView = view.findViewById (R.id.rv_kpi_group)
    }
}
