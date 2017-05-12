package com.netinfo.emp.report.model;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.model
 * <p>
 * Created by Charley on 2017/5/4.
 */
public class ReportCell {
    private int rowIndex;
    private int colIndex;
    private int rowSpan;
    private int colSpan;
    private int style;
    private ReportElement element;

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

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public ReportElement getElement() {
        return element;
    }

    public void setElement(ReportElement element) {
        this.element = element;
    }
}
