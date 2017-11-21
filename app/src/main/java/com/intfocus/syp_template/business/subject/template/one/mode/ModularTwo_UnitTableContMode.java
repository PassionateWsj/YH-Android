package com.intfocus.syp_template.business.subject.template.one.mode;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.intfocus.syp_template.business.subject.template.one.entity.ModularTwo_UnitTableEntity;
import com.intfocus.syp_template.business.subject.template.one.entity.msg.MDetailRootPageRequestResult;
import com.intfocus.syp_template.general.data.TempSubData;
import com.zbl.lib.baseframe.core.AbstractMode;
import com.zbl.lib.baseframe.utils.TimeUtil;

import static com.intfocus.syp_template.YHApplication.threadPool;

/**
 * 仪表盘-数据处理模块
 * Created by zbaoliang on 17-4-28.
 */
public class ModularTwo_UnitTableContMode extends AbstractMode {

    String TAG = ModularTwo_UnitTableContMode.class.getSimpleName();

//    Context ctx;
//
//    public ModularTwo_UnitTableContMode(Context ctx) {
//        this.ctx = ctx;
//    }

    @Override
    public void requestData() {
    }

    /**
     * 解析数据
     *
     * @param
     */
    public void analysisData() {
        Log.i(TAG, "StartAnalysisTime:" + TimeUtil.getNowTime());
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (TempSubData.hasData()) {
                        ModularTwo_UnitTableEntity entity = JSON.parseObject(TempSubData.getData(), ModularTwo_UnitTableEntity.class);
                        dataCallback(entity, "onMessageEvent");
                    }
                    Log.i(TAG, "EndAnalysisTime:" + TimeUtil.getNowTime());
                } catch (Exception e) {
                    dataCallback(new MDetailRootPageRequestResult(true, 400, null), "onMessageEvent");
                    e.printStackTrace();
                }
            }
        });
    }
}
