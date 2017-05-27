package com.netinfo.emp.report.server.entity;

import com.netinfo.emp.report.model.ReportCell;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.entity
 * <p>
 * Created by Charley on 2017/5/11.
 */
public class RowState {
    private int rowIndex;
    private int rowHeight;
    private int colCount;
    private int maxPageHeight;
    private int pageHeight;
    private int pageIndex;
    private int dataIndex;
    private Map<String, ReportCell> cellMap;
    private List<String> skipCells;
    private Element tableElement;
    private List<Map<String, QueryResult>> queryResultsCollection = new ArrayList<>();

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
    }

    public int getColCount() {
        return colCount;
    }

    public void setColCount(int colCount) {
        this.colCount = colCount;
    }

    public int getMaxPageHeight() {
        return maxPageHeight;
    }

    public void setMaxPageHeight(int maxPageHeight) {
        this.maxPageHeight = maxPageHeight;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public void setPageHeight(int pageHeight) {
        this.pageHeight = pageHeight;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(int dataIndex) {
        this.dataIndex = dataIndex;
    }

    public Map<String, ReportCell> getCellMap() {
        return cellMap;
    }

    public void setCellMap(Map<String, ReportCell> cellMap) {
        this.cellMap = cellMap;
    }

    public List<String> getSkipCells() {
        return skipCells;
    }

    public void setSkipCells(List<String> skipCells) {
        this.skipCells = skipCells;
    }

    public Element getTableElement() {
        return tableElement;
    }

    public void setTableElement(Element tableElement) {
        this.tableElement = tableElement;
    }

    public List<Map<String, QueryResult>> getQueryResultsCollection() {
        return queryResultsCollection;
    }

    public void setQueryResultsCollection(List<Map<String, QueryResult>> queryResultsCollection) {
        this.queryResultsCollection = queryResultsCollection;
    }
}
