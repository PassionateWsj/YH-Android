package com.intfocus.template.dashboard.workbox

import android.content.Context
import android.content.res.Configuration
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.intfocus.template.BuildConfig
import com.intfocus.template.ConfigConstants
import com.intfocus.template.R
import com.intfocus.template.listener.NoDoubleClickListener
import com.intfocus.template.model.entity.DashboardItem
import com.intfocus.template.util.LogUtil
import com.intfocus.template.util.PageLinkManage
import com.zbl.lib.baseframe.utils.PhoneUtil

/**
 * Created by liuruilin on 2017/7/28.
 */
class WorkBoxAdapter(var ctx: Context, val datas: List<WorkBoxItem>?) : BaseAdapter() {
    var mInflater: LayoutInflater = LayoutInflater.from(ctx)
    var itemCount = if (ctx.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        ConfigConstants.WORK_BOX_NUM_COLUMNS_LAND
    } else {
        ConfigConstants.WORK_BOX_NUM_COLUMNS_PORT
    }
    var laryoutParams = AbsListView.LayoutParams(PhoneUtil.getScreenWidth(ctx) / itemCount, PhoneUtil.getScreenWidth(ctx) / itemCount)

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
                convertView.layoutParams.height = PhoneUtil.getScreenWidth(ctx) / itemCount
            convertView.layoutParams.width = PhoneUtil.getScreenWidth(ctx) / itemCount
        } else {
            viewTag = convertView.tag as WorkBoxAdapter.ItemViewTag
        }
        viewTag.mName.text = datas!![position].name
        Glide.with(ctx)
                .load(datas[position].icon_link)
                .placeholder(R.drawable.default_icon)
                .error(R.drawable.default_icon)
                .into(viewTag.mIcon)
        viewTag.rlItem.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                PageLinkManage.pageLink(ctx, DashboardItem(datas[position].obj_link ?: "KotlinNullPointerException", datas[position].obj_title ?: "KotlinNullPointerException",
                        datas[position].obj_id ?: "KotlinNullPointerException", datas[position].template_id ?: "-100", "3", datas[position].params_mapping ?: HashMap()))
            }
        })
        viewTag.rlItem.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
                PageLinkManage.pageLink(ctx, DashboardItem(datas[position].obj_link ?: "KotlinNullPointerException", datas[position].obj_title ?: "KotlinNullPointerException",
                        datas[position].obj_id ?: "KotlinNullPointerException", datas[position].template_id ?: "-100", "3", datas[position].params_mapping ?: HashMap()))
                return@setOnKeyListener true
            }
            false
        }
        if ("template" == BuildConfig.FLAVOR || "baozhentv" == BuildConfig.FLAVOR) {
            viewTag.rlItem.requestFocus()
        }
        LogUtil.d(this, "pos :" + position + " hasFocus :" + viewTag.rlItem.hasFocus())
        return convertView
    }

    internal inner class ItemViewTag(var rlItem: RelativeLayout, var mIcon: ImageView, var mName: TextView)
}
