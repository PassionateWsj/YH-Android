package com.intfocus.hxtest.business.dashboard.kpi.adapter

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.intfocus.hxtest.R
import com.intfocus.hxtest.business.dashboard.kpi.bean.KpiGroupItem
import com.intfocus.hxtest.general.constant.Colors
import org.greenrobot.eventbus.EventBus

/**
 * Created by liuruilin on 2017/7/10.
 */
class NumberTwoItemAdapter(var ctx: Context, internal var itemDatas: List<KpiGroupItem>?) : RecyclerView.Adapter<NumberTwoItemAdapter.NumberTwoItemHolder>() {
    var inflater = LayoutInflater.from(ctx)
    private val colors = Colors.colorsRGY

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberTwoItemHolder {
        val contentView = inflater.inflate(R.layout.fragment_number_two, parent, false)
        return NumberTwoItemHolder(contentView, viewType)
    }

    override fun onBindViewHolder(holder: NumberTwoItemHolder, position: Int) {
        holder.tvNumberTwoTitle.text = itemDatas!![position].title
        val number = itemDatas!![position].data!!.high_light!!.number
        val mTypeface = Typeface.createFromAsset(ctx.assets, "ALTGOT2N.TTF")
        holder.tvNumberTwoNumber.text = formatNumber(number)
        holder.tvNumberTwoNumber.setTextColor(colors[itemDatas!![position].data!!.high_light!!.arrow])
        holder.tvNumberTwoUnit.text = itemDatas!![position].unit
        holder.tvNumberTwoCompare.text = itemDatas!![position].data!!.high_light!!.compare
        holder.tvNumberTwoCompare.setTextColor(colors[itemDatas!![position].data!!.high_light!!.arrow])
        holder.tvNumberTwoSub.text = itemDatas!![position].memo1
        holder.tvNumberTwoNumber.typeface = mTypeface
        holder.tvNumberTwoCompare.typeface = mTypeface
        holder.llNumberTwoItem.setOnClickListener {
            EventBus.getDefault().post(itemDatas!![position])
        }
    }

    override fun getItemViewType(position: Int): Int = position

    override fun getItemCount(): Int {
        if (itemDatas == null)
            return 0
        return itemDatas!!.size
    }

    inner class NumberTwoItemHolder(view: View, viewType: Int) : RecyclerView.ViewHolder(view) {
        var tvNumberTwoTitle: TextView = view.findViewById(R.id.tv_number_two_title)
        var tvNumberTwoNumber: TextView = view.findViewById(R.id.tv_number_two_main)
        var tvNumberTwoUnit: TextView = view.findViewById(R.id.tv_number_two_unit)
        var tvNumberTwoCompare: TextView = view.findViewById(R.id.tv_number_two_compare)
        var tvNumberTwoSub: TextView = view.findViewById(R.id.tv_number_two_sub)
        var llNumberTwoItem: LinearLayout = view.findViewById(R.id.ll_number_two_item)
    }

    fun formatNumber(number: String): String {
        var mNumber = number
        if (mNumber.contains("")) {
            mNumber = mNumber.replace("0+?$".toRegex(), "")//去掉多余的0
            mNumber = mNumber.replace("[.]$".toRegex(), "")//如最后一位是.则去掉
        }
        return mNumber
    }
}
