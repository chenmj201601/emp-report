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
    private String linkUrl;
    private int extDirection;
    private String leftParent;
    private String topParent;
    private int formatType;
    private String formatString;
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

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public int getExtDirection() {
        return extDirection;
    }

    public void setExtDirection(int extDirection) {
        this.extDirection = extDirection;
    }

    public String getLeftParent() {
        return leftParent;
    }

    public void setLeftParent(String leftParent) {
        this.leftParent = leftParent;
    }

    public String getTopParent() {
        return topParent;
    }

    public void setTopParent(String topParent) {
        this.topParent = topParent;
    }

    public int getFormatType() {
        return formatType;
    }

    public void setFormatType(int formatType) {
        this.formatType = formatType;
    }

    public String getFormatString() {
        return formatString;
    }

    public void setFormatString(String formatString) {
        this.formatString = formatString;
    }

    public ReportElement getElement() {
        return element;
    }

    public void setElement(ReportElement element) {
        this.element = element;
    }
}
