package com.intfocus.hxtest.business.subject.template.five.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils.TruncateAt.MIDDLE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.intfocus.hxtest.R
import com.intfocus.hxtest.general.util.Utils
import com.intfocus.hxtest.business.subject.template.five.bean.Head
import com.intfocus.hxtest.business.subject.template.five.bean.MainData

/**
 * Created by CANC on 2017/4/6.
 */

class TableContentItemAdapter(private val context: Context, private var heads: List<Head>?, private var mainData: List<MainData>?, private var rowHeight: Int//1行,2行,3行
                              , var listener: ContentItemListener) : RecyclerView.Adapter<TableContentItemAdapter.TableHeadHolder>() {
    var inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableHeadHolder {
        val contentView = inflater.inflate(R.layout.item_table_main_item, parent, false)
        return TableHeadHolder(contentView)
    }

    fun setData(heads: List<Head>, mainData: List<MainData>, rowHeight: Int) {
        this.heads = heads
        this.mainData = mainData
        this.rowHeight = rowHeight
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TableHeadHolder, position: Int) {
        if (!heads!![position].isShow || heads!![position].isKeyColumn) {
            holder.tvMain.visibility = View.GONE
        } else {
            holder.tvMain.text = mainData!![position].value
            holder.tvMain.ellipsize = MIDDLE
            holder.tvMain.maxLines = rowHeight
            holder.tvMain.visibility = View.VISIBLE
            holder.tvMain.layoutParams.height = Utils.dpToPx(context, (25 * rowHeight).toFloat())
            holder.tvMain.setOnClickListener { listener.itemClick(position) }
        }
    }

    override fun getItemCount(): Int = if (mainData == null) 0 else mainData!!.size

    class TableHeadHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvMain:TextView = itemView.findViewById(R.id.tv_main)
    }

    interface ContentItemListener {
        fun itemClick(position: Int)
    }
}
