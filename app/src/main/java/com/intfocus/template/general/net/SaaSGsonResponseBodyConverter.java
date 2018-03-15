package com.intfocus.template.general.net;

import com.google.gson.Gson;
import com.intfocus.template.model.response.BaseResultMember;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * ****************************************************
 * author jameswong
 * created on: 18/03/13 下午5:48
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
public class SaaSGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final Type type;

    SaaSGsonResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        BaseResultMember baseResultMember = gson.fromJson(response, BaseResultMember.class);
        if (baseResultMember.getCode() == 0) {
            return gson.fromJson(response, type);
        } else {
            throw new ResultException(baseResultMember.getCode(), baseResultMember.getMsg());
        }
    }
}

