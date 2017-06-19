package com.netinfo.emp.report.server.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.entity
 * <p>
 * Created by Charley on 2017/6/5.
 */
public class GroupResult {
    private String groupValue;
    private String fieldName;
    private List<DataRecord> results = new ArrayList<>();

    public String getGroupValue() {
        return groupValue;
    }

    public void setGroupValue(String groupValue) {
        this.groupValue = groupValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public List<DataRecord> getResults() {
        return results;
    }
}
