package com.netinfo.emp.report.server.entity;

import com.netinfo.emp.report.model.ReportCell;
import org.jdom2.Element;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.entity
 * <p>
 * Created by Charley on 2017/6/5.
 */
public class GridCell {
    private String key;     //格式：String.Format("%010d%010d",rowIndex,colIndex)
    private int rowIndex;
    private int colIndex;
    private int rowSpan;
    private int colSpan;
    private ReportCell reportCell;
    private Element content;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public int getColSpan() {
        return colSpan;
    }

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    public ReportCell getReportCell() {
        return reportCell;
    }

    public void setReportCell(ReportCell reportCell) {
        this.reportCell = reportCell;
    }

    public Element getContent() {
        return content;
    }

    public void setContent(Element content) {
        this.content = content;
    }

}
