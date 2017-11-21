package com.intfocus.syp_template.business.subject.template.one.entity.msg;


import com.intfocus.syp_template.business.subject.template.one.entity.MDetailUnitEntity;

import java.util.ArrayList;

/**
 * 仪表数据详情页面请求结果
 * Created by zbaoliang on 17-4-28.
 */
public class MDetailRootPageRequestResult {
    public ArrayList<MDetailUnitEntity> datas;

    public MDetailRootPageRequestResult(ArrayList<MDetailUnitEntity> datas) {
        this.datas = datas;
    }
}
