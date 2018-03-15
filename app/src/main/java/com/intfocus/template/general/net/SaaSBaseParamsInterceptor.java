package com.intfocus.template.general.net;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.intfocus.template.constant.Params.DATASOURCE_CODE;
import static com.intfocus.template.constant.Params.GROUP_ID;
import static com.intfocus.template.constant.Params.ROLE_ID;
import static com.intfocus.template.constant.Params.USER_BEAN;
import static com.intfocus.template.constant.Params.USER_NUM;

/**
 * ****************************************************
 * author jameswong
 * created on: 18/03/14 上午10:52
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
public class SaaSBaseParamsInterceptor implements Interceptor {
    private SharedPreferences mUserSP;

    SaaSBaseParamsInterceptor(Context ctx) {
        mUserSP = ctx.getSharedPreferences(USER_BEAN, Context.MODE_PRIVATE);
    }

    /**
     * @param chain
     * @return
     * @throws IOException
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oriRequest = chain.request();

        String userNum = mUserSP.getString(USER_NUM, "0");
        String roleId = mUserSP.getString(ROLE_ID, "0");
        String groupId = mUserSP.getString(GROUP_ID, "0");
        String dataSourceCode = mUserSP.getString(DATASOURCE_CODE, "0");

        //把api_token添加进url中
        HttpUrl.Builder authorizedUrlBuilder = oriRequest.url()
                .newBuilder()
                .scheme(oriRequest.url().scheme())
                .host(oriRequest.url().host())
                .addQueryParameter("user_num", userNum)
                .addQueryParameter("role_id", roleId)
                .addQueryParameter("group_id", groupId)
                .addQueryParameter("dataSourceCode", dataSourceCode);

        // 新的请求--添加参数
        Request newRequest = oriRequest.newBuilder()
                .method(oriRequest.method(), oriRequest.body())
                .url(authorizedUrlBuilder.build())
                .build();

        okhttp3.Response response = chain.proceed(newRequest);
        return response;
    }
}