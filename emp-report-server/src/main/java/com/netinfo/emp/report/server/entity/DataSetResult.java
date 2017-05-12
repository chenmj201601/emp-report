package com.netinfo.emp.report.server.entity;

import com.netinfo.emp.report.model.ReportDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.entity
 * <p>
 * Created by Charley on 2017/5/9.
 */
public class DataSetResult {
    private String name;
    private ReportDataSet DataSet;
    private List<Map<String, QueryResult>> result = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ReportDataSet getDataSet() {
        return DataSet;
    }

    public void setDataSet(ReportDataSet dataSet) {
        DataSet = dataSet;
    }

    public List<Map<String, QueryResult>> getResult() {
        return result;
    }

}
