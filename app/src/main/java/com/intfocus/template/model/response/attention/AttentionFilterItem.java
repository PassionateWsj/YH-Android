package com.intfocus.template.model.response.attention;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/08/22 上午09:48
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
@Entity
public class AttentionFilterItem {
    @Id
    Long _id;
    String main_data_id;
    String main_data_name;
    @Generated(hash = 997369038)
    public AttentionFilterItem(Long _id, String main_data_id,
            String main_data_name) {
        this._id = _id;
        this.main_data_id = main_data_id;
        this.main_data_name = main_data_name;
    }
    @Generated(hash = 706748684)
    public AttentionFilterItem() {
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public String getMain_data_id() {
        return this.main_data_id;
    }
    public void setMain_data_id(String main_data_id) {
        this.main_data_id = main_data_id;
    }
    public String getMain_data_name() {
        return this.main_data_name;
    }
    public void setMain_data_name(String main_data_name) {
        this.main_data_name = main_data_name;
    }
}
