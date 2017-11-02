package com.intfocus.yhdev.general.net;

import com.intfocus.yhdev.general.data.request.CommentBody;
import com.intfocus.yhdev.general.data.request.RequestFavourite;
import com.intfocus.yhdev.general.data.response.BaseResult;
import com.intfocus.yhdev.general.data.response.article.ArticleResult;
import com.intfocus.yhdev.general.data.response.assets.AssetsResult;
import com.intfocus.yhdev.general.data.response.filter.MenuResult;
import com.intfocus.yhdev.general.data.response.home.HomeMsgResult;
import com.intfocus.yhdev.general.data.response.home.KpiResult;
import com.intfocus.yhdev.general.data.response.home.ReportListResult;
import com.intfocus.yhdev.general.data.response.home.WorkBoxResult;
import com.intfocus.yhdev.general.data.response.login.RegisterResult;
import com.intfocus.yhdev.general.data.response.mine_page.NoticeContentResult;
import com.intfocus.yhdev.general.data.response.mine_page.UserInfoResult;
import com.intfocus.yhdev.general.data.response.notice.NoticesResult;
import com.intfocus.yhdev.general.data.response.scanner.NearestStoresResult;
import com.intfocus.yhdev.general.data.response.scanner.StoreListResult;
import com.intfocus.yhdev.general.util.K;
import com.intfocus.yhdev.business.login.bean.Device;
import com.intfocus.yhdev.business.login.bean.DeviceRequest;
import com.intfocus.yhdev.business.login.bean.NewUser;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by CANC on 2017/7/31.
 */

public interface HttpService {

    /**
     * 申请注册
     * <p>
     * GET
     * /api/v1.1/config/info
     *
     * @param keyName keyname
     * @return RegisterResult
     */
    @GET(K.KRegister)
    Observable<RegisterResult> getRegister(@Query("keyname") String keyName);

    /**
     * 推送 push_token
     * <p>
     * POST
     * /api/v1.1/device/push_token
     *
     * @param uuid
     * @param pushToken
     * @return
     */
    @POST(K.KPushToken)
    Observable<BaseResult> putPushToken(@Query("uuid") String uuid, @Query("push_token") String pushToken);

    /**
     * 获取AssetsMD5
     * <p>
     * GET
     * /api/v1.1/assets/md5
     *
     * @return AssetsResult
     */
    @GET(K.KAssetsMD5)
    Observable<AssetsResult> getAssetsMD5();

    /**
     * 公告预警详情
     * <p>
     * GET
     * /api/v1.1/my/view/notice
     *
     * @param noticeId notice_id
     * @param userNum  user_num
     * @return NoticeContentResult
     */
    @GET(K.KNoticeContent)
    Observable<NoticeContentResult> getNoticeContent(@Query("notice_id") String noticeId, @Query("user_num") String userNum);

    /**
     * 发表评论
     * <p>
     * POST
     * /api/v1.1/comment
     *
     * @param commentBody
     * @return BaseResult
     */
    @POST(K.KComment)
    Observable<BaseResult> submitComment(@Body CommentBody commentBody);

    /**
     * 工具箱页
     * <p>
     * GET
     * /api/v1.1/app/component/toolbox
     *
     * @param groupId
     * @param roleId
     * @return WorkBoxResult
     */
    @GET(K.KWorkBoxList)
    Observable<WorkBoxResult> getWorkBox(@Query("group_id") String groupId, @Query("role_id") String roleId);

    /**
     * 报表页面列表
     * <p>
     * GET
     * /api/v1.1/app/component/reports
     *
     * @param groupId
     * @param roleId
     * @return
     */
    @GET(K.KReportList)
    Observable<ReportListResult> getReportList(@Query("group_id") String groupId, @Query("role_id") String roleId);

    /**
     * 门店列表
     * <p>
     * GET
     * /api/v1.1/user/stores
     *
     * @param userNum
     * @return
     */
    @GET(K.KStoreList)
    Observable<StoreListResult> getStoreList(@Query("user_num") String userNum);

    /**
     * 用户信息
     * <p>
     * GET
     * /api/v1.1/my/statistics
     *
     * @param userNum
     * @return
     */
    @GET(K.KUserInfo)
    Observable<UserInfoResult> getUserInfo(@Query("user_num") String userNum);

    /**
     * 获取概况页公告列表
     * <p>
     * GET
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
     * <p>
     * GET
     * /api/v1.1/scan/barcode
     *
     * @param storeId
     * @param codeInfo
     * @return
     */
    @GET(K.KScannerResult)
    Observable<BaseResult> getScannerResult(@Query("store_id") String storeId, @Query("code_info") String codeInfo);

    /**
     * 获取文章收藏列表
     * <p>
     * GET
     * /api/v1.1/my/favourited/articles
     *
     * @param queryMap
     * @return
     */
    @GET(K.KMyFavouritedList)
    Observable<ArticleResult> getMyFavouritedList(@QueryMap Map<String, String> queryMap);

    /**
     * 收藏状态
     * <p>
     * POST
     * /api/v1.1/my/article/favourite_status
     *
     * @param requestFavourite
     * @return
     */
    @POST(K.KFavouriteStatus)
    Observable<BaseResult> articleOperating(@Body RequestFavourite requestFavourite);

    /**
     * 获取数据学院文章列表
     * <p>
     * GET
     * /api/v1.1/my/articles
     *
     * @param queryMap
     * @return
     */
    @GET(K.KArticlesList)
    Observable<ArticleResult> getArticleList(@QueryMap Map<String, String> queryMap);


    /**
     * 获取首页概况数据
     * <p>
     * GET
     * /api/v1.1/app/component/overview
     *
     * @param queryMap
     * @return
     */
    @GET(K.KOverview)
    Observable<KpiResult> getHomeIndex(@QueryMap Map<String, String> queryMap);

    /**
     * 获取首页消息数据
     * <p>
     * GET
     * /api/v1.1/user/notifications
     *
     * @param queryMap
     * @return
     */
    @GET(K.KNotifications)
    Observable<HomeMsgResult> getHomeMsg(@QueryMap Map<String, String> queryMap);

    /**
     * 公告预警列表
     * <p>
     * GET
     * /api/v1.1/my/notices
     *
     * @param queryMap
     * @return
     */
    @GET(K.KNoticeList)
    Observable<NoticesResult> getNoticeList(@QueryMap Map<String, String> queryMap);

    /**
     * 获取筛选菜单信息
     * <p>
     * GET
     * /api/v1/report/menus
     *
     * @return
     */
    @GET(K.KFilterMenuPath)
    Observable<MenuResult> getFilterMenu();


    /**
     * 头像上传
     * <p>
     * POST
     * /api/v1.1/upload/gravatar
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
     * <p>
     * POST
     * /api/v1.1/user/authentication
     *
     * @param userNum
     * @param password
     * @param coordinate
     * @return
     */
    @POST(K.KNewLogin)
    Observable<NewUser> userLogin(@Query("user_num") String userNum, @Query("password") String password, @Query("coordinate") String coordinate);

    /**
     * 上传设备信息
     * <p>
     * POST
     * /api/v1.1/app/device
     *
     * @param deviceRequest
     * @return
     */
    @POST(K.KNewDevice)
    Observable<Device> deviceUpLoad(@Body DeviceRequest deviceRequest);

    /**
     * 退出登录
     * <p>
     * POST
     * /api/v1.1/user/logout
     *
     * @param userDeviceId
     * @return
     */
    @POST(K.KNewLogout)
    Observable<BaseResult> userLogout(@Query("user_device_id") String userDeviceId);

    /**
     * 更新密码
     * <p>
     * POST
     * /api/v1.1/user/update_password
     *
     * @param userNum
     * @param newPwd
     * @return
     */
    @POST(K.KNewUpdatePwd)
    Observable<BaseResult> updatePwd(@Query("user_num") String userNum, @Query("password") String newPwd);

    /**
     * 重置密码
     * <p>
     * POST
     * /api/v1.1/user/reset_password
     *
     * @param userNum
     * @param mobile
     * @return
     */
    @POST(K.KNewResetPwd)
    Observable<BaseResult> resetPwd(@Query("user_num") String userNum, @Query("mobile") String mobile);

    /**
     * 用户信息
     * <p>
     * GET
     * /api/v1.1/my/statistics
     *
     * @param params
     * @return
     */
    @GET(K.KNewChoiceMenu)
    Call<MenuResult> getChoiceMenus(@Query("params") String params);

    /**
     * 下载静态资源
     * <p>
     * GET
     * /api/v1.1/download/assets
     *
     * @param fileUrl
     * @return
     */
    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);

    /**
     * 根据经纬度获取最近的门店
     * <p>
     * GET
     * /api/v1.1/nearest_stores
     *
     * @param limit
     * @param distance
     * @param location
     * @return
     */
    @GET(K.KNearestStores)
    Observable<NearestStoresResult> getNearestStores(@Query("limit") int limit, @Query("distance") double distance, @Query("location") String location);

}
