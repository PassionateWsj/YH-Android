package com.intfocus.yhdev.business.module.options

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.intfocus.yhdev.R
import java.util.ArrayList

/**
 * @author liuruilin
 * @data 2017/11/6
 * @describe
 */
    class SingleOptionsAdapter(var mContext: Context, var listener: OptionsSelectedListener): BaseAdapter() {
        private var items: MutableList<String>? = null

    override fun getCount(): Int {
        return if (items == null) 0 else items!!.size
    }

    override fun getItem(position: Int): Any? {
        return if (items == null) null else items!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var holder: SelectorListHolder? = null
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_options, parent, false)
            holder = SelectorListHolder(convertView)
            convertView!!.tag = holder
        } else {
            holder = convertView.tag as SelectorListHolder
        }

        holder.reportSelectorItem.text = items!![position]
        holder.reportSelectorItem.setOnClickListener{ listener.onItemSelected(items!![position])}
        return convertView
    }

    fun setData(data: List<String>) {
        if (items == null) {
            items = ArrayList()
        }
        items!!.clear()
        items!!.addAll(data)
        notifyDataSetChanged()
    }

    fun getSelectItem(pos: Int): String {
        return items!![pos]
    }

    internal inner class SelectorListHolder(convertView: View) {
        var reportSelectorItem: TextView = convertView.findViewById(R.id.tv_options_item)
    }

    interface OptionsSelectedListener {
        fun onItemSelected(value: String)
    }
}