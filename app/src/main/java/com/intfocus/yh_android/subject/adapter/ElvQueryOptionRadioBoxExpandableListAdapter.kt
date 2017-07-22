package com.intfocus.yh_android.subject.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.intfocus.yh_android.R
import com.intfocus.yh_android.util.DisplayUtil

/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/07/20 下午6:01
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class ElvQueryOptionRadioBoxExpandableListAdapter(mContext: Context, mType: Int) : BaseExpandableListAdapter() {
    val mContext = mContext
    var mGroupData: List<String>? = null
    var mChildData: List<String>? = null
    val mGroupViewText: TextView = TextView(mContext)
    var mType = mType
    var mClickedItemPos = 0
    var mCheckedItemsPos = mutableListOf<Int>()

    companion object {
        val RADIO_BOX_TYPE = 0
        val CHECK_BOX_TYPE = 1
    }

    /**
     * 得到当前position父项
     */
    override fun getGroup(groupPosition: Int): Any {
        return if (mGroupData == null) Unit else mGroupData!![0]
    }

    /**
     * 子项是否可选中
     */
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    /**
     * 按函数的名字来理解应该是是否具有稳定的id，这个方法目前一直都是返回false，没有去改动过
     */
    override fun hasStableIds(): Boolean {
        return false
    }

    /**
     * 得到父项的view
     */
    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        when (mType) {
            RADIO_BOX_TYPE -> {
                mGroupViewText.text = mGroupData!![groupPosition]
            }
            CHECK_BOX_TYPE -> {
                    mGroupViewText.text = ""
                if (mCheckedItemsPos.size > 0) {
                    val checkBoxText = StringBuilder()
                    for (mCheckedItemsPo in mCheckedItemsPos) {
                        checkBoxText.append(mChildData!![mCheckedItemsPo] + ",")
                    }
                    mGroupViewText.text = checkBoxText.subSequence(0, checkBoxText.lastIndex )
                }
            }
        }
        mGroupViewText.gravity = Gravity.CENTER_VERTICAL
        mGroupViewText.setPadding(DisplayUtil.dip2px(mContext, 36f), 0, 0, 0)
        mGroupViewText.height = DisplayUtil.dip2px(mContext, 40f)
        return mGroupViewText
    }

    /**
     * 得到子项数量
     */
    override fun getChildrenCount(groupPosition: Int): Int {
        return if (mGroupData == null) 0 else mChildData!!.size
    }

    /**
     * 得到子项
     */
    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return if (mGroupData == null) Unit else mChildData!![childPosition]
    }

    /**
     * 得到当前父项position
     */
    override fun getGroupId(groupPosition: Int): Long {
        return if (mGroupData == null) 0 else 1
    }

    /**
     * 得到子项View
     */
    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        when (mType) {
            RADIO_BOX_TYPE -> {
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_query_option_radiobox_drop_down, parent, false)
                }
                var textView = convertView!!.findViewById(R.id.tv_query_option_radio_box_drop_down_item) as TextView
                var imageView = convertView.findViewById(R.id.iv_query_option_radio_box_drop_down_item) as ImageView
                if (childPosition == mClickedItemPos) {
                    imageView.setImageDrawable(mContext.resources.getDrawable(R.drawable.icon_radio_button_pressed))
                } else {
                    imageView.setImageDrawable(mContext.resources.getDrawable(R.drawable.icon_radio_button))
                }
                textView.text = mChildData!![childPosition]
            }
            CHECK_BOX_TYPE -> {
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_query_option_radiobox_drop_down, parent, false)
                }
                var textView = convertView!!.findViewById(R.id.tv_query_option_radio_box_drop_down_item) as TextView
                var imageView = convertView.findViewById(R.id.iv_query_option_radio_box_drop_down_item) as ImageView
                if (mCheckedItemsPos.contains(childPosition)) {
                    imageView.setImageDrawable(mContext.resources.getDrawable(R.drawable.icon_check_box_pressed))
                } else {
                    imageView.setImageDrawable(mContext.resources.getDrawable(R.drawable.icon_check_box))
                }
                textView.text = mChildData!![childPosition]
            }
        }
        return convertView!!
    }

    /**
     * 得到当前子项position
     */
    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    /**
     * 获取父项数量
     */
    override fun getGroupCount(): Int {
        return if (mGroupData == null) 0 else 1
    }
}