package com.intfocus.template.subject.seven

import android.content.Context
import com.alibaba.fastjson.JSON
import com.intfocus.template.SYPApplication
import com.intfocus.template.constant.Params
import com.intfocus.template.general.net.ApiException
import com.intfocus.template.general.net.CodeHandledSubscriber
import com.intfocus.template.general.net.RetrofitUtil
import com.intfocus.template.general.net.SaaSRetrofitUtil
import com.intfocus.template.model.DaoUtil
import com.intfocus.template.model.callback.LoadDataCallback
import com.intfocus.template.model.gen.ConcernFilterBeanDao
import com.intfocus.template.model.gen.ConcernListBeanDao
import com.intfocus.template.model.response.attention.Test2
import com.intfocus.template.model.response.filter.MenuItem
import com.intfocus.template.subject.one.entity.Filter
import com.intfocus.template.subject.seven.bean.ConcernComponentBean
import com.intfocus.template.subject.seven.bean.ConcernFilterBean
import com.intfocus.template.subject.seven.bean.ConcernFilterResponse
import com.intfocus.template.util.LoadAssetsJsonUtil
import com.intfocus.template.util.LogUtil
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

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

    private val mDao = DaoUtil.getConcernListBeanDao()

    private var mUuid = ""

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
    override fun getFilterData(repCode: String, reportId: String, callback: LoadDataCallback<Filter>) {
        val mUserSP = SYPApplication.globalContext.getSharedPreferences(Params.USER_BEAN, Context.MODE_PRIVATE)
        val userNum = mUserSP.getString(Params.USER_NUM, "0")
        val roleId = mUserSP.getString(Params.ROLE_ID, "0")
        val groupId = mUserSP.getString(Params.GROUP_ID, "0")
        mUuid = String.format("%s-%s-%s-%s", userNum, roleId, groupId, reportId)


        SaaSRetrofitUtil.getHttpServiceKotlin(SYPApplication.globalContext)
                .getConcernFilterData(repCode, reportId)
                .compose(RetrofitUtil.CommonOptions<ConcernFilterResponse>())
                .subscribe(object : CodeHandledSubscriber<ConcernFilterResponse>() {
                    override fun onError(apiException: ApiException?) {
                        apiException?.let { callback.onError(Exception(it.displayMessage)) }
                    }

                    override fun onCompleted() {
                    }

                    override fun onBusinessNext(data: ConcernFilterResponse?) {
                        if (data?.data != null) {
                            observable = Observable.just(data.data)
                                    .subscribeOn(Schedulers.io())
                                    .map {
                                        val filter = Filter()
                                        it?.let { data -> filter.data = findCurrentLevelList(data, 0) }

                                        if (it != null && it.isNotEmpty()) {
                                            DaoUtil.getConcernFilterBeanDao().insertInTx(it)
                                        }
                                        var filterBean = ConcernFilterBean()
                                        var pid = 0
                                        var display = ""
                                        do {
                                            val filterList = getFilterListByPid(mUuid, pid)
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
                                            e?.let {
                                                callback.onError(Exception(it.message))
                                                LogUtil.d(this@MyConcernModelImpl, "数据处理错误 ::: " + e?.message)
                                            }
                                        }

                                        override fun onNext(data: Filter?) {
                                            data?.let { callback.onSuccess(it) }
                                        }

                                        override fun onCompleted() {
                                            LogUtil.d(this@MyConcernModelImpl, "数据处理完成")
                                        }
                                    })
                        }
                    }

                })

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
     * 获取控件列表
     */
    override fun getData(reportId: String, objNum: String, callback: LoadDataCallback<List<ConcernComponentBean.ConcernComponent>>) {
        // todo 判断数据是否最新  是（不是）：数据库（网络） 获取
        SaaSRetrofitUtil.getHttpServiceKotlin(SYPApplication.globalContext)
                .getConcernComponentData(reportId, objNum)
                .compose(RetrofitUtil.CommonOptions<ConcernComponentBean>())
                .subscribe(object : CodeHandledSubscriber<ConcernComponentBean>() {
                    override fun onCompleted() {
                        LogUtil.d(this@MyConcernModelImpl, "数据处理完成")
                    }

                    override fun onError(apiException: ApiException?) {
                        apiException?.let {
                            callback.onError(Exception(it.displayMessage))
                            LogUtil.d(this@MyConcernModelImpl, "数据处理错误 ::: " + apiException?.displayMessage)
                        }
                    }

                    override fun onBusinessNext(data: ConcernComponentBean?) {
                        data?.data?.let { callback.onSuccess(it) }
                    }

                })
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
                            val attentionItem = mDao.queryBuilder().where(ConcernListBeanDao.Properties.Obj_id.eq(it.attention_item_id)).unique()
                            attentionItem.type = 1
                            mDao.update(attentionItem)
                            Object()
                        }
                    }

                    override fun onCompleted() {
                        LogUtil.d(this@MyConcernModelImpl, "数据库处理完成")
                    }
                })
    }
}