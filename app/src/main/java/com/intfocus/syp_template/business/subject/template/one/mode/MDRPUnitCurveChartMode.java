package com.intfocus.syp_template.business.subject.template.one.mode;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.intfocus.syp_template.business.subject.template.one.entity.MDRPUnitCurveChartEntity;
import com.intfocus.syp_template.general.util.LogUtil;
import com.zbl.lib.baseframe.core.AbstractMode;
import com.zbl.lib.baseframe.utils.TimeUtil;

import static com.intfocus.syp_template.YHApplication.threadPool;


/**
 * 仪表盘-数据处理模块
 * Created by zbaoliang on 17-4-28.
 */
public class MDRPUnitCurveChartMode extends AbstractMode {

    String TAG = MDRPUnitCurveChartMode.class.getSimpleName();

    Context ctx;
    String targetID;

    public MDRPUnitCurveChartMode(Context ctx, String targetID) {
        this.ctx = ctx;
        this.targetID = targetID;
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
        LogUtil.d(TAG, "StartAnalysisTime:" + TimeUtil.getNowTime());
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MDRPUnitCurveChartEntity entity = JSON.parseObject(result, MDRPUnitCurveChartEntity.class);
                    dataCallback(entity, "onMessageEvent");
                    LogUtil.d(TAG, "EndAnalysisTime:" + TimeUtil.getNowTime());
                } catch (Exception e) {
                    e.printStackTrace();
                    MDRPUnitCurveChartEntity entity = new MDRPUnitCurveChartEntity();
                    entity.stateCode = 400;
                    dataCallback(entity, "onMessageEvent");
                }
            }
        });
    }
}
