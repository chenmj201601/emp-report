package com.netinfo.emp.report.server.entity;

import com.netinfo.emp.report.model.ReportCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.entity
 * <p>
 * Created by Charley on 2017/6/15.
 */
public class DesignCell {
    private String key;
    private ReportCell reportCell;
    private List<RelationCell> horizontalChildren = new ArrayList<>();
    private List<RelationCell> verticalChildren = new ArrayList<>();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ReportCell getReportCell() {
        return reportCell;
    }

    public void setReportCell(ReportCell reportCell) {
        this.reportCell = reportCell;
    }

    public List<RelationCell> getHorizontalChildren() {
        return horizontalChildren;
    }

    public List<RelationCell> getVerticalChildren() {
        return verticalChildren;
    }
}
