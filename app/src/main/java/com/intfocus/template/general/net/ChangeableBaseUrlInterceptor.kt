package com.intfocus.template.general.net

/**
 * ****************************************************
 * author jameswong
 * created on: 18/01/17 上午10:25
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/** An interceptor that allows runtime changes to the URL hostname.  */
class ChangeableBaseUrlInterceptor : Interceptor {
    @Volatile private var host: HttpUrl? = null

    fun setHost(url: String) {
        this.host = HttpUrl.parse(url)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
         val newRequest = host?.let {
            val newUrl = chain.request().url().newBuilder()
                    .scheme(it.scheme())
                    .host(it.url().toURI().host)
                    .port(it.port())
                    .build()

            return@let chain.request().newBuilder()
                    .url(newUrl)
                    .build()
        }

         return if(newRequest!=null){
             chain.proceed(newRequest)
         }else{
             chain.proceed(chain.request())
         }
    }
}
