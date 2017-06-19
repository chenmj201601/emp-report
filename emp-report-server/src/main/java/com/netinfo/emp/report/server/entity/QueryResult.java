package com.netinfo.emp.report.server.entity;

import com.netinfo.emp.report.model.ReportDataField;

/**
 * 查询结果，以键值对的形式表示一个结果
 * <p>
 * Project emp-report
 * Package com.netinfo.emp.report.server.entity
 * <p>
 * Created by Charley on 2017/5/9.
 */
public class QueryResult {
    //键名，也就是字段名
    private String key;
    //字段值
    private Object value;
    //关联的字段的信息
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
