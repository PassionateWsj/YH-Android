package com.intfocus.yhdev.subject.template_v1.entity.msg;


import com.intfocus.yhdev.subject.template_v1.entity.MDetailUnitEntity;

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
