package com.intfocus.template.subject.seven.indicatorgroup

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.template.R
import com.intfocus.template.subject.one.entity.SingleValue
import com.intfocus.template.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_indicator_group.*

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 下午5:23
 * e-mail: PassionateWsj@outlook.com
 * name: 横向滑动单值组件页面
 * desc:
 * ****************************************************
 */
class IndicatorGroupFragment : BaseFragment() {

    var mData: List<SingleValue> = ArrayList()

    fun newInstance(data: ArrayList<SingleValue>): IndicatorGroupFragment {
        val args = Bundle()
        val fragment = IndicatorGroupFragment()
        args.putSerializable("data", data)
        fragment.arguments = args
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mData = arguments?.getSerializable("data") as ArrayList<SingleValue>
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_indicator_group, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_indicator_group.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        rv_indicator_group.adapter = IndicatorGroupAdapter(ctx, mData)
    }
}