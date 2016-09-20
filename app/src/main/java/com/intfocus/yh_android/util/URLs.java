package com.intfocus.yh_android.util;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * api链接，宏
 *
 * @author jay
 * @version 1.0
 * @created 2016-01-06
 */
public class URLs extends PrivateURLs implements Serializable {

    public final static String API_USER_PATH            = "%s/api/v1/%s/%s/%s/authentication";
    public final static String API_DATA_PATH            = "%s/api/v1/group/%s/template/%s/report/%s/attachment";
    public final static String API_COMMENT_PATH         = "%s/api/v1/user/%d/id/%d/type/%d";
    public final static String API_SCREEN_LOCK_PATH     = "%s/api/v1/user_device/%s/screen_lock";
    public final static String API_DEVICE_STATE_PATH    = "%s/api/v1/user_device/%d/state";
    public final static String API_RESET_PASSWORD_PATH  = "%s/api/v1/update/%s/password";
    public final static String API_ACTION_LOG_PATH      = "%s/api/v1/android/logger";

    public final static String API_PUSH_DEVICE_TOKEN_PATH = "%s/api/v1/device/%s/push_token/%s";
    public final static String API_BARCODE_SCAN_PATH      = "%s/api/v1/group/%s/role/%s/user/%s/store/%s/barcode_scan?code_info=%s&code_type=%s";
    public final static String API_ASSETS_PATH            = "%s/api/v1/download/%s.zip";
    public final static String API_UPLOAD_GRAVATAR_PATH   = "%s/api/v1/device/%s/upload/user/%s/gravatar";

    public final static String KPI_PATH                  = "%s/mobile/%s/group/%s/role/%s/kpi";
    public final static String MESSAGE_PATH              = "%s/mobile/%s/role/%s/group/%s/user/%s/message";
    public final static String APPLICATION_PATH          = "%s/mobile/%s/role/%s/app";
    public final static String ANALYSE_PATH              = "%s/mobile/%s/role/%s/analyse";
    public final static String COMMENT_PATH              = "%s/mobile/%s/id/%s/type/%s/comment";
    public final static String RESET_PASSWORD_PATH       = "%s/mobile/%s/update_user_password";
    public final static String THURSDAY_SAY_PATH         = "%s/mobile/%s/thursday_say";

    public final static String REPORT_DATA_FILENAME      = "group_%s_template_%s_report_%s.js";

    public final static String USER_CONFIG_FILENAME      = "user.json";
    public final static String CONFIG_DIRNAME            = "Configs";
    public final static String SETTINGS_CONFIG_FILENAME  = "setting.json";
    public final static String BETA_CONFIG_FILENAME      = "beta.json";
    public final static String PUSH_CONFIG_FILENAME      = "push_message.json";
    public final static String TABINDEX_CONFIG_FILENAME  = "page_tab_index.json";
    public final static String GESTURE_PASSWORD_FILENAME = "gesture_password.json";
    public final static String HTML_DIRNAME              = "HTML";
    public final static String SHARED_DIRNAME            = "Shared";
    public final static String CACHED_DIRNAME            = "Cached";
    public final static String CACHED_HEADER_FILENAME    = "cached_header.json";
    public final static String CURRENT_VERSION_FILENAME  = "current_version.txt";
    public final static String GRAVATARJSON_FILENAME     = "gravatar.json";
    public final static String BARCODE_RESULT_FILENAME   = "barcode_scan_result.json";
    public final static String SCAN_BARCODE_FILENAME     = "scan_bar_code.html";
    public final static String PGYER_VERSION_FILENAME    = "pgyer_version.txt";
    public final static String LOCAL_NOTIFICATION_FILENAME = "local_notification.json";

    public final static String kTabKpi                   = "tab_kpi";
    public final static String kTabAnalyse               = "tab_analyse";
    public final static String kTabApp                   = "tab_app";
    public final static String kTabMessage               = "tab_message";

    public final static String kJSInterfaceName          = "AndroidJSBridge";

    public final static String kAssets                   = "assets";
    public final static String kLoding                   = "loading";
    public final static String kFonts                    = "fonts";
    public final static String kImages                   = "images";
    public final static String kStylesheets              = "stylesheets";
    public final static String kJavaScripts              = "javascripts";
    public final static String kBarCodeScan              = "BarCodeScan";
    public final static String kAdvertisement            = "advertisement";

    public final static String kAction                   = "action";
    public final static String kPassword                 = "password";
    public final static String kSettingThursdaySay       = "setting_thursday_say";
    public final static String kRoleId                   = "role_id";
    public final static String kGroupId                  = "group_id";
    public final static String kSetting                  = "setting";
    public final static String kSettingPgyer             = "setting_pgyer";
    public final static String kSettingPassword          = "setting_password";
    public final static String kData                     = "data";
    public final static String kCodeInfo                 = "code_info";
    public final static String kCodeType                 = "code_type";
    public final static String kBannerName               = "bannerName";
    public final static String kObjectId                 = "objectID";
    public final static String kObjectType               = "objectType";
    public final static String kObjTitle                 = "obj_title";
    public final static String kObjType                  = "obj_type";
    public final static String kLink                     = "link";
    public final static String kUserNum                  = "user_num";
    public final static String kStore                    = "store";
    public final static String kStoreIds                 = "store_ids";
    public final static String kName                     = "name";
    public final static String kUseGesturePassword       = "use_gesture_password";
    public final static String kGesturePassword          = "gesture_password";
    public final static String kIsLogin                  = "is_login";
    public final static String kPushDeviceToken          = "push_device_token";

    public final static String kCode                     = "code";
    public final static String kBody                     = "body";
    public final static String kETag                     = "ETag";
    public final static String kLastModified             = "Last_Modified";

    public static String storage_base(Context context) {
        //    String path = "";
        //    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        //        path = String.format("%s/com.intfocus.yh_android", Environment.getExternalStorageDirectory().getAbsolutePath());
        //    } else {
        //        path =String.format("%s/com.intfocus.yh_android", context.getApplicationContext().getFilesDir());
        //    }
        return context.getApplicationContext().getFilesDir().toString();
    }

    public static String timestamp() {
        return (new SimpleDateFormat("yyyyMMddKKmmss")).format(new Date());
    }

    /*
     * UI 版本
     */
    public static String currentUIVersion(Context mContext) {
        try {
            String betaConfigPath = FileUtil.dirPath(mContext, URLs.CONFIG_DIRNAME, URLs.BETA_CONFIG_FILENAME);
            JSONObject betaJSON = new JSONObject();
            if (new File(betaConfigPath).exists()) {
                betaJSON = FileUtil.readConfigFile(betaConfigPath);
            }

            return betaJSON.has("old_ui") && betaJSON.getBoolean("old_ui") ? "v1" : "v2";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "v2";
    }
    /**
     * 对URL进行格式处理
     *
     * @param path 路径
     * @return "http://" + URLEncoder.encode(path)
     */
    private static String formatURL(String path) throws UnsupportedEncodingException {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        return "http://" + URLEncoder.encode(path,"UTF-8");
    }

    /**
     * MD5加密-32位
     *
     * @param inStr 需要MD5加密的内容
     * @return hexValue.toString()
     */
    public static String MD5(String inStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }

        byte[] md5Bytes = md5.digest(byteArray);

        StringBuilder hexValue = new StringBuilder();

        for (byte bytes : md5Bytes) {
            int val = ((int) bytes) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }

        return hexValue.toString();
    }
}