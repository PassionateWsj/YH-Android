package com.intfocus.syp_template.module.image

import android.content.Context
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.intfocus.syp_template.R
import com.intfocus.syp_template.general.util.ImageUtil
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File

/**
 * @author liuruilin
 * @data 2017/11/3
 * @describe
 */
class ImageDisplayAdapter(private val mContext: Context, private val listener: ImageItemClickListener, private val limit: Int): RecyclerView.Adapter<ImageDisplayAdapter.ImageDisplayHolder>() {
    var mData = mutableListOf<Uri>()

    override fun getItemCount(): Int = mData.size + 1

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ImageDisplayHolder {
        val inflate = LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false)

        return ImageDisplayHolder(inflate)
    }

    override fun onBindViewHolder(holder: ImageDisplayHolder?, position: Int) {
        when {
            itemCount == limit + 1 && position == limit -> {
                setAddImageDisplayView(holder)
                holder!!.rlImageDisplayItem.visibility = View.GONE
            }

            position == itemCount - 1 -> {
                setAddImageDisplayView(holder)
            }

            else -> {
                Luban.with(mContext)
                        .load(File(ImageUtil.handleImageOnKitKat(mData[position], mContext))) //传人要压缩的图片
                        .setCompressListener(object : OnCompressListener {
                            override fun onSuccess(p0: File?) {
                                Glide.with(mContext).load(p0).into(holder!!.ivImageAdd)
                                holder.ivImageDelete.visibility = View.VISIBLE
                                holder.ivImageDelete.setOnClickListener {
                                    listener.deleteImage(position)
                                }
                            }

                            override fun onError(p0: Throwable?) {}

                            override fun onStart() {}

                        }).launch()    //启动压缩
                }
        }
    }

    fun setData(data: List<Uri>?) {
        mData.addAll(data!!)
        notifyDataSetChanged()
    }

    fun getData(): List<Uri> = mData

    fun deleteImageWithPos(mPos: Int) {
        mData.removeAt(mPos)
        notifyDataSetChanged()
    }

    private fun setAddImageDisplayView(holder: ImageDisplayHolder?) {
        holder!!.rlImageDisplayItem.visibility = View.VISIBLE

        holder.ivImageAdd.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.btn_addpic))
        holder.ivImageDelete.visibility = View.GONE
        holder.ivImageAdd.setOnClickListener { listener.addImage(limit + 1 - itemCount) }
    }

    class ImageDisplayHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rlImageDisplayItem = itemView.findViewById<LinearLayout>(R.id.ll_image_display_item)
        var ivImageAdd = itemView.findViewById<ImageView>(R.id.iv_image_add)
        var ivImageDelete = itemView.findViewById<ImageView>(R.id.iv_image_delete)
    }

    interface ImageItemClickListener {
        fun addImage(maxNum: Int)
        fun deleteImage(pos: Int)
    }
}
