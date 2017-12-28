package com.intfocus.template.subject.seven.indicatorlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.fastjson.JSON
import com.intfocus.template.R
import com.intfocus.template.model.response.attention.ConcernedListData
import com.intfocus.template.model.response.attention.Test2
import com.intfocus.template.subject.one.entity.SingleValue
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
    override lateinit var presenter: IndicatorListContract.Presenter

    companion object {
        val CONFIG_JSON_DATA = "data"
    }

    var mData: List<Test2.DataBeanXX.AttentionedDataBean> = ArrayList()
    val testApi = "https://api.douban.com/v2/book/search?q=%E7%BC%96%E7%A8%8B%E8%89%BA%E6%9C%AF"

    fun newInstance(): IndicatorListFragment {
//        val args = Bundle()
        val fragment = IndicatorListFragment()
//        args.putString(CONFIG_JSON_DATA, data)
//        fragment.arguments = args
        IndicatorListPresenter(IndicatorListModelImpl.getInstance(), fragment)
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun showConcernedListData(data: ConcernedListData) {

    }

    override fun updateConcernedListTitle(title: String) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_indicator_list, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.loadConcernedList()
        val indicatorListAdapter = IndicatorListAdapter(ctx, this, mData)
        elv_indicator_list.setAdapter(indicatorListAdapter)
        elv_indicator_list.setOnGroupExpandListener { pos ->
            (0 until indicatorListAdapter.groupCount)
                    .filter { pos != it }
                    .forEach { elv_indicator_list.collapseGroup(it) }
        }
        elv_indicator_list.setGroupIndicator(null)
        RxBusUtil.getInstance().toObservable(EventRefreshIndicatorListItemData::class.java)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<EventRefreshIndicatorListItemData>() {
                    override fun onCompleted() {

                    }

                    override fun onNext(event: EventRefreshIndicatorListItemData?) {
                        event?.let {
                            val data = mData[0].attention_item_data[it.childPosition]
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
                                            tv_indicator_list_single_value_title.text = data.main_data.name
                                        }
                                    })
                                }
                            } else {
                                tv_indicator_list_single_value_title.text = data.main_data.name
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

}