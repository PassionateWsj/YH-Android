package com.intfocus.yhdev.net;

import com.intfocus.yhdev.data.request.CommentBody;
import com.intfocus.yhdev.data.request.RequestFavourite;
import com.intfocus.yhdev.data.response.BaseResult;
import com.intfocus.yhdev.data.response.article.ArticleResult;
import com.intfocus.yhdev.data.response.assets.AssetsResult;
import com.intfocus.yhdev.data.response.filter.MenuResult;
import com.intfocus.yhdev.data.response.home.HomeMsgResult;
import com.intfocus.yhdev.data.response.home.KpiResult;
import com.intfocus.yhdev.data.response.home.ReportListResult;
import com.intfocus.yhdev.data.response.home.WorkBoxResult;
import com.intfocus.yhdev.data.response.mine_page.NoticeContentResult;
import com.intfocus.yhdev.data.response.mine_page.UserInfoResult;
import com.intfocus.yhdev.data.response.notice.NoticesResult;
import com.intfocus.yhdev.data.response.scanner.StoreListResult;
import com.intfocus.yhdev.data.response.update.UpdateResult;
import com.intfocus.yhdev.login.bean.Device;
import com.intfocus.yhdev.login.bean.DeviceRequest;
import com.intfocus.yhdev.login.bean.NewUser;
import com.intfocus.yhdev.util.K;

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
     * 推送 push_token
     * <p>
     * POST
     * /api/v1.1/device/push_token
     * <p>
     * uuid
     * push_token
     */
    @POST(K.KPushToken)
    Observable<BaseResult> putPushToken(@Query("uuid") String uuid, @Query("push_token") String pushToken);

    /**
     * 获取AssetsMD5
     * <p>
     * GET
     * /api/v1.1/assets/md5
     */
    @GET(K.KAssetsMD5)
    Observable<AssetsResult> getAssetsMD5();

    /**
     * 公告预警详情
     * <p>
     * GET
     * /api/v1.1/my/view/notice
     * <p>
     * notice_id
     * user_num
     */
    @GET(K.KNoticeContent)
    Observable<NoticeContentResult> getNoticeContent(@Query("notice_id") String noticeId, @Query("user_num") String userNum);

    /**
     * 发表评论
     * <p>
     * POST
     * /api/v1.1/comment
     * <p>
     * "user_num": "",
     * "content": "",
     * "object_type": ,
     * "object_id": ,
     * "object_title": ""
     */
    @POST(K.KComment)
    Observable<BaseResult> submitComment(@Body CommentBody commentBody);

    /**
     * 工具箱页
     * <p>
     * GET
     * /api/v1.1/app/component/toolbox
     * <p>
     * group_id
     * role_id
     */
    @GET(K.KWorkBoxList)
    Observable<WorkBoxResult> getWorkBox(@Query("group_id") String groupId, @Query("role_id") String roleId);

    /**
     * 报表页面列表
     * <p>
     * GET
     * /api/v1.1/app/component/reports
     * <p>
     * group_id
     * role_id
     */
    @GET(K.KReportList)
    Observable<ReportListResult> getReportList(@Query("group_id") String groupId, @Query("role_id") String roleId);

    /**
     * 门店列表
     * <p>
     * GET
     * /api/v1.1/user/stores
     * <p>
     * user_num
     */
    @GET(K.KStoreList)
    Observable<StoreListResult> getStoreList(@Query("user_num") String userNum);

    /**
     * 用户信息
     * <p>
     * GET
     * /api/v1.1/my/statistics
     * <p>
     * user_num
     */
    @GET(K.KUserInfo)
    Observable<UserInfoResult> getUserInfo(@Query("user_num") String userNum);

    /**
     * 获取概况页公告列表
     * <p>
     * GET
     * /api/v1.1/user/notifications
     * <p>
     * group_id
     * role_id
     */
    @GET(K.KNotifications)
    Observable<HomeMsgResult> getNotifications(@Query("group_id") String groupId, @Query("role_id") String roleId);

    /**
     * 扫码结果
     * <p>
     * GET
     * /api/v1.1/scan/barcode
     * <p>
     * store_id
     * code_info
     */
    @GET(K.KScannerResult)
    Observable<BaseResult> getScannerResult(@Query("store_id") String storeId, @Query("code_info") String codeInfo);

    /**
     * 获取文章收藏列表
     * <p>
     * GET
     * /api/v1.1/my/favourited/articles
     * <p>
     * user_num
     * page
     * limit
     */
    @GET(K.KMyFavouritedList)
    Observable<ArticleResult> getMyFavouritedList(@QueryMap Map<String, String> queryMap);

    /**
     * 收藏状态
     * <p>
     * POST
     * /api/v1.1/my/article/favourite_status
     * <p>
     * "user_num": "123",
     * "article_id": "1",
     * "favourite_status": "1
     */
    @POST(K.KFavouriteStatus)
    Observable<BaseResult> articleOperating(@Body RequestFavourite requestFavourite);

    /**
     * 获取数据学院文章列表
     * <p>
     * GET
     * /api/v1.1/my/articles
     * <p>
     * user_num
     * page
     * limit
     */
    @GET(K.KArticlesList)
    Observable<ArticleResult> getArticleList(@QueryMap Map<String, String> queryMap);


    /**
     * 获取首页概况数据
     * <p>
     * GET
     * /api/v1.1/app/component/overview
     * <p>
     * group_id
     * role_id
     */
    @GET(K.KOverview)
    Observable<KpiResult> getHomeIndex(@QueryMap Map<String, String> queryMap);

    /**
     * 获取首页消息数据
     * <p>
     * GET
     * /api/v1.1/user/notifications
     * <p>
     * group_id
     * role_id
     */
    @GET(K.KNotifications)
    Observable<HomeMsgResult> getHomeMsg(@QueryMap Map<String, String> queryMap);

    /**
     * 公告预警列表
     * <p>
     * GET
     * /api/v1.1/my/notices
     * <p>
     * user_num
     * type
     * page
     * limit
     */
    @GET(K.KNoticeList)
    Observable<NoticesResult> getNoticeList(@QueryMap Map<String, String> queryMap);

    /**
     * 获取筛选菜单信息
     * <p>
     * GET
     * /api/v1/report/menus
     */
    @GET(K.KFilterMenuPath)
    Observable<MenuResult> getFilterMenu();


    /**
     * 头像上传
     * <p>
     * POST
     * /api/v1.1/upload/gravatar
     * <p>
     * "user_num": "",
     * "device_id": "",
     * "gravatar": ""
     */
    @Multipart
    @POST(K.kNewUserIconUploadPath)
    Observable<BaseResult> userIconUpload(@Query("device_id") String deviceId, @Query("user_num") String user_num, @Part MultipartBody.Part file);

    /**
     * 登录post请求
     * <p>
     * POST
     * /api/v1.1/user/authentication
     * <p>
     * user_num  　用户名
     * password 　密码
     */
    @POST(K.KNewLogin)
    Observable<NewUser> userLogin(@Query("user_num") String userNum, @Query("password") String password, @Query("coordinate") String coordinate);

    /**
     * 上传设备信息
     * <p>
     * POST
     * /api/v1.1/app/device
     * <p>
     * "user_num": "",
     * "device": {
     * "uuid": "",
     * "os": "",
     * "name": "",
     * "os_version": "",
     * "platform": ""
     * },
     * "app_version": "",
     * "ip": "",
     * "browser": ""
     */
    @POST(K.KNewDevice)
    Observable<Device> deviceUpLoad(@Body DeviceRequest deviceRequest);

    /**
     * 退出登录
     * <p>
     * POST
     * /api/v1.1/user/logout
     * <p>
     * user_device_id
     */
    @POST(K.KNewLogout)
    Observable<BaseResult> userLogout(@Query("user_device_id") String userDeviceId);

    /**
     * 更新密码
     * <p>
     * POST
     * /api/v1.1/user/update_password
     * <p>
     * user_num 　用户名
     * password  　新密码
     */
    @POST(K.KNewUpdatePwd)
    Observable<BaseResult> updatePwd(@Query("user_num") String userNum, @Query("password") String newPwd);

    /**
     * 重置密码
     * <p>
     * POST
     * /api/v1.1/user/reset_password
     * <p>
     * user_num 　用户名
     * mobile 　手机号
     */
    @POST(K.KNewResetPwd)
    Observable<BaseResult> resetPwd(@Query("user_num") String userNum, @Query("mobile") String mobile);

    /**
     * 用户信息
     * <p>
     * GET
     * /api/v1.1/my/statistics
     * <p>
     * user_num
     */
    @GET(K.KNewChoiceMenu)
    Call<MenuResult> getChoiceMenus(@Query("params") String params);

    /**
     * 下载静态资源
     * <p>
     * GET
     * /api/v1.1/download/assets
     * <p>
     * filename
     */
    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);

    /**
     * 下载静态资源
     * <p>
     * GET
     * /api/v1.1/download/assets
     * <p>
     * filename
     */
    @GET(K.KNewUpdate)
    Observable<UpdateResult> getUpdateMsg(@Query("app_os") String app_os, @Query("app_version") String app_version, @Query("app_build") String app_build, @Query("uuid") String uuid);
}
