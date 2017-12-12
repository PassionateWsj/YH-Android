package com.intfocus.shengyiplus.subject.one.entity;

/**
 * 仪表盘详情页面每个单元模块数据
 * Created by zbaoliang on 17-5-7.
 */
public class MDetailUnitEntity {
    /**
     * 图表类型：
     * banner 标题栏;
     * chart 曲线图表;
     * info 一般标签(附标题);
     * single_value 单值组件;
     * line-or-bar 柱状图(竖);
     * bargraph 条状图(横);
     * tables#v3 类Excel冻结横竖首列表格;
     */
    private String title;
    private Tables table;
    //是否选中
    private boolean isCheck = false;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Tables getTable() {
        return table;
    }

    public void setTable(Tables table) {
        this.table = table;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
