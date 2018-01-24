package com.intfocus.template.general.net

import com.intfocus.template.SYPApplication
import com.intfocus.template.util.HttpUtil
import okhttp3.CacheControl
import okhttp3.Interceptor

/**
 * ****************************************************
 * author jameswong
 * created on: 18/01/22 上午10:56
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class CacheInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        var request = chain.request()
        val netAvailable = HttpUtil.isConnected(SYPApplication.globalContext)

        if (netAvailable) {
            request = request.newBuilder()
                    //网络可用 强制从网络获取数据
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build()
        } else {
            request = request.newBuilder()
                    //网络不可用 从缓存获取
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build()
        }
        var response = chain.proceed(request)
        if (netAvailable) {
            response = response.newBuilder()
                    .removeHeader("Pragma")
                    // 有网络时 设置缓存超时时间1个小时
                    .header("Cache-Control", "public, max-age=" + 60)
                    .build()
        } else {
            response = response.newBuilder()
                    .removeHeader("Pragma")
                    // 无网络时，设置超时为2周
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + 14 * 24 * 60 * 60)
                    .build()
        }
        return response

    }
}