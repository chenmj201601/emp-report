package com.netinfo.emp.report.server.entity;

import com.netinfo.emp.report.model.ReportDataField;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.entity
 * <p>
 * Created by Charley on 2017/5/9.
 */
public class QueryResult {
    private String name;
    private Object value;
    private ReportDataField field;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public ReportDataField getField() {
        return field;
    }

    public void setField(ReportDataField field) {
        this.field = field;
    }
}
