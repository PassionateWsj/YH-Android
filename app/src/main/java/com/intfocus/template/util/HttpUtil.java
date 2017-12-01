package com.intfocus.template.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.intfocus.template.ConfigConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.intfocus.template.constant.Params.BODY;
import static com.intfocus.template.constant.Params.CODE;
import static com.intfocus.template.constant.Params.ETAG;
import static com.intfocus.template.constant.Params.LAST_MODIFIED;

/**
 * @author lijunjie
 */
public class HttpUtil {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String kUserAgent = "User-Agent";
    public static final String kContentType = "Content-Type";
    public static final String kFailedToConnectTo = "failed to connect to";
    public static final String kUnauthorized = "unauthorized";
    public static final String kUnableToResolveHost = "unable to resolve host";
    public static final String kApplicationJson = "application/json";
    public static final String kAccept = "Accept";

    /**
     * ִ执行一个HTTP GET请求，返回请求响应的HTML
     *
     * @param urlString 请求的URL地址
     * @return 返回请求响应的HTML
     */
    public static Map<String, String> httpGet(Context ctx, String urlString, Map<String, String> headers) {
        LogUtil.d("GET", urlString);
        SharedPreferences mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE);
        String userNum = mUserSP.getString("user_num", "0");
        String userDeviceId = mUserSP.getString("user_device_id", "0");
        String appVision = mUserSP.getString("app_version", "0");

        Map<String, String> retMap = new HashMap<>();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        okhttp3.Request.Builder builder = new Request.Builder();

        builder.url(urlString).addHeader(kUserAgent, HttpUtil.webViewUserAgent());

        if (headers.containsKey(ETAG)) {
            builder = builder.addHeader("IF-None-Match", headers.get(ETAG));
        }
        if (headers.containsKey(LAST_MODIFIED)) {
            builder = builder.addHeader("If-Modified-Since", headers.get(LAST_MODIFIED));
        }
        Response response;
        Request baseRequest = builder.build();

        //提取api_path
        String apiPath = baseRequest.url().toString().replace(ConfigConstants.kBaseUrl, "");
        if (apiPath.contains("?")) {
            apiPath = apiPath.substring(0, apiPath.indexOf("?"));
        }

        //根据规则加密生成api_token
        String apiToken = Utils.getApiToken(apiPath);

        HttpUrl.Builder httpUrl = baseRequest.url().newBuilder()
                .addQueryParameter(K.API_TOKEN, apiToken)
                .addQueryParameter("_user_num", userNum)
                .addQueryParameter("_user_device_id", userDeviceId)
                .addQueryParameter("_app_version", appVision);

        // 新的请求--添加参数
        Request newRequest = baseRequest.newBuilder()
                .method(baseRequest.method(), baseRequest.body())
                .url(httpUrl.build())
                .build();

        try {
            response = client.newCall(newRequest).execute();
            Headers responseHeaders = response.headers();
            boolean isJSON = false;
            for (int i = 0, len = responseHeaders.size(); i < len; i++) {
                retMap.put(responseHeaders.name(i), responseHeaders.value(i));
                isJSON = responseHeaders.name(i).equalsIgnoreCase(kContentType) && responseHeaders.value(i).contains(kApplicationJson);
            }
            retMap.put(CODE, String.format("%d", response.code()));
            retMap.put(BODY, response.body().string());

            if (isJSON) {
                LogUtil.d("code", retMap.get("code"));
                LogUtil.d("responseBody", retMap.get("body"));
            }
        } catch (UnknownHostException e) {
            // 400: Unable to resolve host "yonghui.idata.mobi": No address associated with hostname
            if (e.getMessage() != null) {
                LogUtil.d("UnknownHostException2", e.getMessage());
            }
            retMap.put(CODE, "400");
            retMap.put(BODY, "{\"info\": \"请检查网络环境！\"}");
        } catch (Exception e) {
            // Default Response
            retMap.put(CODE, "400");
            retMap.put(BODY, "{\"info\": \"请检查网络环境！\"}");

            if (e.getMessage() != null) {
                String errorMessage = e.getMessage().toLowerCase();
                LogUtil.d("Exception", errorMessage);
                if (errorMessage.contains(kUnableToResolveHost) || errorMessage.contains(kFailedToConnectTo)) {
                    retMap.put(CODE, "400");
                    retMap.put(BODY, "{\"info\": \"请检查网络环境！\"}");
                } else if (errorMessage.contains(kUnauthorized)) {
                    retMap.put(CODE, "401");
                    retMap.put(BODY, "{\"info\": \"用户名或密码错误\"}");
                }
            }
        }
        return retMap;
    }

    /**
     * ִ执行一个HTTP POST请求，返回请求响应的HTML
     */
    public static Map<String, String> httpPost(String urlString, Map params) {
        LogUtil.d("POST", urlString);
        Map<String, String> retMap = new HashMap<>();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        Request request;
        Response response;
        Request.Builder requestBuilder = new Request.Builder();
        if (params != null) {
            try {
                Iterator iter = params.entrySet().iterator();
                JSONObject holder = new JSONObject();

                while (iter.hasNext()) {
                    Map.Entry pairs = (Map.Entry) iter.next();
                    String key = (String) pairs.getKey();

                    if (pairs.getValue() instanceof Map) {
                        Map m = (Map) pairs.getValue();

                        JSONObject data = new JSONObject();
                        for (Object o : m.entrySet()) {
                            Map.Entry pairs2 = (Map.Entry) o;
                            data.put((String) pairs2.getKey(), pairs2.getValue());
                            holder.put(key, data);
                        }
                    } else {
                        holder.put(key, pairs.getValue());
                    }
                }
                requestBuilder.post(RequestBody.create(JSON, holder.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            request = requestBuilder
                    .url(urlString)
                    .addHeader(kAccept, kApplicationJson)
                    .addHeader(kContentType, kApplicationJson)
                    .addHeader(kUserAgent, HttpUtil.webViewUserAgent())
                    .build();
            response = client.newCall(request).execute();

            Headers responseHeaders = response.headers();
            int headerSize = responseHeaders.size();
            for (int i = 0; i < headerSize; i++) {
                retMap.put(responseHeaders.name(i), responseHeaders.value(i));
            }

            retMap.put(CODE, String.format("%d", response.code()));
            retMap.put(BODY, response.body().string());

            LogUtil.d("code", retMap.get("code"));
            LogUtil.d("responseBody", retMap.get("body"));
        } catch (UnknownHostException e) {
            if (e != null && e.getMessage() != null) {
                LogUtil.d("UnknownHostException", e.getMessage());
            }
            retMap.put(CODE, "400");
            retMap.put(BODY, "{\"info\": \"请检查网络环境！\"}");
        } catch (Exception e) {
            // Default Response
            retMap.put(CODE, "400");
            retMap.put(BODY, "{\"info\": \"请检查网络环境！\"}");

            if (e != null && e.getMessage() != null) {
                String errorMessage = e.getMessage().toLowerCase();
                LogUtil.d("Exception", errorMessage);
                if (errorMessage.contains(kUnableToResolveHost) || errorMessage.contains(kFailedToConnectTo)) {
                    retMap.put(CODE, "400");
                    retMap.put(BODY, "{\"info\": \"请检查网络环境！\"}");
                } else if (errorMessage.contains(kUnauthorized)) {
                    retMap.put(CODE, "401");
                    retMap.put(BODY, "{\"info\": \"用户名或密码错误\"}");
                }
            }
        }
        return retMap;
    }

    /**
     * ִ执行一个HTTP POST请求，返回请求响应的HTML
     */
    public static Map<String, String> httpPost(String urlString, JSONObject params) {
        LogUtil.d("POST2", urlString);
        Map<String, String> retMap = new HashMap<>();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build();
        Request request;
        Response response;
        Request.Builder requestBuilder = new Request.Builder();

        if (params != null) {
            requestBuilder.post(RequestBody.create(JSON, params.toString()));
            LogUtil.d("PARAM", params.toString());
        }
        try {
            request = requestBuilder
                    .url(urlString)
                    .addHeader(kAccept, kApplicationJson)
                    .addHeader(kContentType, kApplicationJson)
                    .addHeader(kUserAgent, HttpUtil.webViewUserAgent())
                    .build();
            response = client.newCall(request).execute();
            Headers responseHeaders = response.headers();
            for (int i = 0, headerSize = responseHeaders.size(); i < headerSize; i++) {
                retMap.put(responseHeaders.name(i), responseHeaders.value(i));
                LogUtil.d("HEADER", String.format("Key : %s, Value: %s", responseHeaders.name(i),
                        responseHeaders.value(i)));
            }
            retMap.put(CODE, String.format("%d", response.code()));
            retMap.put(BODY, response.body().string());

            LogUtil.d("code", retMap.get("code"));
            LogUtil.d("responseBody", retMap.get("body"));
        } catch (UnknownHostException e) {
            if (e != null && e.getMessage() != null) {
                LogUtil.d("UnknownHostException2", e.getMessage());
            }
            retMap.put(CODE, "400");
            retMap.put(BODY, "{\"info\": \"请检查网络环境！\"}");
        } catch (Exception e) {
            retMap.put(CODE, "400");
            retMap.put(BODY, "{\"info\": \"请检查网络环境！\"}");

            if (e != null && e.getMessage() != null) {
                String errorMessage = e.getMessage().toLowerCase();
                LogUtil.d("Exception2", errorMessage);
                if (errorMessage.contains(kUnableToResolveHost) || errorMessage.contains("failed to connect to")) {
                    retMap.put(CODE, "400");
                    retMap.put(BODY, "{\"info\": \"请检查网络环境！\"}");
                } else if (errorMessage.contains(kUnauthorized)) {
                    retMap.put(CODE, "401");
                    retMap.put(BODY, "{\"info\": \"用户名或密码错误\"}");
                }
            }
        }
        return retMap;
    }

    public static String urlToFileName(String urlString) {
        String path = "default";
        try {
            urlString = urlString.replace(ConfigConstants.kBaseUrl, "");
            URI uri = new URI(urlString);
            path = uri.getPath().replace("/", "_");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("%s.html", path);
    }

    public static String webViewUserAgent() {
        String userAgent = System.getProperty("http.agent");
        if (userAgent == null) {
            userAgent = "Mozilla/5.0 (Linux; U; Android 4.3; en-us; HTC One - 4.3 - API 18 - 1080x1920 Build/JLS36G) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30 default-by-hand";
        }

        return userAgent;
    }

    /**
     * Check if there is any connectivity
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isAvailable());
    }

    public static Map<String, String> downloadResponse(String urlString, Map<String, String> headers) {
        Map<String, String> response = new HashMap<>(16);
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", HttpUtil.webViewUserAgent());
            connection.setConnectTimeout(5 * 1000);
            connection.setReadTimeout(10 * 1000);
            if (headers.containsKey(ETAG)) {
                connection.setRequestProperty("IF-None-Match", headers.get(ETAG));
            }
            if (headers.containsKey(LAST_MODIFIED)) {
                connection.setRequestProperty("If-Modified-Since", headers.get(LAST_MODIFIED));
            }

            connection.connect();
            response.put(CODE, String.valueOf(connection.getResponseCode()));
            Map<String, List<String>> map = connection.getHeaderFields();
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                response.put(entry.getKey(), entry.getValue().get(0));
            }
        } catch (Exception e) {
            LogUtil.d("Exception", e.toString());
            response.put(CODE, "400");
            return response;
        }
        return response;
    }

    /**
     * Zip 档下载
     */
    public static Map<String, String> downloadZip(String urlString, String outputPath, Map<String, String> headers) {
        Map<String, String> response = new HashMap<>();
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", HttpUtil.webViewUserAgent());
            connection.setConnectTimeout(5 * 1000);
            connection.setReadTimeout(10 * 1000);
            if (headers.containsKey(ETAG)) {
                connection.setRequestProperty("IF-None-Match", headers.get(ETAG));
            }
            if (headers.containsKey(LAST_MODIFIED)) {
                connection.setRequestProperty("If-Modified-Since", headers.get(LAST_MODIFIED));
            }

            connection.connect();
            response.put(CODE, String.format("%d", connection.getResponseCode()));
            Map<String, List<String>> map = connection.getHeaderFields();
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                response.put(entry.getKey(), entry.getValue().get(0));
            }
            LogUtil.d("DownloadZIP", String.format("%d - %s - %s", connection.getResponseCode(), urlString, response.toString()));
            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return response;
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            input = connection.getInputStream();
            output = new FileOutputStream(outputPath);

            byte[] data = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            LogUtil.d("Exception", e.toString());
            response.put(CODE, "400");
            return response;
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException ignored) {
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }

    /**
     * 判断当前网络状态是否为 WiFi
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }

        return false;
    }
}
