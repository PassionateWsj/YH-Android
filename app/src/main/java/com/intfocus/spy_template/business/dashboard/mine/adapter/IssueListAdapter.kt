package com.intfocus.spy_template.business.dashboard.mine.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.intfocus.spy_template.R
import com.intfocus.spy_template.business.dashboard.mine.bean.IssueListDataBean

/**
 * Created by liuruilin on 2017/6/12.
 */
class IssueListAdapter(val context: Context,
                       private var issueListDatas: List<IssueListDataBean>?,
                       var listener: IssueItemListener) : RecyclerView.Adapter<IssueListAdapter.IssueListHolder>() {

    var inflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueListHolder {
        val contentView = inflater.inflate(R.layout.item_issue_list, parent, false)
        return IssueListHolder(contentView)
    }

    override fun onBindViewHolder(holder: IssueListHolder, position: Int) {
        holder.llIssueListItem.setOnClickListener { listener.itemClick(position) }
        holder.tvIssueTitle.text = issueListDatas!![position].content
        holder.tvIssueTime.text = issueListDatas!![position].time
        if (issueListDatas!![position].status == 0) {
            holder.ivIssuePoint.visibility = INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return if (issueListDatas == null) 0 else issueListDatas!!.size
    }

    class IssueListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var llIssueListItem = itemView.findViewById<LinearLayout>(R.id.ll_issue_list_item)
        var ivIssuePoint = itemView.findViewById<ImageView>(R.id.iv_issue_item_point)
        var tvIssueTitle = itemView.findViewById<TextView>(R.id.tv_issue_item_title)
        var tvIssueTime = itemView.findViewById<TextView>(R.id.tv_issue_item_time)
    }

    interface IssueItemListener {
        fun itemClick(position: Int)
    }
}
