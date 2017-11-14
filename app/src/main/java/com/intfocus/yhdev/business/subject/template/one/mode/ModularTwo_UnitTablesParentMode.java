package com.intfocus.yhdev.business.subject.template.one.mode;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONReader;
import com.intfocus.yhdev.business.subject.template.one.ModeImpl;
import com.intfocus.yhdev.business.subject.template.one.entity.MDetailUnitEntity;
import com.intfocus.yhdev.business.subject.template.one.entity.msg.MDetailRootPageRequestResult;
import com.intfocus.yhdev.business.subject.templateone.entity.MererDetailEntity;
import com.intfocus.yhdev.general.bean.Report;
import com.intfocus.yhdev.general.gen.ReportDao;
import com.intfocus.yhdev.general.util.DaoUtil;
import com.zbl.lib.baseframe.core.AbstractMode;
import com.zbl.lib.baseframe.utils.TimeUtil;

import java.io.StringReader;
import java.util.ArrayList;

import static com.intfocus.yhdev.YHApplication.threadPool;
import static com.intfucos.yhdev.constant.Params.REPORT_TYPE_TABLE;

/**
 * 仪表盘-数据处理模块
 * Created by zbaoliang on 17-4-28.
 */
public class ModularTwo_UnitTablesParentMode extends AbstractMode {

    String TAG = ModularTwo_UnitTablesParentMode.class.getSimpleName();

    Context ctx;

    MererDetailEntity entity;

    public ModularTwo_UnitTablesParentMode(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void requestData() {
    }

    public ArrayList<MDetailUnitEntity> datas;

    /**
     * 解析数据
     *
     * @param result
     */
    public void analysisData(final String uuid, final int index) {

        ReportDao reportDao = DaoUtil.INSTANCE.getReportDao();
        Report report = reportDao.queryBuilder()
                .where(reportDao.queryBuilder()
                        .and(ReportDao.Properties.Uuid.eq(uuid)
                                , ReportDao.Properties.Type.eq(REPORT_TYPE_TABLE)
                                , ReportDao.Properties.Index.eq(index)))
                .unique();

        final String result = report.getConfig();

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
                                case "table":
                                    entity.config = reader.readObject().toString();
                                    break;

                                case "title":
                                    entity.type = reader.readObject().toString();
                                    break;
                            }
                        }
                        datas.add(entity);
                        reader.endObject();
                    }
                    reader.endArray();
                    MDetailRootPageRequestResult RequestResult = new MDetailRootPageRequestResult(true, 200, datas);
                    dataCallback(RequestResult, "onMessageEvent");
                    Log.i(TAG, "EndAnalysisTime:" + TimeUtil.getNowTime());
                } catch (Exception e) {
                    dataCallback(new MDetailRootPageRequestResult(true, 400, null), "onMessageEvent");
                    e.printStackTrace();
                }
            }
        });
    }
}
