package com.netinfo.emp.report.server.util;

import com.netinfo.emp.report.model.*;
import com.netinfo.emp.report.server.entity.DataSetResult;
import com.netinfo.emp.report.server.entity.QueryResult;
import com.netinfo.emp.report.server.entity.ReportGenerator;
import com.netinfo.emp.report.server.entity.RowState;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.util
 * <p>
 * Created by Charley on 2017/5/5.
 */
public class HtmlUtil {
    public static String generateHtml(ReportGenerator generator) {
        String strContent = "";
        if (generator == null) {
            System.out.println(String.format("ReportGenerator is null."));
            return strContent;
        }
        String reportName = generator.getReportName();
        ReportDocument reportDocument = generator.getReportDocument();
        if (reportDocument == null) {
            System.out.println(String.format("ReportDocument not exist. %s", reportName));
            return strContent;
        }
        ReportGrid grid = reportDocument.getGrid();
        if (grid == null) {
            return strContent;
        }

        //<editor-fold desc="查询数据">

        Map<String, DataSetResult> dataSetResults = DataUtil.queryAllData(reportDocument);

        //</editor-fold>

        //<editor-fold desc="网格行列信息">

        int rowCount = grid.getRowCount();
        int colCount = grid.getColCount();
        int cellWidth = grid.getCellWidth() / ReportDefine.ENLARGE;
        int cellHeigth = grid.getCellHeight() / ReportDefine.ENLARGE;
        String strWidths = grid.getWidths();
        String strHeights = grid.getHeights();

        //</editor-fold>

        //<editor-fold desc="单元格宽度，高度列表">

        List<Integer> listWidths = new ArrayList<>();
        List<Integer> listHeights = new ArrayList<>();
        String[] temps = strWidths.split(",");
        for (int i = 0; i < colCount; i++) {
            if (i < temps.length) {
                String temp = temps[i];
                listWidths.add(Integer.parseInt(temp) / ReportDefine.ENLARGE);
            } else {
                listWidths.add(cellWidth / ReportDefine.ENLARGE);
            }
        }
        temps = strHeights.split(",");
        for (int i = 0; i < rowCount; i++) {
            if (i < temps.length) {
                String temp = temps[i];
                listHeights.add(Integer.parseInt(temp) / ReportDefine.ENLARGE);
            } else {
                listHeights.add(cellHeigth / ReportDefine.ENLARGE);
            }
        }

        //</editor-fold>

        //<editor-fold desc="单元格字典集合">

        List<ReportCell> cells = reportDocument.getCells();
        Map<String, ReportCell> cellMap = new HashMap<>();
        for (int i = 0; i < cells.size(); i++) {
            ReportCell cell = cells.get(i);
            String key = String.format("%03d%03d", cell.getRowIndex(), cell.getColIndex());
            cellMap.put(key, cell);
        }

        //</editor-fold>

        //<editor-fold desc="生成表格框架">

        Element root = new Element("table");
        Document document = new Document(root);

        //<editor-fold desc="设定列的宽度">

        for (int i = 0; i < colCount; i++) {
            Element col = new Element("col");
            col.setAttribute("width", listWidths.get(i).toString());
            root.addContent(col);
        }

        //</editor-fold>

        //<editor-fold desc="考虑合并单元格的情况，如果有单元格跨了多行或多列,将被跨的行或列的索引记录下来，后面生成每个单元格标签的时候需要跳过">

        List<String> listSkipCells = new ArrayList<>();
        for (int i = 0; i < reportDocument.getCells().size(); i++) {
            ReportCell cell = reportDocument.getCells().get(i);
            int rowIndex = cell.getRowIndex();
            int colIndex = cell.getColIndex();
            int rowSpan = cell.getRowSpan();
            int colSpan = cell.getColSpan();
            for (int k = 0; k < rowSpan; k++) {
                for (int l = 0; l < colSpan; l++) {
                    if (k == 0 && l == 0) {
                        continue;
                    }
                    String key = String.format("%03d%03d", k + rowIndex, l + colIndex);
                    listSkipCells.add(key);
                }
            }
        }

        //</editor-fold>

        //<editor-fold desc="按行，按列依次生成单元格，注意，listSkipCells 中的需要跳过">

        RowState rowState = new RowState();
        rowState.setCellMap(cellMap);
        rowState.setSkipCells(listSkipCells);
        rowState.setColCount(colCount);
        rowState.setTableElement(root);

        for (int i = 0; i < rowCount; i++) {
            rowState.setRowIndex(i);
            rowState.setRowHeight(listHeights.get(i));
            ReportSequence reportSequence = hasSequenceCell(i, colCount, cellMap);
            if (reportSequence != null) {
                //存在数据列，需要循环所有行
                String dataSetName = reportSequence.getDataSetName();
                DataSetResult dataSetResult = dataSetResults.get(dataSetName);
                if (dataSetResult == null) {
                    generateSingleRow(rowState);
                } else {
                    List<Map<String, QueryResult>> queryResults = dataSetResult.getResult();
                    for (int k = 0; k < queryResults.size(); k++) {
                        Map<String, QueryResult> queryResult = queryResults.get(k);
                        rowState.setQueryResults(queryResult);
                        generateSingleRow(rowState);
                    }
                }
            } else {
                generateSingleRow(rowState);
            }
        }

        //</editor-fold>

        Format format = Format.getCompactFormat();
        format.setEncoding("utf-8");//UTF-8编码
        format.setIndent("    ");   //4个字符的缩进
        XMLOutputter out = new XMLOutputter(format);
        strContent = out.outputString(document);

        //</editor-fold>

        return strContent;
    }

    public static void generateSingleRow(RowState rowState) {
        int rowIndex = rowState.getRowIndex();
        int rowHeight = rowState.getRowHeight();
        int colCount = rowState.getColCount();
        Map<String, ReportCell> cellMap = rowState.getCellMap();
        List<String> skipCells = rowState.getSkipCells();
        Element tableElement = rowState.getTableElement();
        Map<String, QueryResult> queryResults = rowState.getQueryResults();
        int height = rowHeight;
        Element tableRow = new Element("tr");
        tableRow.setAttribute("style", String.format("height:%dpx;", height));
        for (int j = 0; j < colCount; j++) {
            String key = String.format("%03d%03d", rowIndex, j);
            if (skipCells.contains(key)) {
                continue;       //被跨的单元格跳过
            }
            ReportCell cell = cellMap.get(key);
            if (cell == null) {
                Element tableCell = new Element("td");
                tableRow.addContent(tableCell);
            } else {
                Element tableCell = new Element("td");
                tableCell.setAttribute("rowspan", String.format("%d", cell.getRowSpan()));
                tableCell.setAttribute("colspan", String.format("%d", cell.getColSpan()));
                Element div = null;
                if (cell.getElement() != null) {
                    ReportElement reportElement = cell.getElement();
                    if (reportElement instanceof ReportText) {
                        //静态文本
                        div = new Element("div");
                        ReportText reportText = (ReportText) reportElement;
                        div.addContent(reportText.getText());
                    }
                    if (reportElement instanceof ReportSequence) {
                        //数据列
                        div = new Element("div");
                        ReportSequence reportSequence = (ReportSequence) reportElement;
                        if (queryResults != null) {
                            String fieldName = reportSequence.getDataFieldName();
                            QueryResult queryResult = queryResults.get(fieldName);
                            if (queryResult != null) {
                                String content = queryResult.getValue().toString();
                                div.addContent(content);
                            } else {
                                div.addContent(reportSequence.getExpression());
                            }
                        } else {
                            div.addContent(reportSequence.getExpression());
                        }
                    }
                }

                int styleIndex = cell.getStyle();
                if (styleIndex >= 0) {
                    tableCell.setAttribute("class", String.format("style_%d", cell.getStyle()));
                }

                if (div != null) {
                    tableCell.addContent(div);
                }

                tableRow.addContent(tableCell);
            }
        }
        tableElement.addContent(tableRow);
    }

    public static ReportSequence hasSequenceCell(int rowIndex, int colCount, Map<String, ReportCell> cellMap) {
        for (int i = 0; i < colCount; i++) {
            String key = String.format("%03d%03d", rowIndex, i);
            ReportCell reportCell = cellMap.get(key);
            if (reportCell == null) {
                continue;
            }
            ReportElement reportElement = reportCell.getElement();
            if (reportElement instanceof ReportSequence) {
                return (ReportSequence) reportElement;
            }
        }
        return null;
    }
}
