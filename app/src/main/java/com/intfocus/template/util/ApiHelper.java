package com.intfocus.template.util;

import android.content.Context;
import android.text.TextUtils;

import com.intfocus.template.ConfigConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.intfocus.template.SYPApplication.globalContext;
import static com.intfocus.template.constant.Params.BODY;
import static com.intfocus.template.constant.Params.CODE;
import static com.intfocus.template.constant.Params.ETAG;
import static com.intfocus.template.constant.Params.LAST_MODIFIED;

public class ApiHelper {

    /**
     *  获取报表 JSON 数据
     */
    public static boolean reportJsonData(Context context, String groupID, String templateID, String reportID) {
        String urlString = String.format(K.K_REPORT_JSON_ZIP_API_PATH, ConfigConstants.kBaseUrl, groupID, templateID, reportID);
        String assetsPath = FileUtil.sharedPath(context);
        Map<String, String> headers = ApiHelper.checkResponseHeader(urlString);
        String jsonFileName = String.format("group_%s_template_%s_report_%s.json", groupID, templateID, reportID);
        String cachedZipPath = FileUtil.dirPath(context, K.K_CACHED_DIR_NAME, String.format("%s.zip", jsonFileName));
        Map<String, String> response = HttpUtil.downloadZip(urlString, cachedZipPath, headers);
        String jsonFilePath = FileUtil.dirPath(context, K.K_CACHED_DIR_NAME, jsonFileName);

        //添加code字段是否存在。原因:网络不好的情况下response为{}
        if (!response.containsKey(CODE)) {
            return false;
        }

        String codeStatus = response.get(CODE);
        switch (codeStatus) {
            case "200":

            case "201":
                break;
            case "304":
                if (new File(jsonFilePath).exists()) {
                    return true;
                }
                break;
            default:
                return false;
        }

        try {
            ApiHelper.storeResponseHeader(urlString, response);

            InputStream zipStream = new FileInputStream(cachedZipPath);
            FileUtil.unZip(zipStream, FileUtil.dirPath(context, K.K_CACHED_DIR_NAME), true);
            zipStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            deleteHeadersFile();
            return false;
        }
        return true;
    }

    public static void deleteHeadersFile() {
        String assetsPath = FileUtil.dirPath(globalContext, K.K_HTML_DIR_NAME);
        String headersFilePath = String.format("%s/%s", assetsPath, K.K_CACHED_HEADER_CONFIG_FILE_NAME);
        if ((new File(headersFilePath)).exists()) {
            new File(headersFilePath).delete();
        }
    }

    public static Map<String, String> httpGetWithHeader(String urlString) {
        Map<String, String> retMap = new HashMap<>(16);
        String assetsPath = FileUtil.dirPath(globalContext, K.K_HTML_DIR_NAME);
        String relativeAssetsPath = "../../Shared/assets";
        String urlKey = urlString.contains("?") ? TextUtils.split(urlString, "?")[0] : urlString;

        try {
            Map<String, String> headers = new HashMap<>(16);
            Map<String, String> response = HttpUtil.httpGet(globalContext, urlKey, headers);

            String statusCode = response.get(CODE);
            retMap.put(CODE, statusCode);

            String htmlName = HttpUtil.urlToFileName(urlString);
            String htmlPath = String.format("%s/%s", assetsPath, htmlName);
            retMap.put("path", htmlPath);

            if ("200".equals(statusCode)) {
                ApiHelper.storeResponseHeader(urlKey, response);

                String htmlContent = response.get(BODY);

                htmlContent = htmlContent.replace("/javascripts/", String.format("%s/javascripts/", relativeAssetsPath));
                htmlContent = htmlContent.replace("/stylesheets/", String.format("%s/stylesheets/", relativeAssetsPath));
                htmlContent = htmlContent.replace("/images/", String.format("%s/images/", relativeAssetsPath));
                FileUtil.writeFile(htmlPath, htmlContent);
            } else {
                retMap.put(CODE, statusCode);
            }
        } catch (Exception e) {
            retMap.put(CODE, "500");
            e.printStackTrace();
        }
        return retMap;
    }

    /**
     * 缓存文件中，清除指定链接的内容
     *
     *  链接
     *  缓存头文件相对文件夹
     */
    public static void clearResponseHeader(String urlKey) {
        String assetsPath = FileUtil.dirPath(globalContext, K.K_HTML_DIR_NAME);
        String headersFilePath = String.format("%s/%s", assetsPath, K.K_CACHED_HEADER_CONFIG_FILE_NAME);
        if (!(new File(headersFilePath)).exists()) {
            return;
        }

        JSONObject headersJSON = FileUtil.readConfigFile(headersFilePath);
        if (headersJSON.has(urlKey)) {
            try {
                headersJSON.remove(urlKey);
                LogUtil.d("clearResponseHeader", String.format("%s[%s]", headersFilePath, urlKey));

                FileUtil.writeFile(headersFilePath, headersJSON.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从缓存头文件中，获取指定链接的ETag/Last-Modified
     *
     * @param urlKey     链接
     */
    public static Map<String, String> checkResponseHeader(String urlKey) {
        String assetsPath = FileUtil.dirPath(globalContext, K.K_HTML_DIR_NAME);
        String headersFilePath = String.format("%s/%s", assetsPath, K.K_CACHED_HEADER_CONFIG_FILE_NAME);
        Map<String, String> headers = new HashMap<>(16);

        try {
            JSONObject headersJSON = new JSONObject();
            if ((new File(headersFilePath)).exists()) {
                headersJSON = FileUtil.readConfigFile(headersFilePath);
            }

            JSONObject headerJSON;
            if (headersJSON.has(urlKey)) {
                headerJSON = (JSONObject) headersJSON.get(urlKey);

                if (headerJSON.has(ETAG)) {
                    headers.put(ETAG, headerJSON.getString(ETAG));
                }
                if (headerJSON.has(LAST_MODIFIED)) {
                    headers.put(LAST_MODIFIED, headerJSON.getString(LAST_MODIFIED));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return headers;
    }

    /**
     * 把服务器响应的ETag/Last-Modified存入本地
     *
     * @param urlKey     链接
     * @param response   服务器响应的ETag/Last-Modifiede
     */
    public static void storeResponseHeader(String urlKey, Map<String, String> response) {
        String assetsPath = FileUtil.dirPath(globalContext, K.K_HTML_DIR_NAME);
        String headersFilePath = String.format("%s/%s", assetsPath, K.K_CACHED_HEADER_CONFIG_FILE_NAME);

        try {
            JSONObject headersJSON = new JSONObject();
            if ((new File(headersFilePath)).exists()) {
                headersJSON = FileUtil.readConfigFile(headersFilePath);
            }

            JSONObject headerJSON = new JSONObject();

            if (response.containsKey(ETAG)) {
                headerJSON.put(ETAG, response.get(ETAG));
            }
            if (response.containsKey(LAST_MODIFIED)) {
                headerJSON.put(LAST_MODIFIED, response.get(LAST_MODIFIED));
            }

            headersJSON.put(urlKey, headerJSON);
            FileUtil.writeFile(headersFilePath, headersJSON.toString());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 合并两个JSONObject
     *
     * @param obj   JSONObject
     * @param other JSONObject
     * @return 合并后的JSONObject
     */
    public static JSONObject mergeJson(JSONObject obj, JSONObject other) {
        try {
            Iterator it = other.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                obj.put(key, other.get(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }

    /**
     * 下载文件
     *
     * @param context    上下文
     * @param urlString  下载链接
     * @param outputFile 写入本地文件路径
     */
    public static void downloadFile(Context context, String urlString, File outputFile) {
        try {
            URL url = new URL(urlString);
            String headerPath = String.format("%s/%s/%s", FileUtil.basePath(context), K.K_CACHED_DIR_NAME, K.K_CACHED_HEADER_CONFIG_FILE_NAME);

            File cachePath = new File(String.format("%s/%s", FileUtil.basePath(context), K.K_CACHED_DIR_NAME));
            if (!cachePath.exists()) {
                cachePath.mkdirs();
            }
            JSONObject headerJSON = new JSONObject();
            if ((new File(headerPath)).exists()) {
                headerJSON = FileUtil.readConfigFile(headerPath);
            }

            URLConnection conn = url.openConnection();
            String etag = conn.getHeaderField(ETAG);

            boolean isDownloaded = outputFile.exists() && headerJSON.has(urlString) && etag != null && !etag.isEmpty() && headerJSON.getString(urlString).equals(etag);

            if (isDownloaded) {
                LogUtil.d("downloadFile", "exist - " + outputFile.getAbsolutePath());
            } else {
                InputStream in = url.openStream();
                FileOutputStream fos = new FileOutputStream(outputFile);

                int length;
                byte[] buffer = new byte[1024];// buffer for portion of data from connection
                while ((length = in.read(buffer)) > -1) {
                    fos.write(buffer, 0, length);
                }
                fos.close();
                in.close();

                if (etag != null && !etag.isEmpty()) {
                    headerJSON.put(urlString, etag);
                    FileUtil.writeFile(headerPath, headerJSON.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String checkApiToken(String url) {
        return URLs.MD5(K.ANDROID_API_KEY + url + K.ANDROID_API_KEY);
    }
}
