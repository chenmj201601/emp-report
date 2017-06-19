package com.netinfo.emp.report.server.entity;

import com.netinfo.emp.report.model.ReportDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据集结果，指定的数据集的查询结果，结果放在一个List中，相当于一个表格
 * <p>
 * Project emp-report
 * Package com.netinfo.emp.report.server.entity
 * <p>
 * Created by Charley on 2017/5/9.
 */
public class DataSetResult {
    //数据集名称
    private String name;
    //数据集
    private ReportDataSet DataSet;
    //结果列表
    private List<DataRecord> result = new ArrayList<>();

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

    public List<DataRecord> getResult() {
        return result;
    }

}
