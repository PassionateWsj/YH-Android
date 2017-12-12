package com.intfocus.template.util;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.intfocus.template.BuildConfig;
import com.intfocus.template.general.PriorityRunnable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.intfocus.template.SYPApplication.globalContext;
import static com.intfocus.template.SYPApplication.priorityThreadPool;
import static com.intfocus.template.constant.Params.ACTION;
import static com.intfocus.template.constant.Params.USER_ID;
import static com.intfocus.template.constant.Params.USER_NAME;
import static com.intfocus.template.constant.Params.USER_NUM;
import static com.intfocus.template.constant.Params.USER_PASS;
import static com.intfocus.template.util.K.K_APP_VERSION;
import static com.intfocus.template.util.K.K_USER_NAME;

/**
 *
 * @author liuruilin
 * @date 2017/8/1
 */

public class ActionLogUtil {

    /**
     * 上传锁屏信息
     *
     * @param state    是否启用锁屏
     * @param deviceID 设备标识
     * @param password 锁屏密码
     *                 <p>
     *                 "id": ,
     *                 "screen_lock_state": ,
     *                 "screen_lock_type": "",
     *                 "screen_lock": ""
     */
    public static void screenLock(final String deviceID, final String password, boolean state) {
        priorityThreadPool.execute(new PriorityRunnable(1) {
            @Override
            public void doSth() {
                String urlString = String.format(K.K_SCREEN_LOCK_API_PATH, BuildConfig.BASE_URL);

                Map<String, String> params = new HashMap<>();
                params.put("screen_lock_state", "1");
                params.put("screen_lock_type", "4位数字");
                params.put("screen_lock", password);
                params.put("api_token", ApiHelper.checkApiToken("/api/v1.1/device/screen_lock"));
                params.put("id", deviceID);
                HttpUtil.httpPost(urlString, params);
            }
        });
    }

    public static void actionLog(String actionContent) {
        try {
            JSONObject logParams = new JSONObject();
            logParams.put(ACTION, actionContent);
            actionLog(logParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传用户行为
     * @param param   用户行为
     */
    public static void actionLog(final JSONObject param) {
        priorityThreadPool.execute(new PriorityRunnable(1) {
            @Override
            public void doSth() {
                try {
                    SharedPreferences mUserSP = globalContext.getApplicationContext().getSharedPreferences("UserBean", MODE_PRIVATE);

                    param.put(USER_ID, mUserSP.getString(USER_ID, ""));
                    param.put(USER_NUM, mUserSP.getString(USER_NUM, ""));
                    param.put(USER_NAME, mUserSP.getString(USER_NAME, ""));
                    param.put(K.K_USER_DEVICE_ID, mUserSP.getString(K.K_USER_DEVICE_ID, ""));

                    PackageInfo packageInfo = globalContext.getPackageManager().getPackageInfo(globalContext.getPackageName(), 0);
                    param.put(K_APP_VERSION, String.format("a%s", packageInfo.versionName));
                    param.put("coordinate", mUserSP.getString("coordinate", ""));

                    JSONObject params = new JSONObject();
                    params.put("action_log", param);

                    JSONObject userParams = new JSONObject();
                    userParams.put(K_USER_NAME, mUserSP.getString(K_USER_NAME, ""));
                    userParams.put("user_pass", mUserSP.getString(USER_PASS, ""));
                    params.put("user", userParams);

                    params.put("api_token", ApiHelper.checkApiToken("/api/v1.1/device/logger"));

                    String urlString = String.format(K.K_ACTION_LOG, BuildConfig.BASE_URL);
                    HttpUtil.httpPost(urlString, params);

                } catch (JSONException | PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 上传登录失败行为
     * @param param   用户行为
     */
    public static void actionLoginLog(final JSONObject param) {
        priorityThreadPool.execute(new PriorityRunnable(1) {
            @Override
            public void doSth() {
                try {
                    SharedPreferences mUserSP = globalContext.getApplicationContext().getSharedPreferences("UserBean", MODE_PRIVATE);

                    PackageInfo packageInfo = globalContext.getPackageManager().getPackageInfo(globalContext.getPackageName(), 0);
                    param.put(K_APP_VERSION, String.format("a%s", packageInfo.versionName));
                    param.put("coordinate", mUserSP.getString("coordinate", ""));

                    JSONObject params = new JSONObject();
                    params.put("action_log", param);
                    params.put("api_token", ApiHelper.checkApiToken("/api/v1.1/device/logger"));

                    String urlString = String.format(K.K_ACTION_LOG, BuildConfig.BASE_URL);
                    HttpUtil.httpPost(urlString, params);
                } catch (JSONException | PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
