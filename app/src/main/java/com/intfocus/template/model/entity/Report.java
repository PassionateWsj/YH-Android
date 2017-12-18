package com.intfocus.template.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

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
    @Id(autoincrement = true)
    Long id;

    /**
     * 报表唯一标识, 由 reportId + templateI + groupId 组成
     */
    String uuid;

    /**
     * 报表筛选条件
     */
    String name;

    /**
     * 报表组件id
     */
    int index;

    /**
     * 报表根页签名
     */
    String page_title;

    /**
     * 组件类型
     * 整包数据对应的类型为: main_data
     */
    String type;

    /**
     * JSON 格式的配置信息
     */
    String config;

    @Generated(hash = 265585219)
    public Report(Long id, String uuid, String name, int index, String page_title,
                  String type, String config) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.index = index;
        this.page_title = page_title;
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPage_title() {
        return this.page_title;
    }

    public void setPage_title(String page_title) {
        this.page_title = page_title;
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
