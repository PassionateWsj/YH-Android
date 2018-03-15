package com.intfocus.template.subject.seven.concernlist

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.intfocus.template.R
import com.intfocus.template.subject.seven.bean.ConcernListBean
import com.intfocus.template.subject.seven.listener.ConcernListItemClickListener
import java.util.*

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/21 下午4:14
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */

class ConcernListItemAdapter(private val mContext: Context, private val listener: ConcernListItemClickListener) : BaseAdapter() {
    private var items: MutableList<ConcernListBean>? = null

    override fun getCount(): Int = if (items == null) 0 else items!!.size

    override fun getItem(position: Int): Any? = if (items == null) null else items!![position]

    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var holder: ConcernListBeanListHolder? = null
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_attention, parent, false)
            holder = ConcernListBeanListHolder(convertView)
            convertView!!.tag = holder
        } else {
            holder = convertView.tag as ConcernListBeanListHolder
        }
        items?.let {
            holder.tvItemAttentionListName.text = items!![position].obj_name
            holder.tvItemAttentionListId.text = items!![position].obj_num
        }

        holder.cbItemAttentionListCheck.isChecked = items!![position].type==1
        holder.cbItemAttentionListCheck.setOnCheckedChangeListener { _, isChecked ->
            holder.cbItemAttentionListCheck.text = getText(isChecked)
        }
        holder.cbItemAttentionListCheck.text = getText(items!![position].type==1)
        holder.cbItemAttentionListCheck.setOnClickListener {
            listener.itemClick(position)
        }
        return convertView
    }

    fun getText(isChecked: Boolean): String = if (isChecked) {
        "取消关注"
    } else {
        "+ 关注"
    }


    fun setData(data: List<ConcernListBean>) {
        if (items == null) {
            items = ArrayList()
        }
        items!!.clear()
        items!!.addAll(data)
        notifyDataSetChanged()
    }

    fun clearData() {
        if (items == null) {
            items = ArrayList()
        }
        items!!.clear()
        notifyDataSetChanged()
    }

    fun getSelectItem(pos: Int): ConcernListBean = items!![pos]

    class ConcernListBeanListHolder(convertView: View) {
        val cbItemAttentionListCheck: CheckBox = convertView.findViewById(R.id.cb_item_attention_list_check)
        val tvItemAttentionListName: TextView = convertView.findViewById(R.id.tv_item_attention_list_name)
        val tvItemAttentionListId: TextView = convertView.findViewById(R.id.tv_item_attention_list_id)

    }
}

