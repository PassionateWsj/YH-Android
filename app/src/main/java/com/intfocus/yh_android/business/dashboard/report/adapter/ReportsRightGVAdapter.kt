package com.intfocus.yh_android.business.dashboard.report.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.intfocus.yh_android.R
import com.intfocus.yh_android.business.dashboard.report.mode.GroupDataBean
import com.intfocus.yh_android.general.bean.DashboardItemBean
import com.intfocus.yh_android.general.util.DisplayUtil
import org.greenrobot.eventbus.EventBus

/**
 * Created by liuruilin on 2017/6/17.
 */
class ReportsRightGVAdapter(var ctx: Context, var datas: List<GroupDataBean>?) : BaseAdapter() {
    var mInflater: LayoutInflater = LayoutInflater.from(ctx)
    var laryoutParams = AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(ctx, 100f))

    override fun getCount(): Int = datas!!.size

    override fun getItem(position: Int): Any = datas!![position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, v: View?, parent: ViewGroup): View {
        var convertView = v
        val viewTag: ItemViewTag

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_reports_right_gv, null)

            viewTag = ItemViewTag(convertView!!.findViewById(R.id.ll_reports_right_item),
                    convertView.findViewById(R.id.iv_reports_item_img),
                    convertView.findViewById(R.id.tv_reports_item_name))
            convertView.tag = viewTag
            if (convertView.layoutParams == null)
                convertView.layoutParams = laryoutParams
            else
                convertView.layoutParams.height = DisplayUtil.dip2px(ctx, 100f)
            convertView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT

        } else {
            viewTag = convertView.tag as ItemViewTag
        }
        viewTag.mName.text = datas!![position].name
//        x.image().bind(viewTag.mIcon, datas!![position].icon_link)
        Glide.with(ctx)
                .load(datas!![position].icon_link)
                .into(viewTag.mIcon)

        viewTag.llItem.setOnClickListener {
            EventBus.getDefault().post(DashboardItemBean(datas!![position].obj_link!!, datas!![position].obj_title!!,
                    datas!![position].obj_id!!, datas!![position].template_id!!, "2"))
        }
        return convertView
    }

    internal inner class ItemViewTag(var llItem: LinearLayout, var mIcon: ImageView, var mName: TextView)

    interface ItemListener {
        fun reportItemClick(bannerName: String, link: String)
    }

    fun getItemBackground(position: Int, size: Int): Int {

        var background = 0
        if (size == 1) {
            background = R.drawable.btn_bg_ab
        }

        if (size == 2) {
            when (position) {
                0, 3, 6, 9, 12, 15, 18 -> background = R.drawable.btn_bg_a
                1, 4, 7, 10, 13, 16, 19 -> background = R.drawable.btn_bg_aaa
            }
        }

        if (size == 3) {
            when (position) {
                0, 3, 6, 9, 12, 15, 18 -> background = R.drawable.btn_bg_a
                1, 4, 7, 10, 13, 16, 19 -> background = R.drawable.btn_bg_aa
                2, 5, 8, 11, 14, 17, 20 -> background = R.drawable.btn_bg_aaa
            }
        }

        if (size > 3) {
            if (position < 3) {
                when (position) {
                    0, 3, 6, 9, 12, 15, 18 -> background = R.drawable.btn_bg_a
                    1, 4, 7, 10, 13, 16, 19 -> background = R.drawable.btn_bg_aa
                    2, 5, 8, 11, 14, 17, 20 -> background = R.drawable.btn_bg_aaa
                }
            } else {
                background = getItemBackground(position - 3, size - 3)
            }
        }

        return background
    }
}
