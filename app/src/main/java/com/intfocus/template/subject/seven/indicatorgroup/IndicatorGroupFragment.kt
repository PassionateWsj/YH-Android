package com.intfocus.template.subject.seven.indicatorgroup

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.fastjson.JSON
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

    private var mData: List<SingleValue>? = null

    fun newInstance(data: String): IndicatorGroupFragment {
        val args = Bundle()
        val fragment = IndicatorGroupFragment()
        args.putString("data", data)
        fragment.arguments = args
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mData = JSON.parseArray(JSON
                .parseObject(arguments?.getString("data"))
                .getJSONArray("main_concern_data")
                .toJSONString()
                , SingleValue::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_indicator_group, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_indicator_group.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        mData?.let {
            rv_indicator_group.adapter = IndicatorGroupAdapter(ctx, it)
        }
    }
}