package com.netinfo.emp.report.server.util;

import com.netinfo.emp.report.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.util
 * <p>
 * Created by Charley on 2017/5/4.
 */
public class ReportUtil {

    private static Logger logger = LoggerFactory.getLogger(ReportUtil.class);

    /**
     * 解析报表脚本文件
     *
     * @param document
     * @return
     */
    public static ReportDocument getReportDocument(Document document) {
        Namespace report = Namespace.getNamespace("", "http://netinfo.com/emp/reports");
        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        ReportDocument reportDocument = new ReportDocument();
        Element root = document.getRootElement();
        reportDocument.setName(root.getAttributeValue("Name"));
        reportDocument.setTitle(root.getAttributeValue("Title"));
        Element grid = root.getChild("Grid", report);
        if (grid != null) {
            ReportGrid reportGrid = new ReportGrid();
            reportGrid.setRowCount(Integer.parseInt(grid.getAttributeValue("RowCount")));
            reportGrid.setColCount(Integer.parseInt(grid.getAttributeValue("ColCount")));
            reportGrid.setCellWidth(Integer.parseInt(grid.getAttributeValue("CellWidth")));
            reportGrid.setCellHeight(Integer.parseInt(grid.getAttributeValue("CellHeight")));
            reportGrid.setWidths(grid.getChildText("Widths", report));
            reportGrid.setHeights(grid.getChildText("Heights", report));
            reportDocument.setGrid(reportGrid);
        }
        Element dataSetsElement = root.getChild("DataSets", report);
        if (dataSetsElement != null) {
            List<Element> dataSets = dataSetsElement.getChildren("DataSet", report);
            for (int i = 0; i < dataSets.size(); i++) {
                Element dataSet = dataSets.get(i);
                ReportDataSet reportDataSet = new ReportDataSet();
                reportDataSet.setName(dataSet.getAttributeValue("Name"));
                reportDataSet.setDataSourceName(dataSet.getAttributeValue("DataSourceName"));
                Element sql = dataSet.getChild("Sql", report);
                if (sql != null) {
                    reportDataSet.setSql(sql.getText());
                }
                Element tablesElement = dataSet.getChild("Tables", report);
                if (tablesElement != null) {
                    List<Element> tables = tablesElement.getChildren("Table", report);
                    for (int j = 0; j < tables.size(); j++) {
                        Element table = tables.get(j);
                        ReportDataTable reportDataTable = new ReportDataTable();
                        reportDataTable.setKey(table.getAttributeValue("Key"));
                        reportDataTable.setName(table.getAttributeValue("Name"));
                        reportDataTable.setDisplay(table.getAttributeValue("Display"));
                        reportDataSet.getTables().add(reportDataTable);
                    }
                }
                Element fieldsElement = dataSet.getChild("Fields", report);
                if (fieldsElement != null) {
                    List<Element> fields = fieldsElement.getChildren("Field", report);
                    for (int j = 0; j < fields.size(); j++) {
                        Element field = fields.get(j);
                        ReportDataField reportDataField = new ReportDataField();
                        reportDataField.setKey(field.getAttributeValue("Key"));
                        reportDataField.setName(field.getAttributeValue("Name"));
                        reportDataField.setDisplay(field.getAttributeValue("Display"));
                        reportDataField.setDataType(Integer.parseInt(field.getAttributeValue("DataType")));
                        reportDataField.setFieldName(field.getAttributeValue("FieldName"));
                        reportDataField.setTableName(field.getAttributeValue("TableName"));
                        reportDataSet.getFields().add(reportDataField);
                    }
                }
                Element conditionsElement = dataSet.getChild("Conditions", report);
                if (conditionsElement != null) {
                    List<Element> conditions = conditionsElement.getChildren("Condition", report);
                    for (int j = 0; j < conditions.size(); j++) {
                        Element condition = conditions.get(j);
                        ReportCondition reportCondition = new ReportCondition();
                        reportCondition.setField(condition.getAttributeValue("Field"));
                        reportCondition.setJudge(Integer.parseInt(condition.getAttributeValue("Judge")));
                        reportCondition.setRelation(Integer.parseInt(condition.getAttributeValue("Relation")));
                        Element conditionValue = condition.getChild("Value", report);
                        if (conditionValue != null) {
                            reportCondition.setValue(conditionValue.getText());
                        }
                        reportDataSet.getConditions().add(reportCondition);
                    }
                }
                Element ordersElement = dataSet.getChild("Orders", report);
                if (ordersElement != null) {
                    List<Element> orders = ordersElement.getChildren("Order", report);
                    for (int j = 0; j < orders.size(); j++) {
                        Element order = orders.get(j);
                        ReportOrder reportOrder = new ReportOrder();
                        reportOrder.setField(order.getAttributeValue("Field"));
                        reportOrder.setDirection(Integer.parseInt(order.getAttributeValue("Direction")));
                        reportDataSet.getOrders().add(reportOrder);
                    }
                }
                reportDocument.getDataSets().add(reportDataSet);
            }
        }
        Element cellsElement = root.getChild("Cells", report);
        if (cellsElement != null) {
            List<Element> cells = cellsElement.getChildren("Cell", report);
            for (int i = 0; i < cells.size(); i++) {
                Element cell = cells.get(i);
                ReportCell reportCell = new ReportCell();
                reportCell.setRowIndex(Integer.parseInt(cell.getAttributeValue("RowIndex")));
                reportCell.setColIndex(Integer.parseInt(cell.getAttributeValue("ColIndex")));
                reportCell.setRowSpan(Integer.parseInt(cell.getAttributeValue("RowSpan")));
                reportCell.setColSpan(Integer.parseInt(cell.getAttributeValue("ColSpan")));
                reportCell.setLinkUrl(cell.getAttributeValue("LinkUrl"));
                reportCell.setExtDirection(Integer.parseInt(cell.getAttributeValue("ExtDirection")));
                reportCell.setLeftParent(cell.getAttributeValue("LeftParent"));
                reportCell.setTopParent(cell.getAttributeValue("TopParent"));
                reportCell.setFormatType(Integer.parseInt(cell.getAttributeValue("FormatType")));
                reportCell.setFormatString(cell.getAttributeValue("FormatString"));
                if (cell.getAttributeValue("Style") != null) {
                    reportCell.setStyle(Integer.parseInt(cell.getAttributeValue("Style")));
                }
                Element element = cell.getChild("Element", report);
                if (element != null) {
                    String strElementType = element.getAttributeValue("type", xsi);
                    if (strElementType.equals("ReportText")) {
                        ReportText reportText = new ReportText();
                        reportText.setText(element.getChildText("Text", report));
                        reportCell.setElement(reportText);
                    }
                    if (strElementType.equals("ReportSequence")) {
                        ReportSequence reportSequence = new ReportSequence();
                        reportSequence.setDataSetName(element.getAttributeValue("DataSetName"));
                        reportSequence.setDataTableName(element.getAttributeValue("DataTableName"));
                        reportSequence.setDataFieldName(element.getAttributeValue("DataFieldName"));
                        reportSequence.setExpression(element.getChildText("Expression", report));
                        reportSequence.setOptMethod(Integer.parseInt(element.getAttributeValue("DataOptMethod")));
                        reportSequence.setGroupMode(Integer.parseInt(element.getAttributeValue("GroupMode")));
                        reportSequence.setCollectMode(Integer.parseInt(element.getAttributeValue("CollectMode")));
                        reportCell.setElement(reportSequence);
                    }
                    if (strElementType.equals("ReportImage")) {
                        ReportImage reportImage = new ReportImage();
                        reportImage.setId(element.getAttributeValue("ID"));
                        reportImage.setWidth(Integer.parseInt(element.getAttributeValue("Width")));
                        reportImage.setHeight(Integer.parseInt(element.getAttributeValue("Height")));
                        reportImage.setStretch(Integer.parseInt(element.getAttributeValue("Stretch")));
                        reportImage.setExtension(Integer.parseInt(element.getAttributeValue("Extension")));
                        reportImage.setAlt(element.getChildText("Alt", report));
                        reportCell.setElement(reportImage);
                    }
                }
                reportDocument.getCells().add(reportCell);
            }
        }
        Element stylesElement = root.getChild("Styles", report);
        if (stylesElement != null) {
            List<Element> styles = stylesElement.getChildren("Style", report);
            for (int i = 0; i < styles.size(); i++) {
                Element style = styles.get(i);
                VisualStyle visualStyle = new VisualStyle();
                visualStyle.setFontFamily(style.getAttributeValue("FontFamily"));
                visualStyle.setFontSize(Integer.parseInt(style.getAttributeValue("FontSize")));
                visualStyle.setFontStyle(Integer.parseInt(style.getAttributeValue("FontStyle")));
                visualStyle.setForeColor(style.getAttributeValue("ForeColor"));
                visualStyle.setBackColor(style.getAttributeValue("BackColor"));
                visualStyle.setWidth(Integer.parseInt(style.getAttributeValue("Width")));
                visualStyle.setHeight(Integer.parseInt(style.getAttributeValue("Height")));
                visualStyle.sethAlign(Integer.parseInt(style.getAttributeValue("HAlign")));
                visualStyle.setvAlign(Integer.parseInt(style.getAttributeValue("VAlign")));
                Element border = style.getChild("Border", report);
                if (border != null) {
                    ReportBorder reportBorder = new ReportBorder();
                    reportBorder.setLeft(Integer.parseInt(border.getAttributeValue("Left")));
                    reportBorder.setTop(Integer.parseInt(border.getAttributeValue("Top")));
                    reportBorder.setRight(Integer.parseInt(border.getAttributeValue("Right")));
                    reportBorder.setBottom(Integer.parseInt(border.getAttributeValue("Bottom")));
                    visualStyle.setBorder(reportBorder);
                }
                reportDocument.getStyles().add(visualStyle);
            }
        }
        return reportDocument;
    }
}
