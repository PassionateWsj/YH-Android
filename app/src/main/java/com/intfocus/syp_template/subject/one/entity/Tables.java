package com.intfocus.syp_template.subject.one.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 仪表盘实体对象
 * Created by zbaoliang on 17-4-28.
 */
public class Tables implements Serializable {
    /**
     * 仪表盘标题
     */
    private String[] head;

    private ArrayList<TableRowEntity> data;

    public class TableRowEntity implements Serializable {
        private String[] main_data;
        private Tables sub_data;

        public String[] getMain_data() {
            return main_data;
        }

        public void setMain_data(String[] main_data) {
            this.main_data = main_data;
        }

        public Tables getSub_data() {
            return sub_data;
        }

        public void setSub_data(Tables sub_data) {
            this.sub_data = sub_data;
        }
    }

    public String[] getHead() {
        return head;
    }

    public void setHead(String[] head) {
        this.head = head;
    }

    public ArrayList<TableRowEntity> getData() {
        return data;
    }

    public void setData(ArrayList<TableRowEntity> data) {
        this.data = data;
    }
}
