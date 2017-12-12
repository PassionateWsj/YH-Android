package com.intfocus.shengyiplus.dashboard.mine.adapter

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.intfocus.shengyiplus.R
import com.intfocus.shengyiplus.constant.Colors
import com.intfocus.shengyiplus.dashboard.kpi.bean.KpiGroupItem
import com.intfocus.shengyiplus.listener.NoDoubleClickListener
import com.intfocus.shengyiplus.model.entity.DashboardItem
import com.intfocus.shengyiplus.util.PageLinkManage


/**
 * @author CANC on 2017/7/27.
 */
class BusinessOverViewAdapter(val context: Context,
                              private var datas: List<KpiGroupItem>?) : RecyclerView.Adapter<BusinessOverViewAdapter.OperationalWarningHolder>() {

    private var inflater = LayoutInflater.from(context)
    private val colors = Colors.colorsRGY

    fun setData(data: List<KpiGroupItem>?) {
        this.datas = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationalWarningHolder {
        val contentView = inflater.inflate(R.layout.item_home_business_overview, parent, false)
        return OperationalWarningHolder(contentView)
    }

    override fun getItemCount(): Int = if (datas == null) 0 else datas!!.size

    override fun onBindViewHolder(holder: OperationalWarningHolder, position: Int) {
        val itemData = datas!![position]
        holder.tvNumberTitle.text = itemData.title
        val mTypeface = Typeface.createFromAsset(context.assets, "ALTGOT2N.TTF")
        val number = itemData.data!!.high_light!!.number + ""
        holder.tvNumberMain.text = formatNumber(number)
        holder.tvNumberUnit.text = itemData.unit
        holder.tvNumberCompare.typeface = mTypeface
        holder.tvNumberCompareT.typeface = mTypeface
        holder.tvNumberCompare.setTextColor(colors[itemData.data!!.high_light!!.arrow])
        holder.tvNumberCompareT.setTextColor(colors[itemData.data!!.high_light!!.arrow])
        if (itemData.data!!.high_light!!.compare.contains("%")) {
            holder.tvNumberCompare.text = itemData.data!!.high_light!!.compare.replace("%", "")
            holder.tvNumberCompareT.visibility = View.VISIBLE
        } else {
            holder.tvNumberCompare.text = itemData.data!!.high_light!!.compare
            holder.tvNumberCompareT.visibility = View.GONE
        }

        holder.tvNumberSub.text = itemData.memo1
        holder.tvNumberCompareText.text = itemData.memo2
        holder.rlBusinessOverview.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                PageLinkManage.pageLink(context, DashboardItem(itemData.obj_link!!, itemData.obj_title!!,
                        itemData.obj_id!!, itemData.template_id!!, "1"))
            }
        })
    }

    fun formatNumber(number: String): String {
        var mNumber = number
        if (mNumber.contains("")) {
            mNumber = mNumber.replace("0+?$".toRegex(), "")//去掉多余的0
            mNumber = mNumber.replace("[.]$".toRegex(), "")//如最后一位是.则去掉
        }
        return mNumber
    }

    class OperationalWarningHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvNumberTitle: TextView = itemView.findViewById(R.id.tv_number_title)
        var tvNumberMain: TextView = itemView.findViewById(R.id.tv_number_main)
        var tvNumberUnit: TextView = itemView.findViewById(R.id.tv_number_unit)
        var tvNumberCompare: TextView = itemView.findViewById(R.id.tv_number_compare)
        var tvNumberCompareT: TextView = itemView.findViewById(R.id.tv_number_compare_t)
        var tvNumberSub: TextView = itemView.findViewById(R.id.tv_number_sub)
        var tvNumberCompareText: TextView = itemView.findViewById(R.id.tv_number_compare_name)
        var rlBusinessOverview: RelativeLayout = itemView.findViewById(R.id.rl_business_overview)
    }
}
