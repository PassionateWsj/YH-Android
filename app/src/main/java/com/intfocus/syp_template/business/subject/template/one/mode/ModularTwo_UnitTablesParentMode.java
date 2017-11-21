package com.intfocus.syp_template.business.subject.template.one.mode;

import android.content.Context;

import com.intfocus.syp_template.business.subject.template.one.entity.MDetailUnitEntity;
import com.intfocus.syp_template.business.subject.templateone.entity.MererDetailEntity;
import com.zbl.lib.baseframe.core.AbstractMode;

import java.util.ArrayList;

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
     * result
     */
    public void analysisData(final String uuid, final int index) {


    }
}
