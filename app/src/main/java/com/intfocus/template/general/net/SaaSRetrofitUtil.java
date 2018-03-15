package com.intfocus.template.general.net;

import android.content.Context;

import okhttp3.Interceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * ****************************************************
 * author jameswong
 * created on: 18/03/13 下午5:00
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
public class SaaSRetrofitUtil extends RetrofitUtil {

    private static SaaSRetrofitUtil mInstance = null;
    private HttpServiceKotlin mHttpServiceKotlin = null;

    /**
     * 双重校验锁单例模式
     *
     * @param context
     * @return
     */
    public static synchronized SaaSRetrofitUtil getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SaaSRetrofitUtil.class) {
                if (mInstance == null) {
                    mInstance = new SaaSRetrofitUtil(context);
                }
            }
        }
        return mInstance;
    }

    public static void destroyInstance() {
        mInstance = null;
    }

    private SaaSRetrofitUtil(Context context) {
        this.ctx = context;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://shengyiplus.idata.mobi")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                 /*设置 自定义ResponseConverterFactory处理服务器返回错误码*/
                .addConverterFactory(SaaSResponseConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                 /*设置 ScalarsConverterFactory返回纯String*/
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(getClientBuilder().build())
                .build();
        mHttpServiceKotlin = retrofit.create(HttpServiceKotlin.class);
    }

    @Override
    public Interceptor setBaseParamsInterceptor() {
        return new SaaSBaseParamsInterceptor(ctx);
    }

    public static HttpServiceKotlin getHttpServiceKotlin(Context ctx) {
        return getInstance(ctx).mHttpServiceKotlin;
    }

}
