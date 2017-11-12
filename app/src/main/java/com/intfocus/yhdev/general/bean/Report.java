package com.intfocus.yhdev.general.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author liuruilin
 * @data 2017/11/10
 * @describe
 */
@Entity
public class Report {
    /**
     * id
     */
    @Id (autoincrement = true)
    Long id;

    /**
     * 报表唯一标识, 由 reportId + templateI + groupId 组成
     */
    String uuid;

    /**
     * 报表组件的排序
     */
    String index;

    /**
     * 组件类型
     * 整包数据对应的类型为: main_data
     */
    String type;

    /**
     * JSON 格式的配置信息
     */
    String config;

    @Generated(hash = 2002770393)
    public Report(Long id, String uuid, String index, String type, String config) {
        this.id = id;
        this.uuid = uuid;
        this.index = index;
        this.type = type;
        this.config = config;
    }

    @Generated(hash = 1739299007)
    public Report() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIndex() {
        return this.index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConfig() {
        return this.config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
