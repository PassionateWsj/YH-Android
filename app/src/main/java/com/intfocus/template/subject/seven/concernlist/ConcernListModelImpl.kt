package com.intfocus.template.subject.seven.concernlist

import android.content.Context
import com.intfocus.template.SYPApplication
import com.intfocus.template.constant.Params.DATASOURCE_CODE
import com.intfocus.template.constant.Params.GROUP_ID
import com.intfocus.template.constant.Params.ROLE_ID
import com.intfocus.template.constant.Params.USER_BEAN
import com.intfocus.template.constant.Params.USER_NUM
import com.intfocus.template.general.net.ApiException
import com.intfocus.template.general.net.CodeHandledSubscriber
import com.intfocus.template.general.net.RetrofitUtil
import com.intfocus.template.general.net.SaaSRetrofitUtil
import com.intfocus.template.model.DaoUtil
import com.intfocus.template.model.callback.LoadDataCallback
import com.intfocus.template.model.gen.ConcernListBeanDao
import com.intfocus.template.model.response.BaseResult
import com.intfocus.template.subject.seven.bean.ConcernListResponse
import com.intfocus.template.subject.seven.bean.ConcernOrCancelConcernRequest
import com.intfocus.template.util.LogUtil
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 上午11:28
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class ConcernListModelImpl : ConcernListModel {

    private val mDao = DaoUtil.getConcernListBeanDao()
    private var mUuid = ""

    companion object {
        private val TAG = "ConcernListModelImpl"
        private var INSTANCE: ConcernListModelImpl? = null
        private var observable: Subscription? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): ConcernListModelImpl {
            return INSTANCE ?: ConcernListModelImpl()
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

    override fun updateConcernListData(reportId: String, callback: LoadDataCallback<Boolean>) {
        val mUserSP = SYPApplication.globalContext.getSharedPreferences(USER_BEAN, Context.MODE_PRIVATE)
        val userNum = mUserSP.getString(USER_NUM, "0")
        val roleId = mUserSP.getString(ROLE_ID, "0")
        val groupId = mUserSP.getString(GROUP_ID, "0")
        mUuid = String.format("%s-%s-%s-%s", userNum, roleId, groupId, reportId)

        SaaSRetrofitUtil.getHttpServiceKotlin(SYPApplication.globalContext)
                .getConcernListData( reportId)
                .compose(RetrofitUtil.CommonOptions<ConcernListResponse>())
                .subscribe(object : CodeHandledSubscriber<ConcernListResponse>() {
                    override fun onCompleted() {

                    }

                    override fun onError(apiException: ApiException?) {
                    }

                    override fun onBusinessNext(data: ConcernListResponse?) {
                        observable = Observable.just(data)
                                .subscribeOn(Schedulers.io())
                                .map {
                                    // 删除关注单品数据库表中
                                    DaoUtil.getConcernListBeanDao().deleteInTx(
                                            DaoUtil.getConcernListBeanDao()
                                                    .queryBuilder()
                                                    .where(ConcernListBeanDao.Properties.Uuid.eq(mUuid))
                                                    .list())
                                    it?.let { mDao.insertInTx(it.data) }
                                }
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    callback.onSuccess(true)
                                }
                    }
                })
    }

    override fun getData(keyWord: String, concerned: Boolean, reportId: String, callback: ConcernListModel.LoadDataCallback) {
        val queryBuilder = mDao.queryBuilder()
        observable = Observable.just(keyWord)
                .subscribeOn(Schedulers.io())
                .map {
                    queryBuilder.where(queryBuilder.and(ConcernListBeanDao.Properties.Type.eq(if (concerned) 1 else 0),
                            ConcernListBeanDao.Properties.Uuid.eq(mUuid),
                            queryBuilder.or(ConcernListBeanDao.Properties.Obj_num.like("%$keyWord%"),
                                    ConcernListBeanDao.Properties.Obj_name.like("%$keyWord%")))).list()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it != null && it.isNotEmpty()) {
                        LogUtil.d(this@ConcernListModelImpl, "数据库处理成功,获取数据条数 ::: " + it.size)
                        callback.onDataLoaded(it)
                    } else {
                        callback.onDataNotAvailable(Throwable("未查询到数据"))
                        LogUtil.d(this@ConcernListModelImpl, "数据库处理错误,获取数据条数为 空")
                    }
                }
    }

    override fun concernOrCancelConcern(attentionItemId: String, attentionItemName: String, reportId: String, callback: ConcernListModel.ConcernCallback) {
        val queryBuilder = mDao.queryBuilder()
        observable = Observable.just(listOf(attentionItemId, attentionItemName))
                .subscribeOn(Schedulers.io())
                .map {
                    queryBuilder.where(queryBuilder.and(ConcernListBeanDao.Properties.Obj_num.eq(it[0]),
                            ConcernListBeanDao.Properties.Obj_name.eq(it[1]))).unique()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it == null)
                        return@subscribe
                    val mUserSP = SYPApplication.globalContext.getSharedPreferences(USER_BEAN, Context.MODE_PRIVATE)
                    val userNum = mUserSP.getString(USER_NUM, "0")
                    val roleId = mUserSP.getString(ROLE_ID, "0")
                    val groupId = mUserSP.getString(GROUP_ID, "0")
                    val dataSourceCode = mUserSP.getString(DATASOURCE_CODE, "0")

                    SaaSRetrofitUtil.getHttpServiceKotlin(SYPApplication.globalContext)
                            .concernOrCancelConcern(ConcernOrCancelConcernRequest("REP_000090", dataSourceCode, roleId, groupId, userNum, reportId, it.obj_num, if (it.type == 1) 0 else 1))
                            .compose(RetrofitUtil.CommonOptions<BaseResult>())
                            .subscribe(object : CodeHandledSubscriber<BaseResult>() {
                                override fun onCompleted() {

                                }

                                override fun onError(apiException: ApiException?) {
                                }

                                override fun onBusinessNext(data: BaseResult?) {
                                    if (data != null && data.code == "0") {
                                        it.type = if (it.type == 1) 0 else 1
                                        val isSelected = it.type == 1
                                        mDao.update(it)
                                        callback.onConcernResult(isSelected)
                                    }
                                }
                            })
                }
    }
}