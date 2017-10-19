package com.intfocus.yhdev.subject.template_v1.mode;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONReader;
import com.intfocus.yhdev.subject.template_v1.entity.MererDetalEntity;
import com.intfocus.yhdev.subject.template_v1.entity.msg.MDetalActRequestResult;
import com.intfocus.yhdev.util.ApiHelper;
import com.intfocus.yhdev.util.FileUtil;
import com.intfocus.yhdev.util.K;
import com.zbl.lib.baseframe.core.AbstractMode;
import com.zbl.lib.baseframe.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import static com.intfocus.yhdev.YHApplication.threadPool;

/**
 * 仪表盘-数据处理模块
 *
 * @author zbaoliang
 * @date 17-4-28
 */
public class MeterDetailActMode extends AbstractMode {

    String TAG = MeterDetailActMode.class.getSimpleName();
    String group_id;
    String report_id;

    Context ctx;

    MererDetalEntity entity;

    public MeterDetailActMode(Context ctx) {
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
                try {
                    String response;
                    String jsonFileName = String.format("group_%s_template_%s_report_%s.json", group_id, "1", report_id);
                    String jsonFilePath = FileUtil.dirPath(ctx, K.kCachedDirName, jsonFileName);
                    boolean dataState = ApiHelper.reportJsonData(ctx, group_id, "1", report_id);
                    if (dataState || new File(jsonFilePath).exists()) {
                        response = FileUtil.readFile(jsonFilePath);
                    }
                    else {
                        MDetalActRequestResult result1 = new MDetalActRequestResult(true, 400, null);
                        EventBus.getDefault().post(result1);
                        return;
                    }
//                    response = getJsonData(ctx);
                    Log.i(TAG, "analysisDataStartTime:" + TimeUtil.getNowTime());
                    StringReader stringReader = new StringReader(response);
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

                                            default:
                                                break;
                                        }
                                    }
                                    reader.endObject();
                                    entity.data.add(data);
                                }
                                reader.endArray();
                                Log.i(TAG, "dataEnd:" + TimeUtil.getNowTime());
                                break;

                            default:
                                break;
                        }
                    }
                    reader.endObject();
                    reader.endArray();
                    EventBus.getDefault().post(new MDetalActRequestResult(true, 200, entity));
                    Log.i(TAG, "analysisDataEndTime:" + TimeUtil.getNowTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 加载 模板一 本地 json 测试数据的方法
     * @param context
     * @return
     */
    private String getJsonData(Context context) {
        InputStream is = null;
        BufferedReader reader = null;
        StringBuilder sb = null;
        try {
            is = ctx.getResources().getAssets().open("kpi_detaldata.json");
//            is = context.getResources().getAssets().open("temple-v1.json");
            reader = new BufferedReader(new InputStreamReader(is));
            sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
