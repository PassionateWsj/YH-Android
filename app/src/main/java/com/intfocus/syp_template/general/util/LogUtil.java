package com.intfocus.syp_template.general.util;

import android.util.Log;

import com.intfocus.syp_template.BuildConfig;

/**
 * Created by lijunjie on 16/7/22.
 */
public class LogUtil {
    public static final String TAG = "hjjzzz";

    /**
     * Log.d(tag, str, limit)
     */
    public static void d(String tag, String str, int limit) {
        int maxLength = 2000;
        str = str.trim();
        Log.d(tag, str.substring(0, str.length() > maxLength ? maxLength : str.length()));
        if (str.length() > maxLength && limit < 4) {
            str = str.substring(maxLength, str.length());
            LogUtil.d(tag, str, limit);
        }
    }

    /**
     * Log.d(tag, str)
     */
    public static void d(String tag, String str) {
        /**
         * 若应用不处于 DEBUG 模式，则不打印输出信息
         */
        if (!BuildConfig.DEBUG) {
            return;
        }

        LogUtil.d(tag, str, 0);
    }

    /**
     * Log.d(tag, str)
     */
    public static void d(Object obj, String str) {
        /**
         * 若应用不处于 DEBUG 模式，则不打印输出信息
         */
        if (!BuildConfig.DEBUG) {
            return;
        }

        LogUtil.d(obj.getClass().getSimpleName(), str, 0);
    }


    /**
     * Log.e(tag, str)
     */
    public static void e(String tag, String str) {
        /**
         * 若应用不处于 DEBUG 模式，则不打印输出信息
         */
        if (!BuildConfig.DEBUG) {
            return;
        }

        Log.e(tag, str);
    }

    /**
     * 为大log使用
     * log最大输出为4000(包含tag得长度)
     * tag长度未知，所以分段输出
     *
     * @param tag
     * @param content
     */
    public static void largeLogD(String tag, String content) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (content.length() > 3000) {
            d(tag, content.substring(0, 3000));
            largeLogD(tag, content.substring(3000));
        } else {
            d(tag, content);
        }
    }
}
