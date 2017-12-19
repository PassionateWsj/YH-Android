package com.intfocus.template.subject.seven.indicatorlist

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.intfocus.template.R
import com.intfocus.template.model.response.attention.Test2
import com.intfocus.template.subject.one.entity.SingleValue
import com.intfocus.template.subject.seven.indicatorgroup.IndicatorGroupFragment
import java.util.*


/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 下午5:35
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class IndicatorListAdapter(val mCtx: Context, val data: List<Test2.DataBeanXX.AttentionedDataBean>) : BaseExpandableListAdapter() {

    val coCursor = mCtx.resources.getIntArray(R.array.co_cursor)

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = false

    override fun hasStableIds(): Boolean = true

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
            groupHolder.tvName = view!!.findViewById(R.id.tv_item_indicator_list_group_name) as TextView
            groupHolder.tvId = view.findViewById(R.id.tv_item_indicator_list_group_id) as TextView
            groupHolder.tvValue = view.findViewById(R.id.tv_item_indicator_list_group_value) as TextView
            view.tag = groupHolder
        }
        groupHolder.tvName!!.text = data[groupPosition].attention_item_name
        groupHolder.tvId!!.text = data[groupPosition].attention_item_id
        groupHolder.tvValue!!.text = data[groupPosition].attention_item_data[0].main_data.data
        groupHolder.tvValue!!.setTextColor(coCursor[data[groupPosition].attention_item_data[0].state.color % coCursor.size])
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
            childHolder.llContainer = view!!.findViewById(R.id.ll_item_indicator_list_child_container) as LinearLayout
            view.tag = childHolder
        }

        addItemView(IndicatorGroupFragment().newInstance(data[groupPosition].attention_item_data as ArrayList<SingleValue>), childHolder.llContainer!!)
        return View.inflate(mCtx, R.layout.item_indicator_list_child, parent)
    }

    private fun addItemView(fragment: Fragment, viewGroup: ViewGroup) {
        val layout = FrameLayout(mCtx)
        val params = AppBarLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        layout.layoutParams = params
        val id = Random().nextInt(Integer.MAX_VALUE)
        layout.id = id
        viewGroup.addView(layout)
        val ft = fragment.childFragmentManager.beginTransaction()
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.replace(layout.id, fragment)
        ft.commitNow()
    }

    fun setData() {

    }


    class IndicatorGroupHolder {
        var tvName: TextView? = null
        var tvId: TextView? = null
        var tvValue: TextView? = null
    }

    class IndicatorChildHolder {
        var llContainer: LinearLayout? = null
    }
}