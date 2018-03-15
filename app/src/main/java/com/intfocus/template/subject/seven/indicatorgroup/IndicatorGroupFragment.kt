package com.intfocus.template.subject.seven.indicatorgroup

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.fastjson.JSON
import com.intfocus.template.R
import com.intfocus.template.subject.seven.bean.ConcernGroupBean
import com.intfocus.template.ui.BaseFragment
import com.intfocus.template.util.OKHttpUtils
import kotlinx.android.synthetic.main.fragment_indicator_group.*
import okhttp3.Call
import java.io.IOException

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

    private var mData: List<ConcernGroupBean.ConcernGroup>? = null

    fun newInstance(reportId: String?,controlId: String?, repCode: String?): IndicatorGroupFragment {
        val args = Bundle()
        val fragment = IndicatorGroupFragment()
        args.putString("report_id", reportId)
        args.putString("control_id", controlId)
        args.putString("rep_code", repCode)
        fragment.arguments = args
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reportId = arguments?.getString("report_id")
        val controlId = arguments?.getString("control_id")
        val repCode = arguments?.getString("rep_code")

        val url = "http://shengyiplus.idata.mobi/saas-api/api/portal/custom?repCode=$repCode&dataSourceCode=DATA_000007&control_id=$controlId"
        OKHttpUtils.newInstance().getAsyncData(url,
                object : OKHttpUtils.OnResultListener {
                    override fun onSuccess(call: Call?, response: String?) {
                        mData = JSON.parseObject(
                                response
                                , ConcernGroupBean::class.java).data
                        mData?.let {
                            rv_indicator_group.adapter = IndicatorGroupAdapter(ctx, it)
                        }
                    }

                    override fun onFailure(call: Call?, e: IOException?) {
                    }
                })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_indicator_group, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_indicator_group.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)

    }
}