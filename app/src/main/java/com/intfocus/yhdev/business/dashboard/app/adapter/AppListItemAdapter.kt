package com.intfocus.yhdev.business.dashboard.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.intfocus.yhdev.R
import com.intfocus.yhdev.business.dashboard.report.mode.GroupDataBean
import com.intfocus.yhdev.general.bean.DashboardItemBean
import org.greenrobot.eventbus.EventBus

/**
 * Created by liuruilin on 2017/6/16.
 */
class AppListItemAdapter(var ctx: Context, var datas: List<GroupDataBean>?) : BaseAdapter() {
    var mInflater: LayoutInflater = LayoutInflater.from(ctx)

    override fun getCount(): Int = datas!!.size

    override fun getItem(position: Int): Any = datas!![position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, v: View?, parent: ViewGroup): View {
        var convertView = v
        val viewTag: AppListItemAdapter.ItemViewTag

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_app_list_gv, null)

            // construct an item tag
            viewTag = ItemViewTag(convertView!!.findViewById(R.id.ll_app_item) ,
                    convertView.findViewById(R.id.iv_app_item_img) ,
                    convertView.findViewById(R.id.tv_app_item_name))
            convertView.tag = viewTag
        } else {
            viewTag = convertView.tag as AppListItemAdapter.ItemViewTag
        }

        viewTag.mName.text = datas!![position].name
//        x.image().bind(viewTag.mIcon, datas!![position].icon_link)
        Glide.with(ctx)
                .load(datas!![position].icon_link)
                .into(viewTag.mIcon)
        viewTag.llItem.setOnClickListener {
            EventBus.getDefault().post(DashboardItemBean(datas!![position].obj_link!!, datas!![position].obj_title!!,
                    datas!![position].obj_id!!, datas!![position].template_id!!, "3"))
        }

        return convertView
    }

    internal inner class ItemViewTag(var llItem: LinearLayout, var mIcon: ImageView, var mName: TextView)

    interface ItemListener {
        fun itemClick(bannerName: String?, link: String?)
    }
}
