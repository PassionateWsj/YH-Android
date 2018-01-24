package com.intfocus.template.dashboard.report.adapter

import android.content.Context
import android.content.res.Configuration
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.intfocus.template.ConfigConstants
import com.intfocus.template.R
import com.intfocus.template.dashboard.report.mode.ListGroupBean
import com.intfocus.template.ui.view.CustomGridView

/**
 * Created by liuruilin on 2017/6/17.
 */
class ReportsRightRVAdapter(var ctx: Context, var datas: List<ListGroupBean>?)
    : RecyclerView.Adapter<ReportsRightRVAdapter.ReportsRightListHolder>() {

    fun setData(datas: List<ListGroupBean>?) {
        this.datas = datas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsRightRVAdapter.ReportsRightListHolder {
        val contentView = LayoutInflater.from(ctx).inflate(R.layout.item_reports_right_rv, parent, false)
        return ReportsRightRVAdapter.ReportsRightListHolder(contentView)
    }

    override fun onBindViewHolder(holder: ReportsRightRVAdapter.ReportsRightListHolder, position: Int) {
        holder.tvReportsListTitle.text = datas!![position].group_name
        holder.gvReportsListItem.numColumns = if (ctx.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ConfigConstants.REPORT_NUM_COLUMNS_LAND
        } else {
            ConfigConstants.REPORT_NUM_COLUMNS_PORT
        }
        holder.gvReportsListItem.adapter = ReportsRightGVAdapter(ctx, datas!![position].data)
    }

    override fun getItemCount(): Int = if (datas == null) 0 else datas!!.size

    class ReportsRightListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvReportsListTitle = itemView.findViewById<TextView>(R.id.tv_reports_right_list_title)
        var gvReportsListItem = itemView.findViewById<CustomGridView>(R.id.gv_reports_right_list_item)
    }
}
