package com.intfocus.template.subject.one.entity;

import java.io.Serializable;

/**
 * Created by zbaoliang on 17-5-9.
 */
public class Series implements Serializable {
    private float value;
    private int color;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
