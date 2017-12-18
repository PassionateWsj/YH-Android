package com.intfocus.template.subject.seven.indicatorgroup

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.template.R

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 下午5:35
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class IndicatorGroupAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return IndicatorGroupAdapter.IndicatorGroupViewHolder(LayoutInflater.from(context).inflate(R.layout.item_indicator_group, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun setData() {

    }

    /**
     * 轮播图
     */
    class IndicatorGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}