package com.intfocus.syp_template.business.dashboard.workbox

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.intfocus.syp_template.R
import com.intfocus.syp_template.general.bean.DashboardItemBean
import com.zbl.lib.baseframe.utils.PhoneUtil
import org.greenrobot.eventbus.EventBus

/**
 * Created by liuruilin on 2017/7/28.
 */
class WorkBoxAdapter(var ctx: Context, var datas: List<WorkBoxItem>?) : BaseAdapter() {
    var mInflater: LayoutInflater = LayoutInflater.from(ctx)
    var laryoutParams = AbsListView.LayoutParams(PhoneUtil.getScreenWidth(ctx) / 3, PhoneUtil.getScreenWidth(ctx) / 3)

    override fun getCount(): Int = datas!!.size

    override fun getItem(position: Int): Any = datas!![position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, v: View?, parent: ViewGroup): View {
        var convertView = v
        val viewTag: WorkBoxAdapter.ItemViewTag

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_work_box, null)

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
                .load(datas!![position].icon_link)
                .into(viewTag.mIcon)
        viewTag.rlItem.setOnClickListener {
            if (datas!![position].template_id != null) {
                EventBus.getDefault().post(DashboardItemBean(datas!![position].obj_link!!, datas!![position].obj_title!!,
                        datas!![position].obj_id!!, datas!![position].template_id!!, "3"))
            }
//            val testBean  = Gson().fromJson(ModeImpl.getInstance().getJsonData(ctx,"testworkbox.json"),WorkBoxItem::class.java)
//            EventBus.getDefault().post(DashboardItemBean(testBean.obj_link!!, testBean.obj_title!!,
//                    testBean.obj_id!!, testBean.template_id!!, "3"))

        }

        return convertView
    }

    internal inner class ItemViewTag(var rlItem: RelativeLayout, var mIcon: ImageView, var mName: TextView)
}
