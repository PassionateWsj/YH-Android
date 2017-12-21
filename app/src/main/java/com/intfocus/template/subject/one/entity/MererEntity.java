package com.intfocus.template.subject.one.entity;

import java.io.Serializable;

/**
 * 仪表盘实体对象
 * Created by zbaoliang on 17-4-28.
 */
public class MererEntity implements Serializable {

    /**
     * 是否置顶显示：1为论波区，0为平铺区
     */
    private boolean is_stick;
    /**
     * 仪表盘标题
     */
    private String title;
    /**
     * 仪表盘所属组（大标题名称）
     */
    private String group_name;
    /**
     * 仪表盘类型：line 折线图; bar 柱状图; ring 环形图; number 纯文本
     */
    private String dashboard_type;
    /**
     * 外链地址
     */
    private String target_url;
    /**
     * 单位（如：万元）
     */
    private String unit;
    /**
     * 具体仪表数据
     */
    private LineEntity data;


    public class LineEntity implements Serializable {

        private HighLight high_light;

        private int[] chart_data;

        public class HighLight implements Serializable {
            private boolean percentage;//是否显示百分比0、1
            private double number;//高亮数字
            private double compare;//百分比
            private int arrow;//决定箭头方向和颜色

            public boolean isPercentage() {
                return percentage;
            }

            public void setPercentage(boolean percentage) {
                this.percentage = percentage;
            }

            public double getNumber() {
                return number;
            }

            public void setNumber(double number) {
                this.number = number;
            }

            public double getCompare() {
                return compare;
            }

            public void setCompare(double compare) {
                this.compare = compare;
            }

            public int getArrow() {
                return arrow;
            }

            public void setArrow(int arrow) {
                this.arrow = arrow;
            }
        }

        public HighLight getHigh_light() {
            return high_light;
        }

        public void setHigh_light(HighLight high_light) {
            this.high_light = high_light;
        }

        public int[] getChart_data() {
            return chart_data;
        }

        public void setChart_data(int[] chart_data) {
            this.chart_data = chart_data;
        }
    }

    public boolean isIs_stick() {
        return is_stick;
    }

    public void setIs_stick(boolean is_stick) {
        this.is_stick = is_stick;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getDashboard_type() {
        return dashboard_type;
    }

    public void setDashboard_type(String dashboard_type) {
        this.dashboard_type = dashboard_type;
    }

    public String getTarget_url() {
        return target_url;
    }

    public void setTarget_url(String target_url) {
        this.target_url = target_url;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LineEntity getData() {
        return data;
    }

    public void setData(LineEntity data) {
        this.data = data;
    }
}
