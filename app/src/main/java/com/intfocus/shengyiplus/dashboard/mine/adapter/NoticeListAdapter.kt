package com.intfocus.shengyiplus.dashboard.mine.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.intfocus.shengyiplus.R
import com.intfocus.shengyiplus.model.response.notice.Notice
import com.zzhoujay.richtext.RichText


/**
 * Created by liuruilin on 2017/6/12.
 */
class NoticeListAdapter(val context: Context,
                        private var noticeListDatas: List<Notice>?,
                        var listener: NoticeItemListener) : RecyclerView.Adapter<NoticeListAdapter.NoticeListHolder>() {

    var inflater = LayoutInflater.from(context)

    fun setData(data: List<Notice>?) {
        this.noticeListDatas = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeListHolder {
        val contentView = inflater.inflate(R.layout.item_notice_list, parent, false)
        return NoticeListHolder(contentView)
    }

    override fun onBindViewHolder(holder: NoticeListHolder, position: Int) {
        holder.viewTop.visibility = if (position == 0) View.VISIBLE else View.GONE
        if (noticeListDatas != null) {
            holder.tvNoticeType.text = "[" + getTypeStr(noticeListDatas!![position].type) + "]"
            holder.tvNoticeTitle.text = noticeListDatas!![position].title
            holder.tvNoticeTime.text = noticeListDatas!![position].time
            RichText.from(noticeListDatas!![position].abstracts).into(holder.tvNoticeListContent)
            holder.llNoticeListItem.setOnClickListener { listener.itemClick(noticeListDatas!![position].id) }
            holder.ivNoticePoint.visibility = if (noticeListDatas!![position].see) View.INVISIBLE else View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return if (noticeListDatas == null) 0 else noticeListDatas!!.size
    }

    class NoticeListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var viewTop = itemView.findViewById<View>(R.id.view_top)
        var ivNoticePoint = itemView.findViewById<ImageView>(R.id.iv_notice_point)
        var llNoticeListItem = itemView.findViewById<LinearLayout>(R.id.ll_notice_item)
        var tvNoticeType = itemView.findViewById<TextView>(R.id.tv_notice_type)
        var tvNoticeTitle = itemView.findViewById<TextView>(R.id.tv_notice_title)
        var tvNoticeTime = itemView.findViewById<TextView>(R.id.tv_notice_time)
        var tvNoticeListContent = itemView.findViewById<TextView>(R.id.tv_notice_list_content)
    }

    interface NoticeItemListener {
        fun itemClick(position: Int)
    }

    fun getTypeStr(type: Int): String {
        when (type) {
            0 -> return "系统公告"
            1 -> return "业务公告"
            2 -> return "预警体系"
            3 -> return "报表评论"
        }
        return "未知公告"
    }
}
