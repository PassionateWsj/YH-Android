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
import com.alibaba.fastjson.JSON
import com.intfocus.template.R
import com.intfocus.template.model.response.attention.Test2
import com.intfocus.template.subject.one.entity.SingleValue
import com.intfocus.template.subject.seven.indicatorgroup.IndicatorGroupAdapter
import com.intfocus.template.subject.seven.listener.EventRefreshIndicatorListItemData
import com.intfocus.template.subject.seven.listener.IndicatorListItemDataUpdateListener
import com.intfocus.template.ui.view.autofittextview.AutofitTextView
import com.intfocus.template.util.LoadAssetsJsonUtil
import com.intfocus.template.util.LogUtil
import com.intfocus.template.util.OKHttpUtils
import com.intfocus.template.util.RxBusUtil
import okhttp3.Call
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException


/**
 * ****************************************************
 * @author jameswong
 * created on: 17/12/18 下午5:35
 * e-mail: PassionateWsj@outlook.com
 * name: 模板七 - 关注 - 详情信息列表适配器
 * desc:
 * ****************************************************
 */
class IndicatorListAdapter(private val mCtx: Context, val fragment: Fragment, val data: List<Test2.DataBeanXX.AttentionedDataBean>) : BaseExpandableListAdapter(), IndicatorListItemDataUpdateListener {
    companion object {
        val CODE_EVENT_REFRESH_INDICATOR_LIST_ITEM_DATA = 1
    }

    val testApi = "https://api.douban.com/v2/book/search?q=%E7%BC%96%E7%A8%8B%E8%89%BA%E6%9C%AF"

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


    override fun getGroup(groupPosition: Int): Test2.DataBeanXX.AttentionedDataBean = data[groupPosition]

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
        getGroup(groupPosition).attention_item_name?.let { groupHolder.tvName?.text = it }
        getGroup(groupPosition).attention_item_id?.let { groupHolder.tvId?.text = it }
        RxBusUtil.getInstance().toObservable(EventRefreshIndicatorListItemData::class.java)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { event ->
                    LogUtil.d(this@IndicatorListAdapter, "groupPosition ::: " + groupPosition)
                    LogUtil.d(this@IndicatorListAdapter, "event.childPosition ::: " + event.childPosition)
                    firstUpdateData = false

                    val data = getChild(groupPosition, 0)[event.childPosition]
                    if (data.isReal_time) {
//            data.real_time_api?.let {
                        testApi.let {
                            OKHttpUtils.newInstance().getAsyncData(it, object : OKHttpUtils.OnReusltListener {
                                override fun onFailure(call: Call?, e: IOException?) {

                                }

                                override fun onSuccess(call: Call?, response: String?) {
                                    val itemData = JSON.parseObject(LoadAssetsJsonUtil.getAssetsJsonData(data.real_time_api), SingleValue::class.java)
                                    data.main_data = itemData.main_data
                                    data.sub_data = itemData.sub_data
                                    data.state = itemData.state
                                    data.isReal_time = false
                                    data.let {
                                        groupHolder.tvValue?.text = it.main_data.data
                                        groupHolder.tvValue?.setTextColor(coCursor[it.state.color % coCursor.size])
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            groupHolder.tvValue?.background = mCtx.getDrawable(bgList[it.state.color % coCursor.size])
                                        } else {
                                            groupHolder.tvValue?.background = mCtx.resources.getDrawable(bgList[it.state.color % coCursor.size])
                                        }
                                        currentItemDataIndex = event.childPosition
                                    }
                                }
                            })
                        }
                    } else {
                        data.let {
                            groupHolder.tvValue?.text = it.main_data.data
                            groupHolder.tvValue?.setTextColor(coCursor[it.state.color % coCursor.size])
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                groupHolder.tvValue?.background = mCtx.getDrawable(bgList[it.state.color % coCursor.size])
                            } else {
                                groupHolder.tvValue?.background = mCtx.resources.getDrawable(bgList[it.state.color % coCursor.size])
                            }
                            currentItemDataIndex = event.childPosition
                        }
                    }
                }
//        if (firstUpdateData) {
        val itemData = getChild(groupPosition, 0)[currentItemDataIndex]
        groupHolder.tvValue?.text = itemData.main_data.data
        groupHolder.tvValue?.setTextColor(coCursor[itemData.state.color % coCursor.size])
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            groupHolder.tvValue?.background = mCtx.getDrawable(bgList[itemData.state.color % coCursor.size])
        } else {
            groupHolder.tvValue?.background = mCtx.resources.getDrawable(bgList[itemData.state.color % coCursor.size])
        }
        groupHolder.tvValue?.setOnClickListener {
            currentItemDataIndex += 1
            RxBusUtil.getInstance().post(EventRefreshIndicatorListItemData(currentItemDataIndex % getChild(groupPosition, 0).size))
        }
//        }
        return view
    }

    override fun getChildrenCount(groupPosition: Int): Int = 1

    override fun getChild(groupPosition: Int, childPosition: Int): List<SingleValue> = getGroup(groupPosition).attention_item_data

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
        childHolder.rvIndicatorGroup!!.adapter = IndicatorGroupAdapter(mCtx, getChild(groupPosition, 0), CODE_EVENT_REFRESH_INDICATOR_LIST_ITEM_DATA)
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
