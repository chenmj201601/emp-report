package com.netinfo.emp.report.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.model
 * <p>
 * Created by Charley on 2017/5/4.
 */
public class ReportDocument {
    private String name;
    private String path;
    private ReportGrid grid;
    private List<ReportDataSet> dataSets = new ArrayList<>();
    private List<ReportCell> cells = new ArrayList<>();
    private List<VisualStyle> styles = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ReportGrid getGrid() {
        return grid;
    }

    public void setGrid(ReportGrid grid) {
        this.grid = grid;
    }

    public List<ReportDataSet> getDataSets() {
        return dataSets;
    }

    public List<ReportCell> getCells() {
        return cells;
    }

    public List<VisualStyle> getStyles() {
        return styles;
    }
}
