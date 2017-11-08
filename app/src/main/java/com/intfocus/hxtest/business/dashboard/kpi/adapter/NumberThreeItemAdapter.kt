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
import com.zbl.lib.baseframe.utils.PhoneUtil
import org.greenrobot.eventbus.EventBus

/**
 * Created by liuruilin on 2017/7/10.
 */
class NumberThreeItemAdapter(var ctx: Context, private var itemDatas: List<KpiGroupItem>?) : RecyclerView.Adapter<NumberThreeItemAdapter.NumberThreeItemHolder>() {
    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    private var view2width: Int = 0
    private var inflater = LayoutInflater.from(ctx)
    private val colors = Colors.colorsRGY

    init {
        val sw = PhoneUtil.getScreenWidth(ctx)
        view2width = PhoneUtil.dip2px(ctx, 350.toFloat())
        viewWidth = sw / 2
        viewHeight = (viewWidth * 0.8).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberThreeItemHolder {
        val contentView = inflater.inflate(R.layout.fragment_number_three, parent, false)
        return NumberThreeItemHolder(contentView)
    }

    override fun onBindViewHolder(holder: NumberThreeItemHolder, position: Int) {
        holder.tvNumberThreeTitle.text = itemDatas!![position].title
        val mTypeface = Typeface.createFromAsset(ctx.assets, "ALTGOT2N.TTF")
        val number = itemDatas!![position].data!!.high_light!!.number
        holder.tvNumberThreeNumber.text = formatNumber(number)
        holder.tvNumberThreeUnit.text = itemDatas!![position].unit
        holder.tvNumberThreeCompare.text = itemDatas!![position].data!!.high_light!!.compare
        holder.tvNumberThreeCompare.typeface = mTypeface
        holder.tvNumberThreeCompare.setTextColor(colors[itemDatas!![position].data!!.high_light!!.arrow])
        holder.tvNumberThreeSub.text = itemDatas!![position].memo1
        holder.tvNumberThreeCompareText.text = itemDatas!![position].memo2
        holder.llNumberThreeItem.setOnClickListener {
            EventBus.getDefault().post(itemDatas!![position])
        }
    }

    override fun getItemViewType(position: Int): Int = position

    override fun getItemCount(): Int {
        if (itemDatas == null)
            return 0
        return itemDatas!!.size
    }

    inner class NumberThreeItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvNumberThreeTitle: TextView = view.findViewById(R.id.tv_number_three_title)
        var tvNumberThreeNumber: TextView = view.findViewById(R.id.tv_number_three_main)
        var tvNumberThreeUnit: TextView = view.findViewById(R.id.tv_number_three_unit)
        var tvNumberThreeCompare: TextView = view.findViewById(R.id.tv_number_three_compare)
        var tvNumberThreeSub: TextView = view.findViewById(R.id.tv_number_three_sub)
        var tvNumberThreeCompareText: TextView = view.findViewById(R.id.tv_number_three_compare_name)
        var llNumberThreeItem: LinearLayout = view.findViewById(R.id.ll_kpi_number_three)
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
