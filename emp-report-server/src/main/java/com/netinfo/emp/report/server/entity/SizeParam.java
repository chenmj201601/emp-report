package com.netinfo.emp.report.server.entity;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.entity
 * <p>
 * Created by Charley on 2017/6/9.
 */
public class SizeParam {
    private int id;
    private String name;
    private int value;
    private int extCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getExtCount() {
        return extCount;
    }

    public void setExtCount(int extCount) {
        this.extCount = extCount;
    }
}
