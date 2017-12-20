package com.intfocus.template.subject.seven.indicatorgroup

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.intfocus.template.R
import com.intfocus.template.subject.one.entity.SingleValue
import com.intfocus.template.util.DisplayUtil
import com.intfocus.template.util.LogUtil
import kotlinx.android.synthetic.main.item_indicator_group.view.*
import java.text.DecimalFormat


/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 下午5:35
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class IndicatorGroupAdapter(val context: Context, val data: List<SingleValue>) : RecyclerView.Adapter<IndicatorGroupAdapter.IndicatorGroupViewHolder>() {

    val coCursor = context.resources.getIntArray(R.array.co_cursor)
    var count = 0

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): IndicatorGroupViewHolder {
        LogUtil.d(this, "onBindViewHolder()方法被调用了" + ++count)
        return IndicatorGroupAdapter.IndicatorGroupViewHolder(LayoutInflater.from(context).inflate(R.layout.item_indicator_group, parent, false))
    }

    override fun onBindViewHolder(holder: IndicatorGroupViewHolder?, position: Int) {
        holder!!.itemView.tv_item_indicator_group_main_data.tag = position
        holder!!.itemView.tv_item_indicator_group_sub_data.tag = position

        val data = data[position]
        val df = DecimalFormat("###,###.##")
        val state = data.state.color % coCursor.size
        val color = coCursor[state]

        holder!!.itemView.tv_item_indicator_group_title.text = data.main_data.name
        val mainValue = data.main_data.data.replace("%", "").toFloat()
        holder.itemView.tv_item_indicator_group_main_data.text = df.format(mainValue.toDouble())
        holder.itemView.tv_item_indicator_group_main_data.textSize = 36f

        val subData = data.sub_data.data.replace("%", "").toFloat()
        holder.itemView.tv_item_indicator_group_sub_data.text = df.format(subData.toDouble())
        holder.itemView.tv_item_indicator_group_sub_data.textSize = 10f

        holder.itemView.tv_item_indicator_group_rate.setTextColor(color)
        val diffRate = DecimalFormat(".##%").format(((mainValue - subData) / subData).toDouble())
        holder.itemView.tv_item_indicator_group_rate.text = diffRate

        adapterTextSize(holder.itemView.tv_item_indicator_group_main_data, position)
        adapterTextSize(holder.itemView.tv_item_indicator_group_sub_data, position)

        holder.itemView.img_item_indicator_group_ratiocursor.setCursorState(data.state.color, false)
        holder.itemView.img_item_indicator_group_ratiocursor.isDrawingCacheEnabled = true

        holder.itemView.rl_item_indicator_group_container.setOnClickListener {
            LogUtil.d(this, "pos ::: " + position)
            LogUtil.d(this, "itemView.id ::: " + holder.itemView.id)
            LogUtil.d(this, "state ::: " + state)
            LogUtil.d(this, "color ::: " + data.state.color)
            LogUtil.d(this, "main_data ::: " + holder.itemView.tv_item_indicator_group_main_data.text)
            LogUtil.d(this, "sub_data ::: " + holder.itemView.tv_item_indicator_group_sub_data.text)
            LogUtil.d(this, "rate ::: " + holder.itemView.tv_item_indicator_group_rate.text)
            LogUtil.d(this, "main_data.width ::: " + holder.itemView.tv_item_indicator_group_main_data.width)
            val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            holder.itemView.tv_item_indicator_group_main_data.measure(spec, spec)
            LogUtil.d(this, "main_data.text.width ::: " + holder.itemView.tv_item_indicator_group_main_data.measuredWidth)

        }
        LogUtil.d(this, "onBindViewHolder()方法被调用了" + ++count)
    }

    private fun adapterTextSize(textView: TextView, position: Int) {
        textView.post {
            val textViewWidth = textView.width
            do {
                val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                textView.measure(spec, spec)
                val textMeasuredWidth = textView.measuredWidth
                if (textViewWidth < textMeasuredWidth && textView.tag == position) {
                    val textSize = (textView.textSize * 0.8).toFloat()
                    textView.textSize = DisplayUtil.px2sp(textView.context, textSize).toFloat()
                }
            } while (textViewWidth in 1..(textMeasuredWidth - 1))
        }
    }

    fun setData() {

    }

    /**
     * 轮播图
     */
    class IndicatorGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}