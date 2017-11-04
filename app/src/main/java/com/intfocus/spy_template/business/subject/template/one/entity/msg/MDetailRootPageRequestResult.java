package com.intfocus.spy_template.business.subject.template.one.entity.msg;


import com.intfocus.spy_template.business.subject.template.one.entity.MDetailUnitEntity;

import java.util.ArrayList;

/**
 * 仪表数据详情页面请求结果
 * Created by zbaoliang on 17-4-28.
 */
public class MDetailRootPageRequestResult {
    public boolean isSuccress;
    public int stateCode;
    public ArrayList<MDetailUnitEntity> datas;

    public MDetailRootPageRequestResult(boolean isSuccress, int stateCode, ArrayList<MDetailUnitEntity> datas) {
        this.isSuccress = isSuccress;
        this.stateCode = stateCode;
        this.datas = datas;
    }
}
