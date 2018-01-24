package com.intfocus.template.subject.one.entity;


import java.util.List;

/**
 * 仪表数据详情页面请求结果
 * Created by zbaoliang on 17-4-28.
 */
public class MDetailRootPageRequestResult {
    private List<MDetailUnitEntity> datas;

    public MDetailRootPageRequestResult(List<MDetailUnitEntity> datas) {
        this.datas = datas;
    }

    public List<MDetailUnitEntity> getDatas() {
        return datas;
    }

    public void setDatas(List<MDetailUnitEntity> datas) {
        this.datas = datas;
    }
}
