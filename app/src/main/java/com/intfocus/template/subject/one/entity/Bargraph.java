package com.intfocus.template.subject.one.entity;

import java.util.ArrayList;

/**
 * 条状图数据实体对象
 * Created by zbaoliang on 17-5-15.
 */
public class Bargraph {
    private Series series;
    private XAxis xAxis;

    public class Series {
        private ArrayList<Data> data;

        private String name;
        private String percentage;


        public class Data {
            private String value;
            private int color;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public int getColor() {
                return color;
            }

            public void setColor(int color) {
                this.color = color;
            }
        }

        public ArrayList<Data> getData() {
            return data;
        }

        public void setData(ArrayList<Data> data) {
            this.data = data;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPercentage() {
            return percentage;
        }

        public void setPercentage(String percentage) {
            this.percentage = percentage;
        }
    }


    public class XAxis {
        private String[] data;

        private String name;

        public String[] getData() {
            return data;
        }

        public void setData(String[] data) {
            this.data = data;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public XAxis getxAxis() {
        return xAxis;
    }

    public void setxAxis(XAxis xAxis) {
        this.xAxis = xAxis;
    }
}
