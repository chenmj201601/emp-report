package com.netinfo.emp.report.model;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.model
 * <p>
 * Created by Charley on 2017/5/31.
 */
public class ReportCondition {
    private String field;
    private int judge;
    private String value;
    private int relation;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getJudge() {
        return judge;
    }

    public void setJudge(int juge) {
        this.judge = juge;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }
}
