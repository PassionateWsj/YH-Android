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
     * 报表组件id
     */
    int index;

    /**
     * 报表根页签id
     */
    int page;

    /**
     * 报表根页签名
     */
    String title;

    /**
     * 组件类型
     * 整包数据对应的类型为: main_data
     */
    String type;

    /**
     * JSON 格式的配置信息
     */
    String config;

    @Generated(hash = 824925627)
    public Report(Long id, String uuid, int index, int page, String title,
            String type, String config) {
        this.id = id;
        this.uuid = uuid;
        this.index = index;
        this.page = page;
        this.title = title;
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

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
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
