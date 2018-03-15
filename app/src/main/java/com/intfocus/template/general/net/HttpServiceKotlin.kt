package com.intfocus.template.general.net

import com.intfocus.template.model.response.BaseResult
import com.intfocus.template.model.response.login.SaaSCustomResult
import com.intfocus.template.subject.seven.bean.*
import com.intfocus.template.util.K
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Observable

/**
 * ****************************************************
 * author jameswong
 * created on: 18/03/14 下午3:30
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface HttpServiceKotlin {

    /**
     * SaaS 登录
     *
     * @return
     */
    @GET(K.K_SAAS_API + "?repCode=REP_000000&platform=app")
    fun saasLoginApi(): Observable<SaaSCustomResult>

    /**
     * 获取控件列表
     *
     * @param reportId
     * @return
     */
    @GET(K.K_SAAS_API + "?repCode=REP_000035")
    fun getConcernComponentData(@Query("report_id") reportId: String, @Query("obj_num") objNum: String): Observable<ConcernComponentBean>

    /**
     * 获取已关注的单品列表
     *
     * @param repCode
     * @param controlId
     * @return
     */
    @GET(K.K_SAAS_API)
    fun getConcernItemsListData(@Query("repCode") repCode: String, @Query("control_id") controlId: String): Observable<ConcernItemsBean>

    /**
     * 获取关注列表
     *
     * @param reportId
     * @return
     */
    @GET(K.K_SAAS_API + "?repCode=REP_000089")
    fun getConcernListData(@Query("report_id") reportId: String): Observable<ConcernListResponse>

    /**
     * 获取模板七筛选数据
     *
     * @param repCode
     * @param reportId
     * @return
     */
    @GET(K.K_SAAS_API)
    fun getConcernFilterData(@Query("repCode") repCode: String, @Query("report_id") reportId: String): Observable<ConcernFilterResponse>

    /**
     * SaaS 关注/取消关注
     * POST
     */
    @POST(K.K_SAAS_API)
    fun concernOrCancelConcern(@Body body: ConcernOrCancelConcernRequest): Observable<BaseResult>

}