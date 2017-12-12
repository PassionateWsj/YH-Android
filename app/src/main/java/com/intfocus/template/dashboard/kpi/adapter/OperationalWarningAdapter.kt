package com.intfocus.template.dashboard.mine.adapter

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.intfocus.template.R
import com.intfocus.template.constant.Colors
import com.intfocus.template.dashboard.kpi.bean.KpiGroupItem
import com.intfocus.template.listener.NoDoubleClickListener
import com.intfocus.template.model.entity.DashboardItem
import com.intfocus.template.util.PageLinkManage
import com.intfocus.template.util.Utils


/**
 * Created by CANC on 2017/7/27.
 */
class OperationalWarningAdapter(val context: Context,
                                private var datas: List<KpiGroupItem>?) : RecyclerView.Adapter<OperationalWarningAdapter.OperationalWarningHolder>() {

    var inflater = LayoutInflater.from(context)
    private val colors = Colors.colorsRGY

    fun setData(data: List<KpiGroupItem>?) {
        this.datas = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationalWarningHolder {
        val contentView = inflater.inflate(R.layout.item_home_operational_waring, parent, false)
        return OperationalWarningHolder(contentView)
    }

    override fun getItemCount(): Int = if (datas == null) 0 else datas!!.size

    override fun onBindViewHolder(holder: OperationalWarningHolder, position: Int) {
        val itemData = datas!![position]
        holder.viewEmpty.visibility = if (0 == position) View.VISIBLE else View.GONE
        holder.rlNumberItem.layoutParams.width = ((Utils.getScreenWidth(context) - 20) / (2.5)).toInt()
        holder.tvNumberTitle.text = itemData.title
        val number = itemData.data!!.high_light!!.number + ""
        val mTypeface = Typeface.createFromAsset(context.assets, "ALTGOT2N.TTF")

        if (!number.equals("null")) {
            holder.tvNumberMain.text = formatNumber(number)
        }

        holder.tvNumberMain.setTextColor(colors[itemData.data!!.high_light!!.arrow])
        holder.tvNumberUnit.text = itemData.unit
        holder.tvNnumberCompare.text = itemData.data!!.high_light!!.compare
        holder.tvNnumberCompare.setTextColor(colors[itemData.data!!.high_light!!.arrow])
        holder.tvNumberSub.text = itemData.memo1
        holder.tvNumberMain.typeface = mTypeface
        holder.tvNnumberCompare.typeface = mTypeface
        holder.rlNumberItem.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                PageLinkManage.pageLink(context, DashboardItem(itemData.obj_link!!, itemData.obj_title!!,
                        itemData.obj_id!!, itemData.template_id!!, "1"))
            }
        })
    }

    fun formatNumber(number: String): String {
        var numberStr = number
        if (TextUtils.isEmpty(numberStr)) {
            return ""
        } else {
            if (numberStr.contains("")) {
                numberStr = numberStr.replace("0+?$".toRegex(), "")//去掉多余的0
                numberStr = numberStr.replace("[.]$".toRegex(), "")//如最后一位是.则去掉
            }
            return numberStr
        }
    }

    class OperationalWarningHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rlNumberItem = itemView.findViewById<RelativeLayout>(R.id.rl_number_item)
        var viewEmpty = itemView.findViewById<View>(R.id.view_empty)
        var tvNumberMain = itemView.findViewById<TextView>(R.id.tv_number_main)
        var tvNumberUnit = itemView.findViewById<TextView>(R.id.tv_number_unit)
        var tvNnumberCompare = itemView.findViewById<TextView>(R.id.tv_number_compare)
        var tvNumberTitle = itemView.findViewById<TextView>(R.id.tv_number_title)
        var tvNumberSub = itemView.findViewById<TextView>(R.id.tv_number_sub)
    }
}