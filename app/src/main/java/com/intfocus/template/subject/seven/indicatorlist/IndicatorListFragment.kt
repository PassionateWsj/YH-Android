package com.intfocus.template.subject.seven.indicatorlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.template.R
import com.intfocus.template.model.response.attention.Test2
import com.intfocus.template.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_indicator_list.*

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
        mData = arguments?.get("data") as ArrayList<Test2.DataBeanXX.AttentionedDataBean>
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_indicator_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        elv_indicator_list.setAdapter(IndicatorListAdapter(ctx, mData))
//        rv_indicator_list.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
//        rv_indicator_list.adapter = IndicatorListAdapter(ctx, mData)
    }
}