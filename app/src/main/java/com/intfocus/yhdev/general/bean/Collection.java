package com.intfocus.yhdev.general.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @author liuruilin
 * @data 2017/11/7
 * @describe
 */
@Entity
public class Collection {
    /**
     * local_id
     */
    @Id(autoincrement = true)
    Long localId;

    /**
     * uuid
     */
    String uuid;

    /**
     * D-JSON 数据采集完成后拼装的JSON
     */
    String DJson;

    /**
     * status 采集信息上传状态
     */
    int status;

    /**
     * image_status 图片上传状态
     */
    int image_status;

    @Generated(hash = 2024385460)
    public Collection(Long localId, String uuid, String DJson, int status,
            int image_status) {
        this.localId = localId;
        this.uuid = uuid;
        this.DJson = DJson;
        this.status = status;
        this.image_status = image_status;
    }

    @Generated(hash = 1149123052)
    public Collection() {
    }

    public Long getLocalId() {
        return this.localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDJson() {
        return this.DJson;
    }

    public void setDJson(String DJson) {
        this.DJson = DJson;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getImage_status() {
        return this.image_status;
    }

    public void setImage_status(int image_status) {
        this.image_status = image_status;
    }
}
