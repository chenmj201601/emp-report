package com.netinfo.emp.report.server.entity;

import com.netinfo.emp.report.model.ReportDataField;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.entity
 * <p>
 * Created by Charley on 2017/5/9.
 */
public class QueryResult {
    private String key;
    private Object value;
    private ReportDataField field;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
