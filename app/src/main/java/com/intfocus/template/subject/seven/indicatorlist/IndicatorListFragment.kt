package com.intfocus.template.subject.seven.indicatorlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.template.R
import com.intfocus.template.model.response.attention.Test2
import com.intfocus.template.subject.seven.listener.EventRefreshIndicatorListItemData
import com.intfocus.template.ui.BaseFragment
import com.intfocus.template.util.RxBusUtil
import kotlinx.android.synthetic.main.fragment_indicator_list.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 下午5:23
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class IndicatorListFragment : BaseFragment() {

    var mData: List<Test2.DataBeanXX.AttentionedDataBean> = ArrayList()

    fun newInstance(data: ArrayList<Test2.DataBeanXX.AttentionedDataBean>): IndicatorListFragment {
        val args = Bundle()
        val fragment = IndicatorListFragment()
        args.putSerializable("data", data)
        fragment.arguments = args
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mData = arguments?.getSerializable("data") as ArrayList<Test2.DataBeanXX.AttentionedDataBean>
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_indicator_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val indicatorListAdapter = IndicatorListAdapter(ctx, this, mData)
        elv_indicator_list.setAdapter(indicatorListAdapter)
        elv_indicator_list.setOnGroupExpandListener{ pos->
            (0 until indicatorListAdapter.groupCount)
                    .filter { pos != it }
                    .forEach { elv_indicator_list.collapseGroup(it) }
        }
        RxBusUtil.getInstance().toObservable(EventRefreshIndicatorListItemData::class.java)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { event ->
                    tv_indicator_list_single_value_title.text = mData[0].attention_item_data[event.childPosition].main_data.name
                }
    }

}