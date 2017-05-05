package com.netinfo.emp.report.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.model
 * <p>
 * Created by Charley on 2017/5/4.
 */
public class ReportDataSet {
    private String name;
    private String dataSourceName;
    private List<ReportDataTable> tables = new ArrayList<>();
    private List<ReportDataField> fields = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public List<ReportDataTable> getTables() {
        return tables;
    }

    public List<ReportDataField> getFields() {
        return fields;
    }
}
