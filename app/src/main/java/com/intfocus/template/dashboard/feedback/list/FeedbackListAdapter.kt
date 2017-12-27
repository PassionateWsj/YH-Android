package com.intfocus.template.dashboard.feedback.list

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.intfocus.template.R
import com.intfocus.template.dashboard.feedback.EventFeedbackContent
import com.intfocus.template.model.response.mine_page.FeedbackList
import org.greenrobot.eventbus.EventBus

/**
 * @author liuruilin
 * @data 2017/12/5
 * @describe
 */
class FeedbackListAdapter(var context: Context?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mData: MutableList<FeedbackList.FeedbackListItemData>? = null

    override fun getItemCount(): Int = mData?.size ?: 0

    fun setData(data: List<FeedbackList.FeedbackListItemData>) {
        if (mData == null) {
            mData = mutableListOf()
        }
        mData?.clear()
        mData?.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val contentView = LayoutInflater.from(context).inflate(R.layout.item_feedback_list, parent, false)
        return FeedbackListViewHolder(contentView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder) {
            is FeedbackListViewHolder -> {
                mData?.let {
                    val itemData = it[position]
                    if (itemData.images != null && itemData.images!!.isNotEmpty()) {
                        holder.ivFeedbackListItem.visibility = View.VISIBLE
                        Glide.with(context)
                                .load(itemData.images!![0])
                                .placeholder(R.drawable.default_icon)
                                .into(holder.ivFeedbackListItem)
                    } else {
                        holder.ivFeedbackListItem.visibility = View.INVISIBLE
                    }

                    holder.tvFeedbackListItemContent.text = itemData.content
                    holder.tvFeedbackListItemTime.text = itemData.created_at
                    holder.itemView.setOnClickListener { EventBus.getDefault().post(EventFeedbackContent(itemData.id)) }
                }
            }
        }
    }

    class FeedbackListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var ivFeedbackListItem: ImageView = view.findViewById(R.id.iv_feedback_list)
        var tvFeedbackListItemContent: TextView = view.findViewById(R.id.tv_feedback_list_content)
        var tvFeedbackListItemTime: TextView = view.findViewById(R.id.tv_feedback_list_time)
    }
}
