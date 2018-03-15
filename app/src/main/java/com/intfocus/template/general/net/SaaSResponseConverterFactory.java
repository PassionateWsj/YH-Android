package com.intfocus.template.general.net;

import com.google.gson.Gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * ****************************************************
 * author jameswong
 * created on: 18/03/13 下午5:47
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
public class SaaSResponseConverterFactory extends Converter.Factory {
        private final Gson gson;

        public static SaaSResponseConverterFactory create() {
            return create(new Gson());
        }

        public static SaaSResponseConverterFactory create(Gson gson) {
            return new SaaSResponseConverterFactory(gson);
        }

        private SaaSResponseConverterFactory(Gson gson) {
            if (gson == null) {
                throw new NullPointerException("gson == null");
            }
            this.gson = gson;
        }

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return new SaaSGsonResponseBodyConverter<>(gson, type);
        }
    }
