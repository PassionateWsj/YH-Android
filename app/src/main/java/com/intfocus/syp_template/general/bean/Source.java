package com.intfocus.syp_template.general.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @author liuruilin
 * @data 2017/10/27
 * @describe 信息采集模板 -> 组件采集信息存储表
 */
@Entity
public class Source {

    /**
     * id
     */
    @Id(autoincrement = true)
    Long id;

    /**
     * report_id 数据采集模板id
     */
    String reportId;

    /**
     * 每份信息都有一个唯一的标识 uuid
     */
    String uuid;

    /**
     * 单个组件对应数据库字段的 key 值
     */
    String key;

    /**
     * 组件用来解析渲染的配置信息
     */
    String config;

    /**
     * 组件的类型
     */
    String type;

    /**
     * 组件是否默认显示
     */
    int isShow;

    /**
     * 组件是否为列表展示字段
     */
    int isList;

    /**
     * 组件是否为过滤字段
     */
    int isFilter;

    /**
     * 组件的值
     */
    String value;

    @Generated(hash = 2142487226)
    public Source(Long id, String reportId, String uuid, String key, String config,
            String type, int isShow, int isList, int isFilter, String value) {
        this.id = id;
        this.reportId = reportId;
        this.uuid = uuid;
        this.key = key;
        this.config = config;
        this.type = type;
        this.isShow = isShow;
        this.isList = isList;
        this.isFilter = isFilter;
        this.value = value;
    }

    @Generated(hash = 615387317)
    public Source() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportId() {
        return this.reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getConfig() {
        return this.config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIsShow() {
        return this.isShow;
    }

    public void setIsShow(int isShow) {
        this.isShow = isShow;
    }

    public int getIsList() {
        return this.isList;
    }

    public void setIsList(int isList) {
        this.isList = isList;
    }

    public int getIsFilter() {
        return this.isFilter;
    }

    public void setIsFilter(int isFilter) {
        this.isFilter = isFilter;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
