package com.intfocus.yh_android.net;

import com.intfocus.yh_android.data.request.CommentBody;
import com.intfocus.yh_android.data.request.RequestFavourite;
import com.intfocus.yh_android.data.response.BaseResult;
import com.intfocus.yh_android.data.response.article.ArticleResult;
import com.intfocus.yh_android.data.response.assets.AssetsResult;
import com.intfocus.yh_android.data.response.filter.MenuResult;
import com.intfocus.yh_android.data.response.home.HomeMsgResult;
import com.intfocus.yh_android.data.response.home.KpiResult;
import com.intfocus.yh_android.data.response.home.ReportListResult;
import com.intfocus.yh_android.data.response.home.WorkBoxResult;
import com.intfocus.yh_android.data.response.mine_page.NoticeContentResult;
import com.intfocus.yh_android.data.response.mine_page.UserInfoResult;
import com.intfocus.yh_android.data.response.notice.NoticesResult;
import com.intfocus.yh_android.data.response.scanner.StoreListResult;
import com.intfocus.yh_android.login.bean.Device;
import com.intfocus.yh_android.login.bean.DeviceRequest;
import com.intfocus.yh_android.login.bean.NewUser;
import com.intfocus.yh_android.util.K;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by CANC on 2017/7/31.
 */

public interface HttpService {

    /**
     * 推送 push_token
     * @param uuid
     * @param pushToken
     * @return
     */
    @POST(K.KPushToken)
    Observable<BaseResult> putPushToken(@Query("uuid") String uuid, @Query("push_token") String pushToken);

    /**
     * 获取AssetsMD5
     *
     * @return
     */
    @GET(K.KAssetsMD5)
    Observable<AssetsResult> getAssetsMD5();

    /**
     * 公告预警详情
     *
     * @param noticeId
     * @param userNum
     * @return
     */
    @GET(K.KNoticeContent)
    Observable<NoticeContentResult> getNoticeContent(@Query("notice_id") String noticeId, @Query("user_num") String userNum);

    /**
     * 发表评论
     *
     * @param commentBody
     * @return
     */
    @POST(K.KComment)
    Observable<BaseResult> submitComment(@Body CommentBody commentBody);

    /**
     * 工具箱页
     *
     * @param groupId
     * @param roleId
     * @return
     */
    @GET(K.KWorkBoxList)
    Observable<WorkBoxResult> getWorkBox(@Query("group_id") String groupId, @Query("role_id") String roleId);

    /**
     * 报表页面列表
     *
     * @param groupId
     * @param roleId
     * @return
     */
    @GET(K.KReportList)
    Observable<ReportListResult> getReportList(@Query("group_id") String groupId, @Query("role_id") String roleId);

    /**
     * 门店列表
     *
     * @param userNum
     * @return
     */
    @GET(K.KStoreList)
    Observable<StoreListResult> getStoreList(@Query("user_num") String userNum);

    /**
     * 用户信息
     *
     * @param userNum
     * @return
     */
    @GET(K.KUserInfo)
    Observable<UserInfoResult> getUserInfo(@Query("user_num") String userNum);

    /**
     * 获取概况页公告列表
     * /api/v1.1/user/notifications
     *
     * @param groupId
     * @param roleId
     * @return
     */
    @GET(K.KNotifications)
    Observable<HomeMsgResult> getNotifications(@Query("group_id") String groupId, @Query("role_id") String roleId);

    /**
     * 扫码结果
     * {{host}}/api/v1.1/scan/barcode?api_token=123&store_id=123&code_info=123
     *
     * @param storeId
     * @param codeInfo
     * @return
     */
    @GET(K.KScannerResult)
    Observable<BaseResult> getScannerResult(@Query("store_id") String storeId, @Query("code_info") String codeInfo);

    /**
     * 获取文章收藏列表
     *
     * @param queryMap
     * @return
     */
    @GET(K.KMyFavouritedList)
    Observable<ArticleResult> getMyFavouritedList(@QueryMap Map<String, String> queryMap);

    /**
     * 收藏状态
     *
     * @param requestFavourite
     * @return
     */
    @POST(K.KFavouriteStatus)
    Observable<BaseResult> articleOperating(@Body RequestFavourite requestFavourite);

    /**
     * 获取数据学院文章列表
     *
     * @param queryMap
     * @return
     */
    @GET(K.KArticlesList)
    Observable<ArticleResult> getArticleList(@QueryMap Map<String, String> queryMap);


    /**
     * 获取首页概况数据
     *
     * @param queryMap
     * @return
     */
    @GET(K.KOverview)
    Observable<KpiResult> getHomeIndex(@QueryMap Map<String, String> queryMap);

    /**
     * 获取首页消息数据
     *
     * @param queryMap
     * @return
     */
    @GET(K.KNotifications)
    Observable<HomeMsgResult> getHomeMsg(@QueryMap Map<String, String> queryMap);

    /**
     * 公告预警列表
     *
     * @param queryMap
     * @return
     */
    @GET(K.KNoticeList)
    Observable<NoticesResult> getNoticeList(@QueryMap Map<String, String> queryMap);

    /**
     * 获取筛选菜单信息
     *
     * @return
     */
    @GET(K.KFilterMenuPath)
    Observable<MenuResult> getFilterMenu();


    /**
     * 头像上传
     *
     * @param deviceId
     * @param user_num
     * @param file
     * @return
     */
    @Multipart
    @POST(K.kNewUserIconUploadPath)
    Observable<BaseResult> userIconUpload(@Query("device_id") String deviceId, @Query("user_num") String user_num, @Part MultipartBody.Part file);

    /**
     * 登录post请求
     *
     * @param userNum  　用户名
     * @param password 　密码
     * @return
     */
    @POST(K.KNewLogin)
    Observable<NewUser> userLogin(@Query("user_num") String userNum, @Query("password") String password, @Query("coordinate") String coordinate);

    /**
     * 上传设备信息
     *
     * @param deviceRequest 　设备信息
     * @return
     */
    @POST(K.KNewDevice)
    Observable<Device> deviceUpLoad(@Body DeviceRequest deviceRequest);

    /**
     * 退出登录
     *
     * @param userDeviceId
     * @return
     */
    @POST(K.KNewLogout)
    Observable<BaseResult> userLogout(@Query("user_device_id") String userDeviceId);

    /**
     * 更新密码
     *
     * @param userNum 　用户名
     * @param newPwd  　新密码
     * @return
     */
    @POST(K.KNewUpdataPwd)
    Observable<BaseResult> updatePwd(@Query("user_num") String userNum, @Query("password") String newPwd);

    /**
     * 重置密码
     *
     * @param userNum 　用户名
     * @param mobile  　手机号
     * @return
     */
    @POST(K.KNewResetPwd)
    Observable<BaseResult> resetPwd(@Query("user_num") String userNum, @Query("mobile") String mobile);
}
