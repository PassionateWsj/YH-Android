package com.intfocus.yhdev.subject.template_v1.mode;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intfocus.yhdev.subject.template_v1.entity.MererDetalEntity;
import com.intfocus.yhdev.subject.template_v1.entity.Testbean;
import com.intfocus.yhdev.subject.template_v1.entity.msg.MDetalActRequestResult;
import com.intfocus.yhdev.util.ApiHelper;
import com.intfocus.yhdev.util.FileUtil;
import com.intfocus.yhdev.util.HttpUtil;
import com.intfocus.yhdev.util.K;
import com.zbl.lib.baseframe.core.AbstractMode;
import com.zbl.lib.baseframe.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.intfocus.yhdev.YHApplication.threadPool;


/**
 * 仪表盘-数据处理模块
 * Created by zbaoliang on 17-4-28.
 */
public class MeterDetalActMode extends AbstractMode {

    String TAG = MeterDetalActMode.class.getSimpleName();
    String group_id;
    String report_id;

    Context ctx;

    MererDetalEntity entity;

    public MeterDetalActMode(Context ctx) {
        this.ctx = ctx;
    }

    public void requestData(String group_id, String report_id) {
        this.group_id = group_id;
        this.report_id = report_id;
        requestData();
    }

    @Override
    public void requestData() {
        entity = null;
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
//                try {

//                InputStream is = null;
//                try {
//                    is = ctx.getAssets().open("kpi_detaldata.json");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                InputStreamReader isr = new InputStreamReader(is);
//                JSONReader reader = new JSONReader(isr);
//                reader.startArray();
//                reader.startObject();

                Log.i(TAG, "requestStartTime:" + TimeUtil.getNowTime());
                String urlString = String.format(K.kReportJsonAPIPath, K.kBaseUrl, group_id, "1", report_id);
                String assetsPath = FileUtil.sharedPath(ctx);
                String itemsString;
                Map<String, String> headers = ApiHelper.checkResponseHeader(urlString, assetsPath);
                Map<String, String> response = HttpUtil.httpGet(ctx, urlString, new HashMap<String, String>());
                Log.i(TAG, "requestEndTime:" + TimeUtil.getNowTime());
                if (!"200".equals(response.get("code")) && !"304".equals(response.get("code"))) {
                    MDetalActRequestResult result1 = new MDetalActRequestResult(true, 400, null);
                    EventBus.getDefault().post(result1);
                    return;
                }
                ApiHelper.storeResponseHeader(urlString, assetsPath, response);
                //请求数据成功
                itemsString = response.get("body").toString();
                if (TextUtils.isEmpty(itemsString)) {
                    itemsString = FileUtil.readFile(assetsPath + K.kTemplateV1);//取数据
                } else {
                    try {
                        FileUtil.writeFile(assetsPath + K.kTemplateV1, itemsString);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (TextUtils.isEmpty(itemsString)) {
                    MDetalActRequestResult result1 = new MDetalActRequestResult(true, 400, null);
                    EventBus.getDefault().post(result1);
                    return;
                }
                Log.i(TAG, "analysisDataStartTime:" + TimeUtil.getNowTime());
                StringReader stringReader = new StringReader(itemsString);
                Log.i(TAG, "analysisDataReaderTime1:" + TimeUtil.getNowTime());
                JSONReader reader = new JSONReader(stringReader);
                reader.startArray();
                reader.startObject();

                entity = new MererDetalEntity();
                entity.data = new ArrayList<>();
                Log.i(TAG, "analysisDataReaderTime2:" + TimeUtil.getNowTime());

                while (reader.hasNext()) {
                    String key = reader.readString();
                    switch (key) {
                        case "name":
                            String name = reader.readObject().toString();
                            entity.name = name;
                            Log.i(TAG, "name:" + TimeUtil.getNowTime());
                            break;

                        case "data":
                            Log.i(TAG, "dataStart:" + TimeUtil.getNowTime());
                            reader.startArray();
                            while (reader.hasNext()) {
                                reader.startObject();
                                MererDetalEntity.PageData data = new MererDetalEntity.PageData();
                                while (reader.hasNext()) {
                                    String dataKey = reader.readString();
                                    switch (dataKey) {
                                        case "parts":
                                            String parts = reader.readObject().toString();
                                            data.parts = parts;
                                            break;

                                        case "title":
                                            String title = reader.readObject().toString();
                                            data.title = title;
                                            break;
                                    }
                                }
                                reader.endObject();
                                entity.data.add(data);
                            }
                            reader.endArray();
                            Log.i(TAG, "dataEnd:" + TimeUtil.getNowTime());
                            break;
                    }
                }
                reader.endObject();
                reader.endArray();
                EventBus.getDefault().post(new MDetalActRequestResult(true, 200, entity));
                Log.i(TAG, "analysisDataEndTime:" + TimeUtil.getNowTime());
            }
        });
    }
}
