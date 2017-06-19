package com.netinfo.emp.report.model;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.model
 * <p>
 * Created by Charley on 2017/6/1.
 */
public class ReportOrder {
    private String field;
    private int direction;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
