package com.intfocus.template.subject.seven.indicatorlist

import android.content.Context
import android.os.Build
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.intfocus.template.R
import com.intfocus.template.model.response.attention.Test2
import com.intfocus.template.subject.seven.indicatorgroup.IndicatorGroupAdapter
import com.intfocus.template.subject.seven.listener.EventRefreshIndicatorListItemData
import com.intfocus.template.subject.seven.listener.IndicatorListItemDataUpdateListener
import com.intfocus.template.ui.view.autofittextview.AutofitTextView
import com.intfocus.template.util.LogUtil
import com.intfocus.template.util.RxBusUtil
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


/**
 * ****************************************************
 * @author jameswong
 * created on: 17/12/18 下午5:35
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class IndicatorListAdapter(private val mCtx: Context, val fragment: Fragment, val data: List<Test2.DataBeanXX.AttentionedDataBean>) : BaseExpandableListAdapter(), IndicatorListItemDataUpdateListener {
    companion object {
        val CODE_EVENT_REFRESH_INDICATOR_LIST_ITEM_DATA = 1
    }

    private val coCursor = mCtx.resources.getIntArray(R.array.co_cursor)!!
    private val bgList = mutableListOf(
            R.drawable.bg_radius_sold_red,
            R.drawable.bg_radius_sold_yellow,
            R.drawable.bg_radius_sold_green
    )

    private var currentItemDataIndex = 0
    private var firstUpdateData = true

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = false

    override fun hasStableIds(): Boolean = false

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getGroupCount(): Int = data.size


    override fun getGroup(groupPosition: Int): Any = data[groupPosition]

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        var view: View? = null
        var groupHolder: IndicatorGroupHolder? = null
        if (convertView != null) {
            view = convertView
            groupHolder = view.tag as IndicatorGroupHolder
        } else {
            view = LayoutInflater.from(mCtx).inflate(R.layout.item_indicator_list_group, parent, false)
            groupHolder = IndicatorGroupHolder()
            groupHolder.tvName = view!!.findViewById(R.id.tv_item_indicator_list_group_name)
            groupHolder.tvId = view.findViewById(R.id.tv_item_indicator_list_group_id)
            groupHolder.tvValue = view.findViewById(R.id.tv_item_indicator_list_group_value)
            view.tag = groupHolder
        }
        groupHolder.tvName!!.text = data[groupPosition].attention_item_name
        groupHolder.tvId!!.text = data[groupPosition].attention_item_id
        RxBusUtil.getInstance().toObservable(EventRefreshIndicatorListItemData::class.java)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { event ->
                    LogUtil.d(this@IndicatorListAdapter, "groupPosition ::: " + groupPosition)
                    LogUtil.d(this@IndicatorListAdapter, "event.childPosition ::: " + event.childPosition)
                    currentItemDataIndex = event.childPosition
                    firstUpdateData = false
                    groupHolder.tvValue!!.text = data[groupPosition].attention_item_data[event.childPosition].main_data.data
                    groupHolder.tvValue!!.setTextColor(coCursor[data[groupPosition].attention_item_data[event.childPosition].state.color % coCursor.size])
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        groupHolder.tvValue!!.background = mCtx.getDrawable(bgList[data[groupPosition].attention_item_data[event.childPosition].state.color % coCursor.size])
                    } else {
                        groupHolder.tvValue!!.background = mCtx.resources.getDrawable(bgList[data[groupPosition].attention_item_data[event.childPosition].state.color % coCursor.size])
                    }
                }
//        if (firstUpdateData) {
        groupHolder.tvValue!!.text = data[groupPosition].attention_item_data[currentItemDataIndex].main_data.data
        groupHolder.tvValue!!.setTextColor(coCursor[data[groupPosition].attention_item_data[currentItemDataIndex].state.color % coCursor.size])
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            groupHolder.tvValue!!.background = mCtx.getDrawable(bgList[data[groupPosition].attention_item_data[currentItemDataIndex].state.color % coCursor.size])
        } else {
            groupHolder.tvValue!!.background = mCtx.resources.getDrawable(bgList[data[groupPosition].attention_item_data[currentItemDataIndex].state.color % coCursor.size])
        }
        groupHolder.tvValue!!.setOnClickListener {
            currentItemDataIndex += 1
            RxBusUtil.getInstance().post(EventRefreshIndicatorListItemData(currentItemDataIndex % data[groupPosition].attention_item_data.size))
        }
//        }
        return view
    }

    override fun getChildrenCount(groupPosition: Int): Int = 1

    override fun getChild(groupPosition: Int, childPosition: Int): Any = data[groupPosition].attention_item_data

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        var view: View? = null
        var childHolder: IndicatorChildHolder? = null
        if (convertView != null) {
            view = convertView
            childHolder = view.tag as IndicatorChildHolder
        } else {
            view = LayoutInflater.from(mCtx).inflate(R.layout.item_indicator_list_child, parent, false)
            childHolder = IndicatorChildHolder()
            childHolder.rvIndicatorGroup = view!!.findViewById(R.id.rv_indicator_group)
            view.tag = childHolder
            childHolder.rvIndicatorGroup!!.isFocusable = false
        }

        childHolder.rvIndicatorGroup!!.layoutManager = LinearLayoutManager(mCtx, LinearLayoutManager.HORIZONTAL, false)
        childHolder.rvIndicatorGroup!!.adapter = IndicatorGroupAdapter(mCtx, data[groupPosition].attention_item_data, CODE_EVENT_REFRESH_INDICATOR_LIST_ITEM_DATA)
        return view
    }

    fun setData() {

    }

    override fun dataUpdated(pos: Int) {
    }

    class IndicatorGroupHolder {
        var tvName: TextView? = null
        var tvId: TextView? = null
        var tvValue: AutofitTextView? = null
    }

    class IndicatorChildHolder {
        var rvIndicatorGroup: RecyclerView? = null
    }

}
