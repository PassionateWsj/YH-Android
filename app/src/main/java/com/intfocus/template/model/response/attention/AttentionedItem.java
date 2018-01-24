package com.intfocus.template.model.response.attention;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 下午2:58
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
@Entity
public class AttentionedItem {
    @Id
    Long id;
    String main_data_id;
    String main_data_name;
    String attention_item_id;
    String attention_item_name;
    String main_attention_data;
    String attention_item_data;
    String updated_at;

    @Generated(hash = 1650701806)
    public AttentionedItem(Long id, String main_data_id, String main_data_name,
            String attention_item_id, String attention_item_name,
            String main_attention_data, String attention_item_data,
            String updated_at) {
        this.id = id;
        this.main_data_id = main_data_id;
        this.main_data_name = main_data_name;
        this.attention_item_id = attention_item_id;
        this.attention_item_name = attention_item_name;
        this.main_attention_data = main_attention_data;
        this.attention_item_data = attention_item_data;
        this.updated_at = updated_at;
    }

    @Generated(hash = 998034863)
    public AttentionedItem() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAttention_item_id() {
        return this.attention_item_id;
    }

    public void setAttention_item_id(String attention_item_id) {
        this.attention_item_id = attention_item_id;
    }

    public String getAttention_item_name() {
        return this.attention_item_name;
    }

    public void setAttention_item_name(String attention_item_name) {
        this.attention_item_name = attention_item_name;
    }

    public String getMain_attention_data() {
        return this.main_attention_data;
    }

    public void setMain_attention_data(String main_attention_data) {
        this.main_attention_data = main_attention_data;
    }

    public String getAttention_item_data() {
        return this.attention_item_data;
    }

    public void setAttention_item_data(String attention_item_data) {
        this.attention_item_data = attention_item_data;
    }

    public String getUpdated_at() {
        return this.updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
