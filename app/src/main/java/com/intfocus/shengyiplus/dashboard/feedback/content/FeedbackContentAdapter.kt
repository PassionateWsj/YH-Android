package com.intfocus.shengyiplus.dashboard.feedback.content

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.intfocus.shengyiplus.R
import com.intfocus.shengyiplus.model.response.mine_page.FeedbackList

/**
 * @author liuruilin
 * @data 2017/12/6
 * @describe
 */
class FeedbackContentAdapter(private val mContext: Context?, val listener: OnImageClickListener) : RecyclerView.Adapter<FeedbackContentAdapter.ImageDisplayHolder>() {
    private var data: List<String>? = null

    override fun getItemCount(): Int = if (null != data) { data!!.size } else 0

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ImageDisplayHolder {
        val inflate = LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false)
        return ImageDisplayHolder(inflate)
    }

    override fun onBindViewHolder(holder: ImageDisplayHolder?, position: Int) {
        holder!!.ivImageDelete.visibility = View.GONE
        holder.ivImageAdd.visibility = View.VISIBLE
        Glide.with(mContext)
                .load(data!![position])
                .placeholder(R.drawable.default_icon)
                .into(holder.ivImageAdd)
        holder.ivImageAdd.setOnClickListener { listener.onImageClick(data!![position]) }
    }

    fun setData(data: List<String>?) {
        this.data = data
        notifyDataSetChanged()
    }

    class ImageDisplayHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rlImageDisplayItem = itemView.findViewById<LinearLayout>(R.id.ll_image_display_item)
        var ivImageAdd = itemView.findViewById<ImageView>(R.id.iv_image_add)!!
        var ivImageDelete = itemView.findViewById<ImageView>(R.id.iv_image_delete)!!
    }

    interface OnImageClickListener {
        fun onImageClick(link: String)
    }
}
