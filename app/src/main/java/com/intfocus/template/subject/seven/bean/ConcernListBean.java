package com.intfocus.template.subject.seven.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * ****************************************************
 * author jameswong
 * created on: 18/03/12 下午2:33
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
@Entity
public class ConcernListBean {
    @Id
    Long _id;
    String uuid;
    String obj_id;
    String obj_name;
    String obj_num;
    String rep_code;
    int type = 0;

    @Generated(hash = 1543210728)
    public ConcernListBean(Long _id, String uuid, String obj_id, String obj_name,
            String obj_num, String rep_code, int type) {
        this._id = _id;
        this.uuid = uuid;
        this.obj_id = obj_id;
        this.obj_name = obj_name;
        this.obj_num = obj_num;
        this.rep_code = rep_code;
        this.type = type;
    }

    @Generated(hash = 1110924217)
    public ConcernListBean() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getObj_id() {
        return this.obj_id;
    }

    public void setObj_id(String obj_id) {
        this.obj_id = obj_id;
    }

    public String getObj_name() {
        return this.obj_name;
    }

    public void setObj_name(String obj_name) {
        this.obj_name = obj_name;
    }

    public String getObj_num() {
        return this.obj_num;
    }

    public void setObj_num(String obj_num) {
        this.obj_num = obj_num;
    }

    public String getRep_code() {
        return this.rep_code;
    }

    public void setRep_code(String rep_code) {
        this.rep_code = rep_code;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
