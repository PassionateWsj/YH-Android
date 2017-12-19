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

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): IndicatorGroupViewHolder =
            IndicatorGroupAdapter.IndicatorGroupViewHolder(LayoutInflater.from(context).inflate(R.layout.item_indicator_group, parent, false))

    override fun onBindViewHolder(holder: IndicatorGroupViewHolder?, position: Int) {
        holder?.bindForecast(data[position], coCursor)
    }

    fun setData() {

    }


    /**
     * 轮播图
     */
    class IndicatorGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindForecast(data: SingleValue, coCursor: IntArray) {
            with(data) {
                val df = DecimalFormat("###,###.##")
                val state = data.state.color % coCursor.size
                val color = coCursor[state]

                itemView.tv_item_indicator_group_title.text = data.main_data.name
//                tvD2name.setText(data.sub_data.name)
                val mainValue = data.main_data.data.replace("%", "").toFloat()
                itemView.tv_item_indicator_group_main_data.text = df.format(mainValue.toDouble())
                itemView.tv_item_indicator_group_main_data.textSize = 36f

                val subData = data.sub_data.data.replace("%", "").toFloat()
                itemView.tv_item_indicator_group_sub_data.text = df.format(subData.toDouble())
                itemView.tv_item_indicator_group_sub_data.textSize = 10f

//                itemView.tv_item_indicator_group_main_data.setTextColor(color)
                itemView.tv_item_indicator_group_rate.setTextColor(color)
                val rate = (mainValue - subData) / subData
//                val diff = mainValue - subData
//                val diffValue = df.format(diff.toDouble())
                val diffRate = DecimalFormat(".##%").format(rate.toDouble())
                itemView.tv_item_indicator_group_rate.text = "" + diffRate

                adapterTextSize(itemView.tv_item_indicator_group_main_data)
                adapterTextSize(itemView.tv_item_indicator_group_sub_data)

                val absmv = Math.abs(rate)
                val isPlus: Boolean
                isPlus = if (absmv <= 0.1f) {
                    rate <= 0
                } else {
                    rate >= -0.1f
                }
                itemView.img_item_indicator_group_ratiocursor.setCursorState(state, isPlus)
                itemView.img_item_indicator_group_ratiocursor.isDrawingCacheEnabled = true
            }
        }

        private fun adapterTextSize(textView: TextView) {
            textView.post {
                val textViewWidth =textView.width
                do {
                    val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    textView.measure(spec, spec)
                    val textMeasuredWidth = textView.measuredWidth
                    if (textViewWidth < textMeasuredWidth) {
                        val textSize = (textView.textSize * 0.9).toFloat()
                        textView.textSize = DisplayUtil.px2sp(textView.context,textSize).toFloat()
                    }
                } while (textViewWidth < textMeasuredWidth)
            }
        }
    }
}