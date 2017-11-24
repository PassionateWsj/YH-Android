package com.intfocus.syp_template.business.subject.template.one.entity.msg;


import com.intfocus.syp_template.business.subject.template.one.entity.MDetailUnitEntity;

import java.util.List;

/**
 * 仪表数据详情页面请求结果
 * Created by zbaoliang on 17-4-28.
 */
public class MDetailRootPageRequestResult {
    public List<MDetailUnitEntity> datas;

    public MDetailRootPageRequestResult(List<MDetailUnitEntity> datas) {
        this.datas = datas;
    }
}
