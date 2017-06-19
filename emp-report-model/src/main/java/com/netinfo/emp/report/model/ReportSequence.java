package com.netinfo.emp.report.model;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.model
 * <p>
 * Created by Charley on 2017/5/4.
 */
public class ReportSequence extends ReportElement {
    private String dataSetName;
    private String dataTableName;
    private String dataFieldName;
    private String expression;
    private int optMethod;
    private int groupMode;
    private int collectMode;

    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public String getDataTableName() {
        return dataTableName;
    }

    public void setDataTableName(String dataTableName) {
        this.dataTableName = dataTableName;
    }

    public String getDataFieldName() {
        return dataFieldName;
    }

    public void setDataFieldName(String dataFieldName) {
        this.dataFieldName = dataFieldName;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public int getOptMethod() {
        return optMethod;
    }

    public void setOptMethod(int optMethod) {
        this.optMethod = optMethod;
    }

    public int getGroupMode() {
        return groupMode;
    }

    public void setGroupMode(int groupMode) {
        this.groupMode = groupMode;
    }

    public int getCollectMode() {
        return collectMode;
    }

    public void setCollectMode(int collectMode) {
        this.collectMode = collectMode;
    }
}
