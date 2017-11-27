package com.intfocus.syp_template.util;

import java.io.Serializable;

/**
 * api 链接，宏
 *
 * @author jay
 * @version 1.0
 * @created 2016-01-06
 * Created by lijunjie on 16/9/22.
 */
public class K  implements Serializable {

    public final static Integer K_TIMER_INTERVAL = 30;

    /**
     * API#paths
     */
    public final static String K_USER_AUTHENTICATE_API_PATH = "%s/api/v1/%s/%s/%s/authentication";
    public final static String K_USER_FORGET_API_PATH = "%s/api/v1/:platform/reset_password";
    public final static String K_REPORT_DATA_API_PATH = "%s/api/v1/group/%s/template/%s/report/%s/zip";
    public final static String K_REPORT_JSON_ZIP_API_PATH = "%s/api/v1/group/%s/template/%s/report/%s/jzip";
    public final static String K_REPORT_JSON_API_PATH = "%s/api/v1/group/%s/template/%s/report/%s/json";
//    public final static String kCommentAPIPath = "%s/api/v1/user/%d/id/%d/type/%d";

    public final static String K_DEVICE_STATE_API_PATH = "%s/api/v1/user_device/%s/state";
    public final static String K_RSET_PWD_API_PATH = "%s/api/v1/update/%s/password";
    public final static String K_PUSH_DEVICE_TOKEN_API_PATH = "%s/api/v1/device/%s/push_token/%s";
    public final static String K_BAR_CODE_SCAN_API_PATH = "%s/api/v1/group/%s/role/%s/user/%s/store/%s/barcode_scan?code_info=%s&code_type=%s";
    public final static String K_BAR_CODE_SCAN_API_VIEW_PATH = "%s/mobile/v2/store/%s/barcode/%s/view";
    public final static String K_BAR_CODE_SCAN_API_DATA_PATH = "%s/mobile/v2/store/%s/barcode/%s/attachment";
    //    public final static String K_DOWNLOAD_ASSETS_API_PATH = "%s/api/v1/download/%s.zip";
//    public final static String K_DEVICE_TOKEN_API_PATH = "%s/api/v1/user/%s/devices";
    public final static String K_DELETE_DEVICE_ID_API_PATH = "%s/api/v1/android/%s/logout";

    /**
     * Mobile#View Path
     */
    public final static String K_KPI_MOBILE_DATA_PATH = "%s/mobile/%s/data/group/%s/role/%s/kpi";
    public final static String K_KPI_API_DATA_PATH = "%s/api/v1/group/%s/role/%s/kpi";
    public final static String K_MESSAGE_DATA_MOBILE_PATH = "%s/api/v1/role/%s/group/%s/user/%s/message";
    public final static String K_COMMENT_MOBILE_PATH = "%s/mobile/%s/id/%s/type/%s/comment";
    public final static String K_THURSDAY_SAY_MOBILE_PATH = "%s/mobile/%s/thursday_say";
    public final static String K_NEW_KPI_API_DATA_PATH = "/api/v1/group/{groupId}/role/{roleId}/kpi";
    public final static String K_NEW_MSG_DATA_MOBILE_PATH = "/api/v1/role/{roleId}/group/{groupId}/user/{userId}/message";

    /**
     * MinePage#API
     */
    public final static String K_USER_INFO_PATH = "%s/api/v1/user/%s/group/%s/role/%s/statistics";
    public final static String K_NOTICE_LIST_PATH = "%s/api/v1/user/%s/notices?type=%s&page=%s&limit=%s";
    public final static String K_APP_LIST_PATH = "%s/api/v1/group/%s/role/%s/apps";
    public final static String K_WORK_BOX_LIST_PATH = "%s/api/v1/group/%s/role/%s/app_covers";
    public final static String K_REPORTS_LIST_PATH = "%s/api/v1/group/%s/role/%s/analyses";
    public final static String K_INSTITUTE_LIST_PATH = "/api/v1/user/{userId}/page/{page}/limit/{pageSize}/articles";
    public final static String K_INSTITUTE_COLLECTION_PATH = "%s/api/v1/user/%s/article/%s/favourite_status/%s";
    public final static String K_FAVOURITE_ARTICLES_PATH = "/api/v1/user/{userId}/page/{page}/limit/{pageSize}/favourite_articles";
    public final static String K_ARTICLE_COLLECTION_PATH = "/api/v1/user/{userId}/article/{articleId}/favourite_status/{status}";
    public final static String K_NEW_NOTICE_LIST_PATH = "/api/v1/user/{userId}/notices";
    public final static String K_USER_ICON_UPLOAD_PATH = "/api/v1/device/{deviceId}/upload/user/{userId}/gravatar";

    /**
     * 筛选
     */
    public final static String K_FILTER_MENU_PATH = "/api/v1/report/menus";

    /**
     * Config#Application
     */
    public final static String K_CONFIG_DIR_NAME = "Configs";
    public final static String K_SHARED_DIR_NAME = "Shared";
    public final static String K_CACHED_DIR_NAME = "Cached";
    public final static String K_HTML_DIR_NAME = "HTML";
    public final static String K_ASSETS_DIR_NAME = "Assets";
    public final static String K_REPORT_DATA_FILE_NAME = "group_%s_template_%s_report_%s.js";
    public final static String K_USER_CONFIG_FILE_NAME = "user.json";
    public final static String K_PUSH_MESSAGE_FILE_NAME = "push_message.json";
    public final static String K_SETTING_CONFIG_FILE_NAME = "setting.json";
    public final static String K_TAB_INDEX_CONFIG_FILE_NAME = "page_tab_index.json";
    public final static String K_GESTURE_PWD_CONFIG_FILE_NAME = "gesture_password.json";
    public final static String K_LOCAL_NOTIFICATION_CONFIG_FILE_NAME = "local_notification.json";
    public final static String K_CACHED_HEADER_CONFIG_FILE_NAME = "cached_header.json";
    public final static String K_PGYER_VERSION_CONFIG_FILE_NAME = "pgyer_version.json";
    public final static String K_GRAVATAR_CONFIG_FILE_NAME = "gravatar.json";
    public final static String K_BETA_CONFIG_FILE_NAME = "beta_v0.json";
    public final static String K_BAR_CODE_RESULT_FILE_NAME = "barcode_result.json";
    public final static String K_SCAN_BAR_CODE_HTML_NAME = "scan_bar_code.html";
    public final static String K_CURRENT_VERSION_FILE_NAME = "current_version.txt";
    public final static String K_BEHAVIOR_CONFIG_FILE_NAME = "behavior.json";
    public final static String K_TEMPLATE_V1 = "templateV1";

    /**
     * Config#User Model
     */
    public final static String K_APP_VERSION = "app_version";
    public final static String K_FONTS_MD5 = "fonts_md5";
    public final static String K_IMAGES_MD5 = "images_md5";
    public final static String K_ASSETS_MD5 = "assets_md5";
    public final static String K_ICONS_MD5 = "icons_md5";
    public final static String K_STYLESHEETS_MD5 = "stylesheets_md5";
    public final static String K_JAVA_SCRIPTS_MD5 = "javascripts_md5";
    public final static String K_INFO = "info";
    public final static String K_VALID = "valid";
    public final static String K_USER_ID = "user_id";
    public final static String K_USER_NAME = "user_name";
    public final static String K_USER_DEVICE_ID = "user_device_id";
    public final static String K_CURRENT_UI_VERSION = "current_ui_version";

    /**
     * Config#Push Message
     */
    public final static String K_PUSH_CONFIG_FILE_NAME = "push_message_config.json";
    public final static String K_PUSH_IS_VALID = "push_valid";
    public final static String K_PUSH_DEVICE_TOKEN = "push_token";
    public final static String K_PUSH_DEVICE_UUID = "device_uuid";

    /**
     * Config#User Device Info
     */
    public final static int K_MIN_SDK_VERSION = 14;
    public final static int K_MAX_SDK_VERSION = 25;

    /**
     * 新API及所需api key
     */
    public final static String API_TOKEN = "api_token";

    /**
     * 加密所需api key
     */
    public final static String ANDROID_API_KEY = "578905f6e0c4189caa344ee4b1e460e5";

    public final static String KCollectionUpload = "/api/v1.1/acquisition/data"; //提交采集数据
    public final static String KUploadImage = "/api/v1.1/upload/images"; //上传图片


    /**
     * 扫码结果
     */
    public final static String K_SCANNER_RESULT = "/api/v1.1/scan/barcode";
    /**
     * 个人信息
     */
    public final static String K_USER_INFO = "/api/v1.1/my/statistics";
    /**
     * 公告预警
     */
    public final static String K_NOTICE_LIST = "/api/v1.1/my/notices";
    /**
     * 公告预警详情
     */
    public final static String K_NOTICE_CONTENT = "/api/v1.1/my/view/notice";
    /**
     * 数据学院文章列表
     */
    public final static String K_ARTICLES_LIST = "/api/v1.1/my/articles";
    /**
     * 收藏状态
     */
    public final static String K_FAVOURITE_STATUS = "/api/v1.1/my/article/favourite_status";
    /**
     * 我的收藏列表
     */
    public final static String K_MY_FAVOURITE_LIST = "/api/v1.1/my/favourited/articles";
    /**
     * 生意概况
     */
    public final static String K_OVERVIEW = "/api/v1.1/app/component/overview";
    /**
     * 用户公告
     */
    public final static String K_NOTIFICATIONS = "/api/v1.1/user/notifications";
    /**
     * 门店列表
     */
    public final static String K_STORE_LIST = "/api/v1.1/user/stores";
    /**
     * 报表列表
     */
    public final static String K_REPORT_LIST = "/api/v1.1/app/component/reports";
    /**
     * 工具箱
     */
    public final static String K_WORK_BOX_LIST = "/api/v1.1/app/component/toolbox";
    /**
     * 用户验证
     */
    public final static String K_NEW_LOGIN = "/api/v1.1/user/authentication";
    /**
     * 上传设备信息
     */
    public final static String K_NEW_DEVICE = "/api/v1.1/app/device";
    /**
     * 退出登录
     */
    public final static String K_NEW_LOGOUT = "/api/v1.1/user/logout";
    /**
     * 更新密码
     */
    public final static String K_NEW_UPDATE_PWD = "/api/v1.1/user/update_password";
    /**
     * 重置密码
     */
    public final static String K_NEW_RESET_PWD = "/api/v1.1/user/reset_password";
    /**
     * 发表评论
     */
    public final static String K_COMMENT = "/api/v1.1/comment";
    /**
     * 申请注册
     */
    public final static String K_REGISTER = "/api/v1.1/config/info";
    /**
     * "api_token": "",
     * "user_num": ,
     * "title": "",
     * "content": "",
     * "app_version":"",
     * "platform":"",
     * "platform_version":""
     * <p>
     * <p>
     * 提交反馈
     */
    public final static String K_FEED_BACK = "/api/v1.1/feedback";
    /**
     * 静态资源MD5
     */
    public final static String K_ASSETS_MD5_LIST = "/api/v1.1/assets/md5";
    /**
     * 推送设备 push_token
     */
    public final static String K_PUSH_TOKEN = "/api/v1.1/device/push_token";

    /**
     * 报表 Html 数据
     */
    public final static String K_REPORT_HTML = "%s/mobile/v2/group/%s/template/%s/report/%s";
    /**
     * 报表 Zip 数据
     */
    public final static String K_REPORT_BASE_API = "/api/v1.1/report/data";
    /**
     * 报表 Zip 数据
     */
    public final static String K_REPORT_ZIP_DATA = "%s/api/v1.1/report/data?api_token=%s&group_id=%s&template_id=%s&report_id=%s&disposition=zip";

    /**
     * 报表 Json 数据
     */
    public final static String K_REPORT_JSON_DATA = "/api/v1.1/report/data?disposition=json";

    /**
     * 上传设备
     */
    public final static String K_ACTION_LOG = "%s/api/v1.1/device/logger";

    /**
     * 上传锁屏信息
     */
    public final static String K_SCREEN_LOCK_API_PATH = "%s/api/v1.1/device/screen_lock";

    /**
     * /api/v1.1/download/assets
     * <p>
     * filename=javascripts.zip
     * <p>
     * <p>
     * 下载静态资源
     */
    public final static String K_DOWNLOAD_ASSETS_API_PATH = "%s/api/v1.1/download/assets?api_token=d93c1a0dc03fe4ffad55a82febd1c94f&filename=%s.zip";

    /**
     * user_num
     * id
     * <p>
     * <p>
     * 设备列表
     */
    public final static String K_DEVICE_TOKEN_API_PATH = "/api/v1.1/user/devices";

    /**
     * 上传头像
     */
    public final static String K_NEW_USER_ICON_UPLOAD_PATH = "/api/v1.1/upload/gravatar";
    public final static String K_NEW_CHOICE_MENU = "/api/v1.1/report/choice_menus";
    public final static String K_DOWNLOAD_ASSETS_ZIP = "/api/v1.1/download/assets";
    public final static String K_NEAREST_STORES = "/api/v1.1/nearest_stores";
    public final static String KCollectionConfig = "/api/v1.1/acquisition/config";
}
