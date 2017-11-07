package com.intfocus.hx.business.subject.template.one.mode;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONReader;
import com.intfocus.hx.business.subject.template.one.entity.MDetailUnitEntity;
import com.intfocus.hx.business.subject.template.one.entity.msg.MDetailRootPageRequestResult;
import com.zbl.lib.baseframe.core.AbstractMode;
import com.zbl.lib.baseframe.utils.TimeUtil;

import java.io.StringReader;
import java.util.ArrayList;

import static com.intfocus.hx.general.YHApplication.threadPool;


/**
 * 仪表盘-数据处理模块
 * Created by zbaoliang on 17-4-28.
 */
public class MDetalRootPageMode extends AbstractMode {

    String TAG = MDetalRootPageMode.class.getSimpleName();

    Context ctx;

    public ArrayList<MDetailUnitEntity> datas;

    public MDetalRootPageMode(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void requestData() {
    }

    /**
     * 解析数据
     *
     * @param result
     */
    public void analysisData(final String result) {
        Log.i(TAG, "StartAnalysisTime:" + TimeUtil.getNowTime());
        datas = new ArrayList<>();
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    StringReader isr = new StringReader(result);
                    JSONReader reader = new JSONReader(isr);
                    reader.startArray();
                    while (reader.hasNext()) {
                        MDetailUnitEntity entity = new MDetailUnitEntity();
                        reader.startObject();
                        while (reader.hasNext()) {
                            String key = reader.readString();
                            switch (key) {
                                case "config":
                                    entity.config = reader.readObject().toString();
                                    break;

                                case "type":
                                    entity.type = reader.readObject().toString();
                                    break;
                            }
                        }
                        datas.add(entity);
                        reader.endObject();
                    }
                    reader.endArray();
                    MDetailRootPageRequestResult requestResult = new MDetailRootPageRequestResult(true, 200, datas);
                    dataCallback(requestResult, "onMessageEvent");
                    Log.i(TAG, "EndAnalysisTime:" + TimeUtil.getNowTime());
                } catch (Exception e) {
                    dataCallback(new MDetailRootPageRequestResult(true, 400, null), "onMessageEvent");
                    e.printStackTrace();
                }
            }
        });
    }
}