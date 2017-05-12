package com.netinfo.emp.report.server.entity;

import com.netinfo.emp.report.model.ReportCell;
import org.jdom2.Element;

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
    private Map<String, ReportCell> cellMap;
    private List<String> skipCells;
    private Element tableElement;
    private Map<String, QueryResult> queryResults;

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

    public Map<String, QueryResult> getQueryResults() {
        return queryResults;
    }

    public void setQueryResults(Map<String, QueryResult> queryResults) {
        this.queryResults = queryResults;
    }
}
