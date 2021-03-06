package com.intfocus.template.dashboard.mine.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.intfocus.template.R
import com.intfocus.template.dashboard.mine.bean.InstituteDataBean


/**
 * Created by CANC on 2017/6/12.
 */
class InstituteAdapter(val context: Context,
                       private var instituteDatas: List<InstituteDataBean>?,
                       var listener: NoticeItemListener) : RecyclerView.Adapter<InstituteAdapter.InstituteHolder>() {

    var inflater = LayoutInflater.from(context)

    fun setData(data: List<InstituteDataBean>?) {
        this.instituteDatas = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstituteHolder {
        val contentView = inflater.inflate(R.layout.item_institute, parent, false)
        return InstituteHolder(contentView)
    }

    override fun onBindViewHolder(holder: InstituteHolder, position: Int) {
        if (instituteDatas != null) {
//            x.image().bind(holder.ivInstitute, "https://img6.bdstatic.com/img/image/smallpic/touxixiaoqinx.jpg")
            holder.ivInstitute.visibility = View.GONE
            holder.tvTitle.text = instituteDatas!![position].title
            var tagStr = instituteDatas!![position].tagInfo
            if (!TextUtils.isEmpty(tagStr)) {
                holder.tvTag.visibility = View.VISIBLE
                if (tagStr!!.contains(",")) {
                    holder.tvTag.text = tagStr.replace(",", "|")
                } else {
                    holder.tvTag.text = tagStr
                }
            } else {
                holder.tvTag.visibility = View.GONE
            }
            holder.llInstituteItem.setOnClickListener {
                listener.itemClick(instituteDatas!![position])
            }
            holder.ivCollection.setOnClickListener {
                listener.addCollection(instituteDatas!![position])
            }
            holder.ivCancel.setOnClickListener {
                listener.cancelCollection(instituteDatas!![position])
            }
            holder.ivCollection.visibility = if ("0".equals(instituteDatas!![position].favorite)) View.VISIBLE else View.GONE
            holder.ivCancel.visibility = if ("0".equals(instituteDatas!![position].favorite)) View.GONE else View.VISIBLE


        }
    }

    override fun getItemCount(): Int = if (instituteDatas == null) 0 else instituteDatas!!.size

    class InstituteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var llInstituteItem = itemView.findViewById<LinearLayout>(R.id.ll_institute_item)
        var ivInstitute = itemView.findViewById<ImageView>(R.id.iv_institute)
        var tvTitle = itemView.findViewById<TextView>(R.id.tv_title)
        var tvTag = itemView.findViewById<TextView>(R.id.tv_tag)
        var ivCollection = itemView.findViewById<ImageView>(R.id.iv_collection)
        var ivCancel = itemView.findViewById<ImageView>(R.id.iv_cancel)
    }

    interface NoticeItemListener {
        fun itemClick(instituteDataBean: InstituteDataBean)
        fun addCollection(instituteDataBean: InstituteDataBean)
        fun cancelCollection(instituteDataBean: InstituteDataBean)
    }
}
