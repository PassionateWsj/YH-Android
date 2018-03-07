package com.intfocus.template.subject.seven

import android.content.Context
import com.alibaba.fastjson.JSON
import com.intfocus.template.model.DaoUtil
import com.intfocus.template.model.callback.LoadDataCallback
import com.intfocus.template.model.entity.Report
import com.intfocus.template.model.gen.AttentionItemDao
import com.intfocus.template.model.gen.ConcernFilterBeanDao
import com.intfocus.template.model.response.attention.Test2
import com.intfocus.template.model.response.filter.MenuItem
import com.intfocus.template.subject.one.ModeImpl
import com.intfocus.template.subject.one.ModeModel
import com.intfocus.template.subject.one.entity.Filter
import com.intfocus.template.subject.seven.bean.ConcernComponentBean
import com.intfocus.template.subject.seven.bean.ConcernFilterBean
import com.intfocus.template.subject.seven.bean.ConcernFilterResponse
import com.intfocus.template.util.LoadAssetsJsonUtil
import com.intfocus.template.util.LogUtil
import com.intfocus.template.util.OKHttpUtils
import com.zbl.lib.baseframe.utils.TimeUtil
import okhttp3.Call
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 上午11:28
 * e-mail: PassionateWsj@outlook.com
 * name: 模板七 数据处理类
 * desc:
 * ****************************************************
 */
class MyConcernModelImpl : MyConcernModel<List<ConcernComponentBean.ConcernComponent>> {

    private val mDao = DaoUtil.getAttentionItemDao()

    /**
     * 默认只有一个页签
     */
    private val pageId = 0

    companion object {
        private val TAG = "MyConcernModelImpl"
        private var INSTANCE: MyConcernModelImpl? = null
        private var observable: Subscription? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): MyConcernModelImpl {
            return INSTANCE ?: MyConcernModelImpl()
                    .apply { INSTANCE = this }
        }

        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic
        fun destroyInstance() {
            unSubscribe()
            INSTANCE = null
        }

        /**
         * 取消订阅
         */
        private fun unSubscribe() {
            observable?.unsubscribe() ?: return
        }
    }

    /**
     * 获取筛选数据
     */
    override fun getFilterData(callback: LoadDataCallback<Filter>) {
        val uuid = "123123123-123-97-1232"
        val dao = DaoUtil.getConcernFilterBeanDao()
        OKHttpUtils.newInstance().getAsyncData("http://47.96.170.148:8081/saas-api/api/portal/custom?repCode=REP_000039&dataSourceCode=DATA_000007&roleId=123&groupId=97&userNum=123123123&reportId=1232",
                object : OKHttpUtils.OnResultListener {
                    override fun onFailure(call: Call?, e: IOException?) {

                    }

                    override fun onSuccess(call: Call?, response: String?) {
                        observable = Observable.just(response)
                                .subscribeOn(Schedulers.io())
                                .map {
                                    val filterList = JSON.parseObject(it, ConcernFilterResponse::class.java).data
                                    val filter = Filter()
                                    filterList?.let { data -> filter.data = findCurrentLevelList(data, 0) }

                                    if (filterList != null && filterList.isNotEmpty()) {
                                        dao.insertInTx(filterList)
                                    }
                                    var filterBean = ConcernFilterBean()
                                    var pid = 0
                                    var display = ""
                                    do {
                                        val filterList = getFilterListByPid(uuid, pid)
                                        if (filterList.isNotEmpty()) {
                                            filterBean = filterList[0]
                                            pid = filterBean.id
                                            display = "$display||${filterBean.obj_name}"
                                        }
                                    } while (filterList.isNotEmpty())
                                    filter.display = display.removePrefix("||")
                                    filter.default_id = filterBean.obj_num
                                    filter
                                }
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : Observer<Filter> {
                                    override fun onError(e: Throwable?) {
                                        LogUtil.d(this@MyConcernModelImpl, "数据处理错误 ::: " + e?.message)
                                    }

                                    override fun onNext(data: Filter?) {
                                        data?.let { callback.onSuccess(it) }
                                    }

                                    override fun onCompleted() {
                                        LogUtil.d(this@MyConcernModelImpl, "数据处理完成")
                                    }
                                })
                    }
                })

        // 获取关注列表数据并插入数据库
        getConcernListData()
    }

    /**
     * 递归生成层级结构的筛选数据类
     */
    private fun findCurrentLevelList(filterList: List<ConcernFilterBean>, pid: Int): ArrayList<MenuItem> {
        val data = ArrayList<MenuItem>()
        filterList.filter { it.pid == pid }
                .forEach {
                    val menuItem = MenuItem(it.obj_num, it.obj_name)
                    menuItem.data = findCurrentLevelList(filterList, it.id)
                    data.add(menuItem)
                }
        return data
    }

    /**
     * 根据 uuid 和 pid 查找筛选结果
     */
    private fun getFilterListByPid(uuid: String, pid: Int): List<ConcernFilterBean> {
        val queryBuilder = DaoUtil.getConcernFilterBeanDao().queryBuilder()
        return queryBuilder.where(
                queryBuilder.and(
                        ConcernFilterBeanDao.Properties.Uuid.eq(uuid),
                        ConcernFilterBeanDao.Properties.Pid.eq(pid)))
                .build().list()
    }

    /**
     * 获取关注列表数据并插入数据库
     */
    private fun getConcernListData() {
        val assetsJsonData = LoadAssetsJsonUtil.getAssetsJsonData("template7_main_attention_data.json")
        val data = JSON.parseObject(assetsJsonData, Test2::class.java)
        observable = Observable.just(data.data)
                .subscribeOn(Schedulers.io())
                .flatMap { it ->
                    mDao.deleteAll()
                    mDao.insertInTx(it?.attention_list)
                    Observable.from(data.data.attentioned_data)
                }
                .subscribe(object : Observer<Test2.DataBeanXX.AttentionedDataBean> {
                    override fun onError(e: Throwable?) {
                        LogUtil.d(this@MyConcernModelImpl, "数据库处理错误 ::: " + e?.message)
                    }

                    override fun onNext(it: Test2.DataBeanXX.AttentionedDataBean?) {
                        it?.let {
                            val attentionItem = mDao.queryBuilder().where(AttentionItemDao.Properties.Attention_item_id.eq(it.attention_item_id)).unique()
                            attentionItem.isAttentioned = true
                            mDao.update(attentionItem)
                        }
                    }

                    override fun onCompleted() {
                        LogUtil.d(this@MyConcernModelImpl, "数据库处理完成")
                    }
                })
    }

    /**
     * 获取控件列表
     */
    override fun getData(uuid: String, obj_num: String, callback: LoadDataCallback<List<ConcernComponentBean.ConcernComponent>>) {
        // todo 判断数据是否最新  是（不是）：数据库（网络） 获取
        OKHttpUtils.newInstance().getAsyncData("http://47.96.170.148:8081/saas-api/api/portal/custom?repCode=REP_000035&dataSourceCode=DATA_000007&templateId=7&objNum=$obj_num&roleId=123&groupId=97&userNum=123123123&reportId=1232",
                object : OKHttpUtils.OnResultListener {
                    override fun onFailure(call: Call?, e: IOException?) {

                    }

                    override fun onSuccess(call: Call?, response: String?) {
                        observable = Observable.just(response)
                                .subscribeOn(Schedulers.io())
                                .map {
                                    JSON.parseObject(it, ConcernComponentBean::class.java)
                                }
                                .subscribe(object : Observer<ConcernComponentBean> {
                                    override fun onError(e: Throwable?) {
                                        LogUtil.d(this@MyConcernModelImpl, "数据处理错误 ::: " + e?.message)
                                    }

                                    override fun onNext(data: ConcernComponentBean?) {
                                        data?.data?.let { callback.onSuccess(it) }
                                    }

                                    override fun onCompleted() {
                                        LogUtil.d(this@MyConcernModelImpl, "数据处理完成")
                                    }
                                })
                    }
                })
    }

    override fun getData(userNum: String, filterId: String, callback: MyConcernModel.LoadDataCallback) {
        val assetsJsonData = LoadAssetsJsonUtil.getAssetsJsonData("template7_main_attention_data.json")
        val data = JSON.parseObject(assetsJsonData, Test2::class.java)
        observable = Observable.just(data.data)
                .subscribeOn(Schedulers.io())
                .flatMap { it ->
                    mDao.deleteAll()
                    mDao.insertInTx(it?.attention_list)
                    Observable.from(data.data.attentioned_data)
                }
                .subscribe(object : Observer<Test2.DataBeanXX.AttentionedDataBean> {
                    override fun onError(e: Throwable?) {
                        LogUtil.d(this@MyConcernModelImpl, "数据库处理错误 ::: " + e?.message)
                    }

                    override fun onNext(it: Test2.DataBeanXX.AttentionedDataBean?) {
                        it?.let {
                            val attentionItem = mDao.queryBuilder().where(AttentionItemDao.Properties.Attention_item_id.eq(it.attention_item_id)).unique()
                            attentionItem.isAttentioned = true
                            mDao.update(attentionItem)
                        }
                    }

                    override fun onCompleted() {
                        LogUtil.d(this@MyConcernModelImpl, "数据库处理完成")
                    }
                })
        callback.onDataLoaded(data, data.data.filter)
    }

    override fun getData(ctx: Context, groupId: String, templateId: String, reportId: String, callback: MyConcernModel.LoadReportsDataCallback) {
        ModeImpl.getInstance().getData(reportId, templateId, groupId, object : ModeModel.LoadDataCallback {
            override fun onDataLoaded(reports: List<String>, filter: Filter) {
                callback.onFilterDataLoaded(filter)
                getData(reportId + templateId + groupId, pageId, callback)
            }

            override fun onDataNotAvailable(e: Throwable) {
            }
        })
    }

    fun getData(uuid: String, pageId: Int, callback: MyConcernModel.LoadReportsDataCallback) {
        LogUtil.d(TAG, "StartAnalysisTime:" + TimeUtil.getNowTime())
        observable = Observable.just("")
                .subscribeOn(Schedulers.io())
                .map {
                    ModeImpl.getInstance().queryPageData(uuid, pageId)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<Report>> {
                    override fun onError(e: Throwable?) {
                        e?.let {
                            callback.onDataNotAvailable(it)
                        }
                    }

                    override fun onNext(t: List<Report>?) {
                        t?.let {
                            callback.onReportsDataLoaded(it)
                        }
                    }

                    override fun onCompleted() {
                        LogUtil.d(TAG, "EndAnalysisTime:" + TimeUtil.getNowTime())
                    }

                })
    }
}