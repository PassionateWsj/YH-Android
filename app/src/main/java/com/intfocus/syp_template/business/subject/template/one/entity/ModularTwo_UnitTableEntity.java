package com.intfocus.syp_template.business.subject.template.one.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 仪表盘实体对象
 * Created by zbaoliang on 17-4-28.
 */
public class ModularTwo_UnitTableEntity implements Serializable {
    /**
     * 仪表盘标题
     */
    private String[] head;

    private ArrayList<TableRowEntity> data;

    public class TableRowEntity implements Serializable {
        private String[] main_data;
        private ModularTwo_UnitTableEntity sub_data;

        public String[] getMain_data() {
            return main_data;
        }

        public void setMain_data(String[] main_data) {
            this.main_data = main_data;
        }

        public ModularTwo_UnitTableEntity getSub_data() {
            return sub_data;
        }

        public void setSub_data(ModularTwo_UnitTableEntity sub_data) {
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
