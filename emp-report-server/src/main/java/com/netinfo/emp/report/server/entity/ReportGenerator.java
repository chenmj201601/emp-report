package com.netinfo.emp.report.server.entity;

import com.netinfo.emp.report.model.ReportDocument;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 表示一个报表生成器，其中包含生成报表的相关信息
 * 此生成器在一个报表生命周期内一直有效，并由 Key 做唯一标识
 * <p>
 * Project emp-report
 * Package com.netinfo.emp.report.server.entity
 * <p>
 * Created by Charley on 2017/5/11.
 */
public class ReportGenerator {
    //由 SessionID，ReportName 和时间戳构成，经哈希计算得到
    private String key;
    private String sessionId;
    private String reportName;
    private String reportTitle;
    private File reportFile;
    private File dataSourceFile;
    /**
     * 是否已经加载数据
     * 在第一次访问此报表的时候，不会立即连接数据库查询数据，而是在生成报表主体内容的时候才连接数据库查询数据
     * 数据查询结果将会缓存在 ReportGenerator 中，在翻页的过程不会再次查询数据
     */
    private boolean dataLoaded;
    private int pageIndex;
    private int pageCount;
    private ReportDocument reportDocument;
    //缓存查询结果
    private Map<String, DataSetResult> dataSetResults = new HashMap<>();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public File getReportFile() {
        return reportFile;
    }

    public void setReportFile(File reportFile) {
        this.reportFile = reportFile;
    }

    public File getDataSourceFile() {
        return dataSourceFile;
    }

    public void setDataSourceFile(File dataSourceFile) {
        this.dataSourceFile = dataSourceFile;
    }

    public ReportDocument getReportDocument() {
        return reportDocument;
    }

    public void setReportDocument(ReportDocument reportDocument) {
        this.reportDocument = reportDocument;
    }

    public boolean isDataLoaded() {
        return dataLoaded;
    }

    public void setDataLoaded(boolean dataLoaded) {
        this.dataLoaded = dataLoaded;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public Map<String, DataSetResult> getDataSetResults() {
        return dataSetResults;
    }

}
