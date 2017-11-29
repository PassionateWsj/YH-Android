package com.intfocus.template.subject.one.module.tables.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.intfocus.template.R

/**
 * Created by CANC on 2017/7/24.
 */
class TableTitleAdapter(val context: Context,
                        private var data: List<com.intfocus.template.subject.one.entity.MDetailUnitEntity>?,
                        var listener: NoticeItemListener) : RecyclerView.Adapter<TableTitleAdapter.NoticeMenuHolder>() {

    var inflater = LayoutInflater.from(context)

    fun setData(data: List<com.intfocus.template.subject.one.entity.MDetailUnitEntity>?) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeMenuHolder {
        val contentView = inflater.inflate(R.layout.item_table_title, parent, false)
        return NoticeMenuHolder(contentView)
    }

    override fun onBindViewHolder(holder: NoticeMenuHolder, position: Int) {
        if (data != null) {
            val noticeMenuData = data!![position]
            holder.tvText.text = noticeMenuData.title
            if (noticeMenuData.isCheck) {
                holder.tvText.setTextColor(ContextCompat.getColor(context, R.color.co1_syr))
                holder.viewLine.visibility = View.VISIBLE
            } else {
                holder.tvText.setTextColor(ContextCompat.getColor(context, R.color.co4_syr))
                holder.viewLine.visibility = View.INVISIBLE
            }
            holder.llTitle.setOnClickListener { listener.itemClick(position) }
        }
    }

    override fun getItemCount(): Int = if (data == null) 0 else data!!.size

    class NoticeMenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var llTitle: LinearLayout = itemView.findViewById(R.id.ll_title)
        var tvText: TextView = itemView.findViewById(R.id.tv_text)
        var viewLine: View = itemView.findViewById(R.id.view_line)
    }

    interface NoticeItemListener {
        fun itemClick(position: Int)
    }

}
