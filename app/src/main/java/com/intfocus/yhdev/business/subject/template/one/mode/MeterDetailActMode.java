package com.intfocus.yhdev.business.subject.template.one.mode;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONReader;
import com.intfocus.yhdev.business.subject.templateone.entity.MererDetailEntity;
import com.intfocus.yhdev.business.subject.templateone.entity.msg.MDetailActRequestResult;
import com.intfocus.yhdev.general.util.ApiHelper;
import com.intfocus.yhdev.general.util.FileUtil;
import com.intfocus.yhdev.general.util.K;
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

    private static String TAG = MeterDetailActMode.class.getSimpleName();
    private String mGroupId;
    private String mReportId;

    private Context ctx;

    private MererDetailEntity entity;

    public MeterDetailActMode(Context ctx) {
        this.ctx = ctx;
    }

    public void requestData(String groupId, String reportId) {
        this.mGroupId = groupId;
        this.mReportId = reportId;
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
                    String jsonFileName = String.format("group_%s_template_%s_report_%s.json", mGroupId, "1", mReportId);
                    String jsonFilePath = FileUtil.dirPath(ctx, K.K_CACHED_DIR_NAME, jsonFileName);
                    boolean dataState = ApiHelper.reportJsonData(ctx, mGroupId, "1", mReportId);
                    if (dataState || new File(jsonFilePath).exists()) {
                        response = FileUtil.readFile(jsonFilePath);
                    }
                    else {
                        MDetailActRequestResult result1 = new MDetailActRequestResult(true, 400, null);
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

                    entity = new MererDetailEntity();
                    entity.setData(new ArrayList<MererDetailEntity.PageData>());
                    Log.i(TAG, "analysisDataReaderTime2:" + TimeUtil.getNowTime());

                    while (reader.hasNext()) {
                        String key = reader.readString();
                        switch (key) {
                            case "name":
                                entity.setName(reader.readObject().toString());
                                Log.i(TAG, "name:" + TimeUtil.getNowTime());
                                break;

                            case "data":
                                Log.i(TAG, "dataStart:" + TimeUtil.getNowTime());
                                reader.startArray();

                                while (reader.hasNext()) {
                                    reader.startObject();
                                    MererDetailEntity.PageData data = new MererDetailEntity.PageData();
                                    while (reader.hasNext()) {
                                        String dataKey = reader.readString();
                                        switch (dataKey) {
                                            case "parts":
                                                data.setParts(reader.readObject().toString());
                                                break;

                                            case "title":
                                                data.setTitle(reader.readObject().toString());
                                                break;

                                            default:
                                                break;
                                        }
                                    }
                                    reader.endObject();
                                    entity.getData().add(data);
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
                    EventBus.getDefault().post(new MDetailActRequestResult(true, 200, entity));
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
            is = context.getResources().getAssets().open("kpi_detaldata.json");
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
