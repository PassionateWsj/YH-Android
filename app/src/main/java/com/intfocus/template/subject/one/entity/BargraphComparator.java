package com.intfocus.template.subject.one.entity;

/**
 * 条状图-重构对象-以便排序用
 *
 * @author zbaoliang
 * @date 17-5-16
 */
public class BargraphComparator {
    private String name;
    private String data;
    private int color;

    public BargraphComparator(String name, String data, int color) {
        this.name = name;
        this.data = data;
        this.color = color;
    }

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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
