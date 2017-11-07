package com.intfocus.hx.business.dashboard.mine.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.intfocus.hx.R
import com.intfocus.hx.general.data.response.filter.MenuItem

/**
 * Created by CANC on 2017/8/8.
 * 筛选适配器
 */
class NewFilterAdapter(val context: Context,
                       var menuDatas: List<MenuItem>?,
                       var lisenter: FilterMenuListener) : RecyclerView.Adapter<NewFilterAdapter.FilterMenuHolder>() {
    var inflater = LayoutInflater.from(context)!!
    fun setData(data: List<MenuItem>?) {
        this.menuDatas = data
        notifyDataSetChanged()
    }

    fun update() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterMenuHolder {
        val contentView = inflater.inflate(R.layout.item_address, parent, false)
        return FilterMenuHolder(contentView)
    }

    override fun onBindViewHolder(holder: FilterMenuHolder, position: Int) {
        holder.contentView.setBackgroundResource(R.drawable.recycler_bg)
        if (menuDatas != null) {
            holder.itemAddressTv.text = menuDatas!![position].name
            holder.llItem.setOnClickListener {
                lisenter.itemClick(position)
            }
            holder.itemAddressImg.visibility = if (menuDatas!![position].arrorDirection) View.VISIBLE else View.GONE
        }
    }

    override fun getItemCount(): Int = if (menuDatas == null) 0 else menuDatas!!.size

    class FilterMenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var contentView = itemView
        var llItem: LinearLayout = itemView.findViewById(R.id.ll_item)
        var itemAddressTv: TextView = itemView.findViewById(R.id.item_address_tv)
        var itemAddressImg: ImageView = itemView.findViewById(R.id.item_address_img)
    }

    interface FilterMenuListener {
        fun itemClick(position: Int)
    }
}
