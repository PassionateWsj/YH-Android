package com.intfocus.hxtest.business.dashboard.workbox

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.intfocus.hxtest.R
import com.intfocus.hxtest.general.bean.DashboardItemBean
import com.zbl.lib.baseframe.utils.PhoneUtil
import org.greenrobot.eventbus.EventBus

/**
 * Created by liuruilin on 2017/7/28.
 */
class WorkBoxAdapter(var ctx: Context, val datas: List<WorkBoxItem>?) : BaseAdapter() {
    var mInflater: LayoutInflater = LayoutInflater.from(ctx)
    var laryoutParams = AbsListView.LayoutParams(PhoneUtil.getScreenWidth(ctx) / 3, PhoneUtil.getScreenWidth(ctx) / 3)

    override fun getCount(): Int = datas!!.size

    override fun getItem(position: Int): Any = datas!![position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, v: View?, parent: ViewGroup): View {
        var convertView = v
        val viewTag: WorkBoxAdapter.ItemViewTag

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_work_box, parent, false)

            // construct an item tag
            viewTag = ItemViewTag(convertView!!.findViewById(R.id.ll_work_box_item),
                    convertView.findViewById(R.id.iv_work_box_item_img),
                    convertView.findViewById(R.id.tv_work_box_item_name))
            convertView.tag = viewTag

            if (convertView.layoutParams == null)
                convertView.layoutParams = laryoutParams
            else
                convertView.layoutParams.height = PhoneUtil.getScreenWidth(ctx) / 3
            convertView.layoutParams.width = PhoneUtil.getScreenWidth(ctx) / 3

        } else {
            viewTag = convertView.tag as WorkBoxAdapter.ItemViewTag
        }

        viewTag.mName.text = datas!![position].name
//        x.image().bind(viewTag.mIcon, datas!![position].icon_link)
        Glide.with(ctx)
                .load(datas[position].icon_link)
                .into(viewTag.mIcon)
        viewTag.rlItem.setOnClickListener {
            EventBus.getDefault().post(DashboardItemBean(datas[position].obj_link ?: "KotlinNullPointerException", datas[position].obj_title ?: "KotlinNullPointerException",
                    datas[position].obj_id ?: "KotlinNullPointerException", datas[position].template_id ?: "-100", "3",datas[position].params_mapping!!))
        }

        return convertView
    }

    internal inner class ItemViewTag(var rlItem: RelativeLayout, var mIcon: ImageView, var mName: TextView)
}
