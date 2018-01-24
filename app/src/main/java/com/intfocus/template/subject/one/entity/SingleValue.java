package com.intfocus.template.subject.one.entity;

import java.io.Serializable;

/**
 * Created by zbaoliang on 17-5-14.
 */

public class SingleValue implements Serializable {

    private boolean real_time = false;
    private String real_time_api;
    private State state = new State();
    private MainData main_data = new MainData();
    private MainData sub_data = new MainData();

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public MainData getMain_data() {
        return main_data;
    }

    public void setMain_data(MainData main_data) {
        this.main_data = main_data;
    }

    public MainData getSub_data() {
        return sub_data;
    }

    public void setSub_data(MainData sub_data) {
        this.sub_data = sub_data;
    }

    public boolean isReal_time() {
        return real_time;
    }

    public void setReal_time(boolean real_time) {
        this.real_time = real_time;
    }

    public String getReal_time_api() {
        return real_time_api;
    }

    public void setReal_time_api(String real_time_api) {
        this.real_time_api = real_time_api;
    }

    public class State implements Serializable {
        private int color = 0;

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }

    public class MainData implements Serializable {
        private String name = "";
        private String data = "0.0";
        private String format = "";
        private String percentage = "";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public String getPercentage() {
            return percentage;
        }

        public void setPercentage(String percentage) {
            this.percentage = percentage;
        }
    }

}
