package com.netinfo.emp.report.server.entity;

import com.netinfo.emp.report.model.ReportDocument;
import com.netinfo.emp.report.model.VisualStyle;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.entity
 * <p>
 * Created by Charley on 2017/5/11.
 */
public class ReportGenerator {
    private String key;
    private String reportName;
    private File reportFile;
    private ReportDocument reportDocument;
    private Map<String, VisualStyle> reportStyles = new HashMap<>();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public File getReportFile() {
        return reportFile;
    }

    public void setReportFile(File reportFile) {
        this.reportFile = reportFile;
    }

    public ReportDocument getReportDocument() {
        return reportDocument;
    }

    public void setReportDocument(ReportDocument reportDocument) {
        this.reportDocument = reportDocument;
    }

    public Map<String, VisualStyle> getReportStyles() {
        return this.reportStyles;
    }
}
