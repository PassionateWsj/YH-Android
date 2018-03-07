package com.intfocus.template.subject.seven.indicatorlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.fastjson.JSON
import com.intfocus.template.R
import com.intfocus.template.subject.seven.bean.ConcernGroupBean
import com.intfocus.template.subject.seven.bean.ConcernItemsBean
import com.intfocus.template.subject.seven.listener.EventRefreshIndicatorListItemData
import com.intfocus.template.ui.BaseFragment
import com.intfocus.template.util.LoadAssetsJsonUtil
import com.intfocus.template.util.LogUtil
import com.intfocus.template.util.OKHttpUtils
import com.intfocus.template.util.RxBusUtil
import kotlinx.android.synthetic.main.fragment_indicator_list.*
import okhttp3.Call
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException


/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 下午5:23
 * e-mail: PassionateWsj@outlook.com
 * name: 关注详情列表
 * desc: 关注单品 可拓展详情信息列表
 * ****************************************************
 */
class IndicatorListFragment : BaseFragment(), IndicatorListContract.View {

    companion object {
        val CONFIG_JSON_REP = "concern_items_list_rep"
        val CONFIG_JSON_ID = "concern_items_list_id"
    }

    override lateinit var presenter: IndicatorListContract.Presenter

    lateinit var indicatorListAdapter: IndicatorListAdapter

    var mData: List<ConcernItemsBean.ConcernItem> = ArrayList()
    val testApi = "https://api.douban.com/v2/book/search?q=%E7%BC%96%E7%A8%8B%E8%89%BA%E6%9C%AF"
    var concern_items_list_rep = ""
    var concern_items_list_id = ""

    fun newInstance(control_id: String?, rep_code: String?): IndicatorListFragment {
        val args = Bundle()
        val fragment = IndicatorListFragment()
        args.putString(CONFIG_JSON_REP, rep_code)
        args.putString(CONFIG_JSON_ID, control_id)
        fragment.arguments = args
        IndicatorListPresenter(IndicatorListModelImpl.getInstance(), fragment)
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            concern_items_list_rep = it.getString(CONFIG_JSON_REP, "")
            concern_items_list_id = it.getString(CONFIG_JSON_ID, "")
        }
    }

    override fun showConcernedListData(data: List<ConcernItemsBean.ConcernItem>) {
        mData = data
        indicatorListAdapter.setGroupData(data)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_indicator_list, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRxBus()
        initAdapter()
        initData()
    }

    private fun initRxBus() {

        RxBusUtil.getInstance().toObservable(EventRefreshIndicatorListItemData::class.java)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<EventRefreshIndicatorListItemData>() {
                    override fun onCompleted() {

                    }

                    override fun onNext(event: EventRefreshIndicatorListItemData?) {
                        event?.let {
                            val data = mData[0].concern_item_group_list?.get(it.childPosition)
                            if (data?.real_time!!) {
//            data.real_time_api?.let {
                                testApi.let {
                                    OKHttpUtils.newInstance().getAsyncData(it, object : OKHttpUtils.OnResultListener {
                                        override fun onFailure(call: Call?, e: IOException?) {

                                        }

                                        override fun onSuccess(call: Call?, response: String?) {
                                            val itemData = JSON.parseObject(data.real_time_api?.let { it1 -> LoadAssetsJsonUtil.getAssetsJsonData(it1) }, ConcernGroupBean.ConcernGroup::class.java)
                                            data.main_data_data = itemData.main_data_data
                                            data.sub_data_data = itemData.sub_data_data
                                            data.state_color = itemData.state_color
                                            data.real_time = false
                                            tv_indicator_list_single_value_title.text = data.main_data_name
                                        }
                                    })
                                }
                            } else {
                                tv_indicator_list_single_value_title.text = data.main_data_name
                            }
                        }
                    }

                    override fun onError(e: Throwable?) {
                        e?.let {
                            LogUtil.d(this@IndicatorListFragment, it.message)
                        }
                    }
                })
    }

    private fun initData() {
        presenter.loadConcernedList(concern_items_list_id, concern_items_list_rep)

    }

    private fun initAdapter() {

        indicatorListAdapter = IndicatorListAdapter(ctx, this)
        elv_indicator_list.setAdapter(indicatorListAdapter)
        elv_indicator_list.setOnGroupExpandListener { pos ->
            (0 until indicatorListAdapter.groupCount)
                    .filter { pos != it }
                    .forEach { elv_indicator_list.collapseGroup(it) }
        }
        elv_indicator_list.setGroupIndicator(null)

    }

}