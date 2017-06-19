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
    private String sql;
    private List<ReportDataTable> tables = new ArrayList<>();
    private List<ReportDataField> fields = new ArrayList<>();
    private List<ReportCondition> conditions = new ArrayList<>();
    private List<ReportOrder> orders = new ArrayList<>();

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

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<ReportDataTable> getTables() {
        return tables;
    }

    public List<ReportDataField> getFields() {
        return fields;
    }

    public List<ReportCondition> getConditions() {
        return conditions;
    }

    public List<ReportOrder> getOrders() {
        return orders;
    }
}
