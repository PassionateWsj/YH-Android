package com.intfocus.yhdev.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;


/**
 * ****************************************************
 * author: jameswong
 * created on: 17/09/12 上午10:56
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
public class ProessUtil {
    /**
     * 判断进程是否运行
     *
     * @return
     */
    public static boolean isProessRunning(Context context, String proessName) {

        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : lists) {
            if (info.processName.equals(proessName)) {
                isRunning = true;
            }
        }

        return isRunning;
    }

    /**
     * 获取运行栈信息
     *
     * @param context
     * @return
     */
    public static ActivityManager.RunningTaskInfo getRunningTaskInfo(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        return manager.getRunningTasks(1).get(0);
    }

    /**
     * 获取当前运行的类名
     *
     * @param context
     * @return
     */
    public static String getShortClassName(Context context) {
        return getRunningTaskInfo(context).topActivity.getShortClassName();    //类名
    }

    /**
     * 获取当前运行的完整类名
     *
     * @param context
     * @return
     */
    public static String getClassName(Context context) {
        return getRunningTaskInfo(context).topActivity.getClassName();    //类名
    }

    /**
     * 获取当前运行的包名
     *
     * @param context
     * @return
     */
    public static String getPaceageName(Context context) {
        return getRunningTaskInfo(context).topActivity.getPackageName();    //包名
    }

}
