package com.intfocus.template.subject.nine.collectionlist.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.intfocus.template.R
import com.intfocus.template.model.entity.Collection
import com.intfocus.template.util.TimeUtils

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/29 下午0:01
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class CollectionListAdapter(private val mCtx: Context) : RecyclerView.Adapter<CollectionListAdapter.ViewHolder>() {

    private val mData: MutableList<Collection> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.item_collection_list, parent, false))

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder?.let {
                val itemData = mData[position]
                holder.tv_item_collection_list_status.text = when {
                    itemData.status == 1 -> ""
                    itemData.status == 0 -> "上传中"
                    else -> "草稿"
                }
                itemData.h1?.let {
                    holder.tv_item_collection_list_title.text = it
                }
                itemData.h2?.let {
                    holder.tv_item_collection_list_content.text = it
                }
                itemData.h3?.let {
                    holder.tv_item_collection_list_title_label.text = it
                }
                itemData.updated_at?.let {
                    holder.tv_item_collection_list_time.text = TimeUtils.getStandardDate(it)
                }
        }
    }

    override fun getItemCount(): Int =  mData.size

    fun setData(data: List<Collection>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_item_collection_list_status: TextView = itemView.findViewById(R.id.tv_item_collection_list_status)
        val tv_item_collection_list_title_label: TextView = itemView.findViewById(R.id.tv_item_collection_list_title_label)
        val tv_item_collection_list_title: TextView = itemView.findViewById(R.id.tv_item_collection_list_title)
        val tv_item_collection_list_time: TextView = itemView.findViewById(R.id.tv_item_collection_list_time)
        val tv_item_collection_list_content: TextView = itemView.findViewById(R.id.tv_item_collection_list_content)
    }
}