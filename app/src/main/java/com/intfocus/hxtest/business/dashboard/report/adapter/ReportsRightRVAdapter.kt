package com.intfocus.hxtest.business.dashboard.report.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.intfocus.hxtest.R
import com.intfocus.hxtest.business.dashboard.report.mode.ListGroupBean
import com.intfocus.hxtest.general.view.MyGridView

/**
 * Created by liuruilin on 2017/6/17.
 */
class ReportsRightRVAdapter(var ctx: Context, var datas: List<ListGroupBean>?)
    : RecyclerView.Adapter<ReportsRightRVAdapter.ReportsRightListHolder>() {

    var inflater = LayoutInflater.from(ctx)

    fun setData(datas: List<ListGroupBean>?) {
        this.datas = datas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsRightRVAdapter.ReportsRightListHolder {
        val contentView = inflater.inflate(R.layout.item_reports_right_rv, parent, false)
        return ReportsRightRVAdapter.ReportsRightListHolder(contentView)
    }

    override fun onBindViewHolder(holder: ReportsRightRVAdapter.ReportsRightListHolder, position: Int) {
        holder.tvReportsListTitle.text = datas!![position].group_name
        holder.gvReportsListItem.adapter = ReportsRightGVAdapter(ctx, datas!![position].data)
    }

    override fun getItemCount(): Int {
        return if (datas == null) 0 else datas!!.size
    }

    class ReportsRightListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvReportsListTitle = itemView.findViewById<TextView>(R.id.tv_reports_right_list_title)
        var gvReportsListItem = itemView.findViewById<MyGridView>(R.id.gv_reports_right_list_item)
    }
}
