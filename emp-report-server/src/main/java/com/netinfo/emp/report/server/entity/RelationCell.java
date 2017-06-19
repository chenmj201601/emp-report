package com.netinfo.emp.report.server.entity;

import com.netinfo.emp.report.model.ReportCell;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 单元格依赖关系
 * <p>
 * Project emp-report
 * Package com.netinfo.emp.report.server.entity
 * <p>
 * Created by Charley on 2017/6/9.
 */
public class RelationCell {

    private int rowIndex;       //坐标信息
    private int rowSpan;
    private int colIndex;
    private int colSpan;

    private int extRowCount;        //扩展数量
    private int extColCount;

    private String dataSetName;     //DataSetName

    private ReportCell reportCell;
    private Element content;
    private RelationCell leftParent;
    private RelationCell topParent;

    private List<DataRecord> datas = new ArrayList<>();       //分组后的数据列表
    private List<RelationCell> rightChildren = new ArrayList<>();    //子格列表
    private List<RelationCell> bottomChildren = new ArrayList<>();


    //<editor-fold desc="全局属性,每个对象都携带，内容相同，使用时无需判断 Null">

    private ReportGenerator generator;
    private Map<String, ReportCell> reportCellMap;
    private Map<String, DataSetResult> dataSetResultMap;
    private Map<String, DesignCell> designCellMap;
    private Map<Integer, SizeParam> widthParamMap;
    private Map<Integer, SizeParam> heightParamMap;

    //</editor-fold>


    //<editor-fold desc="属性读写">

    public int getExtRowCount() {
        return extRowCount;
    }

    public void setExtRowCount(int extRowCount) {
        this.extRowCount = extRowCount;
    }

    public int getExtColCount() {
        return extColCount;
    }

    public void setExtColCount(int extColCount) {
        this.extColCount = extColCount;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public int getColIndex() {
        return colIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    public int getColSpan() {
        return colSpan;
    }

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
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

    public RelationCell getLeftParent() {
        return leftParent;
    }

    public void setLeftParent(RelationCell leftParent) {
        this.leftParent = leftParent;
    }

    public RelationCell getTopParent() {
        return topParent;
    }

    public void setTopParent(RelationCell topParent) {
        this.topParent = topParent;
    }

    public List<DataRecord> getDatas() {
        return datas;
    }

    public List<RelationCell> getRightChildren() {
        return rightChildren;
    }

    public List<RelationCell> getBottomChildren() {
        return bottomChildren;
    }

    public ReportGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(ReportGenerator generator) {
        this.generator = generator;
    }

    public Map<String, ReportCell> getReportCellMap() {
        return reportCellMap;
    }

    public void setReportCellMap(Map<String, ReportCell> reportCellMap) {
        this.reportCellMap = reportCellMap;
    }

    public Map<String, DataSetResult> getDataSetResultMap() {
        return dataSetResultMap;
    }

    public void setDataSetResultMap(Map<String, DataSetResult> dataSetResultMap) {
        this.dataSetResultMap = dataSetResultMap;
    }

    public Map<String, DesignCell> getDesignCellMap() {
        return designCellMap;
    }

    public void setDesignCellMap(Map<String, DesignCell> designCellMap) {
        this.designCellMap = designCellMap;
    }

    public Map<Integer, SizeParam> getWidthParamMap() {
        return widthParamMap;
    }

    public void setWidthParamMap(Map<Integer, SizeParam> widthParamMap) {
        this.widthParamMap = widthParamMap;
    }

    public Map<Integer, SizeParam> getHeightParamMap() {
        return heightParamMap;
    }

    public void setHeightParamMap(Map<Integer, SizeParam> heightParamMap) {
        this.heightParamMap = heightParamMap;
    }

    //</editor-fold>

}
