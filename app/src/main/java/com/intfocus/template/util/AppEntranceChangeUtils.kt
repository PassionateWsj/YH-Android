package com.intfocus.template.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

/**
 * ****************************************************
 * author jameswong
 * created on: 18/01/29 上午10:28
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class AppEntranceChangeUtils(val mContext: Context) {

    companion object {
        private var INSTANCE: AppEntranceChangeUtils? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(mContext: Context): AppEntranceChangeUtils {
            return INSTANCE ?: AppEntranceChangeUtils(mContext)
                    .apply { INSTANCE = this }
        }

        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }

    private val appList = listOf(
//            "com.intfocus.template.splash.SplashActivity",
            "com.intfocus.template.splash.SYPSplashActivity",
            "com.intfocus.template.splash.YhSplashActivity",
            "com.intfocus.template.splash.BZSplashActivity"
    )

    /**
     * 启动组件
     *
     * @param componentName 组件名
     */
    private fun enableComponent(componentName: ComponentName?) {
        //此方法用以启用和禁用组件，会覆盖 Androidmanifest 文件下定义的属性
        mContext.packageManager.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP)
    }

    /**
     * 禁用组件
     *
     * @param componentName 组件名
     */
    private fun disableComponent(componentName: ComponentName?) {
        mContext.packageManager.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP)
    }

    /**
     * 禁用组件
     *
     * @param componentName 组件名
     */
    private fun checkComponent(componentName: ComponentName?): Int =
            mContext.packageManager.getComponentEnabledSetting(componentName)


    fun changeEntrance(appId: String) {
        when (appId) {
            "123f3cbe64f64735918567802048a61a", "a613ffcf1b2441c79105ff7e26ade104", "410a1997ecd245a0b876042ffb1883c3" -> {
                enableComponentByIndex(0)
            }
            "20588c54c89345c3aba7c0f2fc12781b" -> {
                enableComponentByIndex(1)
            }
            "f097ec3887ea46b0a83b1ce9f931780c" -> {
                enableComponentByIndex(2)
            }
        }
    }

    private fun enableComponentByIndex(enabledIndex: Int) {
//        var currentEnableComponentIndex: Int? = null
        appList.forEachIndexed { index, cls ->
            if (index == enabledIndex) {
                enableComponent(ComponentName(mContext, cls))
            } else {
                if (checkComponent(ComponentName(mContext, cls)) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        || checkComponent(ComponentName(mContext, cls)) == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
                    disableComponent(ComponentName(mContext, cls))
//                    currentEnableComponentIndex = index
                }
            }
        }
//        currentEnableComponentIndex?.let {
//            disableComponent(ComponentName(mContext, appList[it]))
//        }
    }
}