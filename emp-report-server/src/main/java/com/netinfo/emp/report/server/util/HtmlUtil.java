package com.netinfo.emp.report.server.util;

import com.netinfo.emp.report.model.*;
import com.netinfo.emp.report.server.entity.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Collator;
import java.util.*;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.util
 * <p>
 * Created by Charley on 2017/5/5.
 */
public class HtmlUtil {

    private static Logger logger = LoggerFactory.getLogger(HtmlUtil.class);

    public static String generateHtml(ReportGenerator generator) {
        String strContent = "";

        //<editor-fold desc="对象检查">

        if (generator == null) {
            logger.error(String.format("ReportGenerator is null."));
            return strContent;
        }
        ReportDocument reportDocument = generator.getReportDocument();
        if (reportDocument == null) {
            logger.error(String.format("ReportDocument is null."));
            return strContent;
        }
        ReportGrid reportGrid = reportDocument.getGrid();
        if (reportGrid == null) {
            logger.error(String.format("ReportGrid is null."));
            return strContent;
        }

        //</editor-fold>

        //<editor-fold desc="网格行列信息">

        int rowCount = reportGrid.getRowCount();
        int colCount = reportGrid.getColCount();
        int cellWidth = reportGrid.getCellWidth() / ReportDefine.ENLARGE;
        int cellHeight = reportGrid.getCellHeight() / ReportDefine.ENLARGE;
        String widths = reportGrid.getWidths();
        String heights = reportGrid.getHeights();

        Map<Integer, SizeParam> widthParamMap = new TreeMap<>();
        Map<Integer, SizeParam> heightParamMap = new TreeMap<>();
        String[] temps = widths.split(",");
        for (int i = 0; i < colCount; i++) {
            SizeParam widthParam = new SizeParam();
            String name = String.format("c_%03d", i);
            widthParam.setId(i);
            widthParam.setName(name);
            if (i < temps.length) {
                widthParam.setValue(Integer.parseInt(temps[i]) / ReportDefine.ENLARGE);
            } else {
                widthParam.setValue(cellWidth);
            }
            widthParamMap.put(i, widthParam);
        }
        temps = heights.split(",");
        for (int i = 0; i < rowCount; i++) {
            SizeParam heightParam = new SizeParam();
            String name = String.format("r_%03d", i);
            heightParam.setId(i);
            heightParam.setName(name);
            if (i < temps.length) {
                heightParam.setValue(Integer.parseInt(temps[i]) / ReportDefine.ENLARGE);
            } else {
                heightParam.setValue(cellHeight);
            }
            heightParamMap.put(i, heightParam);
        }

        //</editor-fold>

        //<editor-fold desc="查询数据">

        Map<String, DataSetResult> dataSetResultMap;
        if (!generator.isDataLoaded()) {
            dataSetResultMap = DataUtil.queryAllData(generator);
            generator.setDataLoaded(true);
            generator.setPageIndex(0);
            generator.setPageCount(1);
        } else {
            dataSetResultMap = generator.getDataSetResults();
        }

        //</editor-fold>

        //<editor-fold desc="单元格字典集合">

        List<ReportCell> cells = reportDocument.getCells();
        Map<String, ReportCell> reportCellMap = new HashMap<>();
        for (int i = 0; i < cells.size(); i++) {
            ReportCell cell = cells.get(i);
            String key = String.format("%03d%03d", cell.getRowIndex(), cell.getColIndex());
            reportCellMap.put(key, cell);
        }

        //</editor-fold>

        //<editor-fold desc="创建DesignCell">

        Map<String, DesignCell> designCellMap = new HashMap<>();
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                String key = String.format("%03d%03d", row, col);
                ReportCell reportCell = reportCellMap.get(key);
                if (reportCell != null) {
                    DesignCell designCell = new DesignCell();
                    designCell.setKey(key);
                    designCell.setReportCell(reportCell);
                    designCellMap.put(key, designCell);
                }
            }
        }

        //</editor-fold>

        //<editor-fold desc="创建单元格依赖关系">

        ReportCell rootReportCell = new ReportCell();
        rootReportCell.setRowIndex(0);
        rootReportCell.setColIndex(0);
        rootReportCell.setRowSpan(rowCount);
        rootReportCell.setColSpan(colCount);
        RelationCell rootCell = new RelationCell();
        rootCell.setReportCell(rootReportCell);

        //<editor-fold desc="全局属性，每个对象都携带，内容相同，使用时无需判断 Null">

        rootCell.setGenerator(generator);
        rootCell.setReportCellMap(reportCellMap);
        rootCell.setDataSetResultMap(dataSetResultMap);
        rootCell.setDesignCellMap(designCellMap);
        rootCell.setWidthParamMap(widthParamMap);
        rootCell.setHeightParamMap(heightParamMap);

        //</editor-fold>

        generateVerticalCell(rootCell);
        generateHorizontalCell(rootCell);

        //</editor-fold>

        //<editor-fold desc="创建单元格">

        Map<String, GridCell> gridCellMap = new HashMap<>();
        generateGridCell(rootCell, designCellMap, gridCellMap);

        //</editor-fold>

        //<editor-fold desc="打印日志">

        //printDesignCell(rootCell, designCellMap);
        //printRightChildren(rootCell, 0);
        //printBottomChildren(rootCell, 0);
        //printGridCells(gridCellMap);

        //</editor-fold>

        //<editor-fold desc="生成表格内容">

        strContent = generateHtmlContent(rootCell, gridCellMap);

        //</editor-fold>

        return strContent;
    }

    private static String generateHtmlContent(RelationCell rootCell, Map<String, GridCell> gridCellMap) {

        //<editor-fold desc="行列信息">

        Map<Integer, SizeParam> widthParamMap = rootCell.getWidthParamMap();
        Map<Integer, SizeParam> heightParamMap = rootCell.getHeightParamMap();
        if (widthParamMap == null
                || heightParamMap == null) {
            return "";
        }
        int rowCount = heightParamMap.size();
        int colCount = widthParamMap.size();

        //</editor-fold>

        Element table = new Element("table");
        Document document = new Document(table);

        //<editor-fold desc="考虑合并单元格的情况，如果有单元格跨了多行或多列,将被跨的行或列的索引记录下来，后面生成每个单元格标签的时候需要跳过">

        List<String> listSkipCells = new ArrayList<>();
        for (Map.Entry<String, GridCell> entry : gridCellMap.entrySet()) {
            GridCell gridCell = entry.getValue();
            int rowIndex = gridCell.getRowIndex();
            int colIndex = gridCell.getColIndex();
            int rowSpan = gridCell.getRowSpan();
            int colSpan = gridCell.getColSpan();
            for (int k = 0; k < rowSpan; k++) {
                for (int l = 0; l < colSpan; l++) {
                    if (k == 0 && l == 0) {
                        continue;
                    }
                    String key = String.format("%010d%010d", k + rowIndex, l + colIndex);
                    listSkipCells.add(key);
                }
            }
        }

        //</editor-fold>

        //<editor-fold desc="设定列的宽度">

        int colTotalCount = 0;
        for (int i = 0; i < colCount; i++) {
            SizeParam widthParam = widthParamMap.get(i);
            if (widthParam != null) {
                int count = widthParam.getExtCount();
                count++;
                for (int k = 0; k < count; k++) {
                    Element col = new Element("col");
                    col.setAttribute("width", String.format("%d", widthParam.getValue()));
                    colTotalCount++;
                    table.addContent(col);
                }
            }
        }

        //</editor-fold>

        //<editor-fold desc="生成表格内容">

        int row = 0;
        for (int i = 0; i < rowCount; i++) {
            SizeParam heightParam = heightParamMap.get(i);
            if (heightParam != null) {
                int count = heightParam.getExtCount();
                count++;
                for (int k = 0; k < count; k++) {
                    int rowHeight = heightParam.getValue();
                    Element rowElement = new Element("tr");
                    rowElement.setAttribute("style", String.format("height:%dpx;", rowHeight));
                    for (int col = 0; col < colTotalCount; col++) {
                        String key = String.format("%010d%010d", row, col);
                        if (listSkipCells.contains(key)) {
                            continue;
                        }

                        Element cellElement = new Element("td");
                        cellElement.setAttribute("id", String.format("c_%s", key));
                        GridCell gridCell = gridCellMap.get(key);
                        if (gridCell == null) {
                            cellElement.addContent(" ");
                        } else {
                            cellElement.setAttribute("rowspan", String.format("%d", gridCell.getRowSpan()));
                            cellElement.setAttribute("colspan", String.format("%d", gridCell.getColSpan()));
                            cellElement.setAttribute("data-row-index", String.format("%d", gridCell.getRowIndex()));
                            cellElement.setAttribute("data-col-index", String.format("%d", gridCell.getColIndex()));
                            Element contentElement = gridCell.getContent();
                            if (contentElement != null) {
                                cellElement.addContent(gridCell.getContent());
                            }
                        }

                        //<editor-fold desc="单元格样式">

                        if (gridCell != null) {
                            ReportCell reportCell = gridCell.getReportCell();
                            if (reportCell != null) {
                                int styleIndex = reportCell.getStyle();
                                if (styleIndex >= 0) {
                                    cellElement.setAttribute("class", String.format("style-%d", reportCell.getStyle()));
                                }
                            }
                        }

                        //</editor-fold>

                        rowElement.addContent(cellElement);

                    }
                    table.addContent(rowElement);
                    row++;
                }
            }
        }

        //</editor-fold>

        Format format = Format.getCompactFormat();
        format.setEncoding("utf-8");//UTF-8编码
        format.setIndent("    ");   //4个字符的缩进
        XMLOutputter out = new XMLOutputter(format);
        String strContent = out.outputString(document);
        return strContent;
    }

    private static void generateVerticalCell(RelationCell parentCell) {
        ReportCell parentReportCell = parentCell.getReportCell();
        if (parentReportCell == null) {
            return;
        }
        int colCount = 0;
        Map<Integer, SizeParam> widthParamMap = parentCell.getWidthParamMap();
        if (widthParamMap != null) {
            colCount = widthParamMap.size();
        }
        int rowIndex = parentReportCell.getRowIndex();
        int colIndex = parentReportCell.getColIndex();
        int rowSpan = parentReportCell.getRowSpan();

        int realRowIndex = parentCell.getRowIndex();
        RelationCell preCell = null;
        int extRowCount = 0;
        int realRowSpan = 0;
        for (int _row = rowIndex; _row < rowIndex + rowSpan; _row++) {     //单元格可能跨行，所跨的每一行都要处理
            int row = _row;
            for (int _col = colIndex + 1; _col < colCount; _col++) {
                int col = _col;
                String key = String.format("%03d%03d", row, col);
                ReportCell reportCell = parentCell.getReportCellMap().get(key);
                if (reportCell != null) {
                    int childRowSpan = reportCell.getRowSpan();
                    if (childRowSpan > 1) {
                        _row += childRowSpan - 1;        //如果跨行了，处理下一行的时候跳过
                    }
                    int extDirection = reportCell.getExtDirection();

                    //<editor-fold desc="非序列单元格">

                    RelationCell relationCell = new RelationCell();

                    //<editor-fold desc="全局属性">

                    relationCell.setGenerator(parentCell.getGenerator());
                    relationCell.setWidthParamMap(parentCell.getWidthParamMap());
                    relationCell.setHeightParamMap(parentCell.getHeightParamMap());
                    relationCell.setDataSetResultMap(parentCell.getDataSetResultMap());
                    relationCell.setDesignCellMap(parentCell.getDesignCellMap());
                    relationCell.setReportCellMap(parentCell.getReportCellMap());

                    //</editor-fold>

                    if (preCell == null) {
                        relationCell.setRowIndex(realRowIndex + row - rowIndex);
                    } else {
                        relationCell.setRowIndex(preCell.getRowIndex() + preCell.getRowSpan());
                    }
                    relationCell.setLeftParent(parentCell);
                    relationCell.setReportCell(reportCell);
                    relationCell.setDataSetName(parentCell.getDataSetName());
                    for (DataRecord data : parentCell.getDatas()) {
                        relationCell.getDatas().add(data);
                    }

                    ReportElement reportElement = reportCell.getElement();
                    if (reportElement == null) {

                        generateVerticalCell(relationCell);
                        realRowSpan += relationCell.getRowSpan();
                        parentCell.getRightChildren().add(relationCell);
                        DesignCell designCell = parentCell.getDesignCellMap().get(key);
                        if (designCell != null) {
                            designCell.getVerticalChildren().add(relationCell);
                        }
                        preCell = relationCell;
                        break;
                    }
                    if (reportElement instanceof ReportText) {
                        ReportText reportText = (ReportText) reportElement;
                        String content = reportText.getText();
                        if (content != null) {
                            Element contentElement = new Element("div");
                            contentElement.addContent(formatCellContent(reportCell, content));
                            relationCell.setContent(contentElement);
                        }

                        generateVerticalCell(relationCell);
                        realRowSpan += relationCell.getRowSpan();
                        parentCell.getRightChildren().add(relationCell);
                        DesignCell designCell = parentCell.getDesignCellMap().get(key);
                        if (designCell != null) {
                            designCell.getVerticalChildren().add(relationCell);
                        }
                        preCell = relationCell;
                    }
                    if (reportElement instanceof ReportImage) {
                        ReportImage reportImage = (ReportImage) reportElement;
                        Element contentElement = new Element("div");
                        String reportName = parentCell.getGenerator().getReportName();
                        String strId = reportImage.getId();
                        String strExt = reportImage.getExt();
                        Element img = new Element("img");
                        img.setAttribute("src", String.format("/reports/resources/%s/%s%s?r=%d", reportName, strId, strExt, (new Random()).nextInt(100)));
                        img.setAttribute("alt", reportImage.getAlt());
                        img.setAttribute("title", reportImage.getAlt());
                        if (reportImage.getWidth() > 0) {
                            img.setAttribute("width", String.format("%dpx", reportImage.getWidth()));
                        }
                        if (reportImage.getHeight() > 0) {
                            img.setAttribute("height", String.format("%dpx", reportImage.getHeight()));
                        }
                        contentElement.addContent(img);
                        relationCell.setContent(contentElement);

                        generateVerticalCell(relationCell);
                        realRowSpan += relationCell.getRowSpan();
                        parentCell.getRightChildren().add(relationCell);
                        DesignCell designCell = parentCell.getDesignCellMap().get(key);
                        if (designCell != null) {
                            designCell.getVerticalChildren().add(relationCell);
                        }
                        preCell = relationCell;
                    }

                    //</editor-fold>

                    //<editor-fold desc="序列单元格，如果是序列单元格，要循环序列中的每一个元素">

                    if (reportElement instanceof ReportSequence) {
                        ReportSequence reportSequence = (ReportSequence) reportElement;
                        String dataSetName = parentCell.getDataSetName();
                        String thisDataSetName = reportSequence.getDataSetName();
                        String thisFieldName = reportSequence.getDataFieldName();
                        thisFieldName = String.format("%s.%s", thisDataSetName, thisFieldName);
                        List<DataRecord> datas = null;
                        if (dataSetName == null
                                || !thisDataSetName.equals(dataSetName)) {
                            DataSetResult dataSetResult = parentCell.getDataSetResultMap().get(thisDataSetName);
                            if (dataSetResult != null) {
                                datas = dataSetResult.getResult();
                            }
                        } else {
                            datas = parentCell.getDatas();
                        }
                        if (datas != null) {
                            int optMethod = reportSequence.getOptMethod();
                            if (optMethod == ReportDefine.DATA_OPT_METHOD_GROUP) {
                                Map<String, GroupResult> groupResultMap = DataUtil.filterFieldData(thisFieldName, datas);
                                List<GroupResult> groupResults = new ArrayList<>();
                                for (Map.Entry<String, GroupResult> entry : groupResultMap.entrySet()) {
                                    groupResults.add(entry.getValue());
                                }
                                Collections.sort(groupResults, (o1, o2) -> Collator.getInstance(Locale.CHINA).compare(o1.getGroupValue(), o2.getGroupValue()));
                                int count = groupResults.size();

                                if (extDirection == ReportDefine.CELL_EXT_METHOD_NONE) {

                                    //<editor-fold desc="不扩展的单元格，将数据用逗号连接起来">

                                    String strContent = "";
                                    for (GroupResult groupResult : groupResults) {
                                        strContent += String.format("%s,", groupResult.getGroupValue());
                                    }
                                    if (strContent.length() > 0) {
                                        strContent = strContent.substring(0, strContent.length() - 1);
                                    }
                                    Element contentElement = new Element("div");
                                    contentElement.addContent(strContent);
                                    relationCell.setContent(contentElement);

                                    generateVerticalCell(relationCell);
                                    realRowSpan += relationCell.getRowSpan();
                                    parentCell.getRightChildren().add(relationCell);
                                    DesignCell designCell = parentCell.getDesignCellMap().get(key);
                                    if (designCell != null) {
                                        designCell.getVerticalChildren().add(relationCell);
                                    }
                                    preCell = relationCell;

                                    //</editor-fold>

                                }

                                if (extDirection == ReportDefine.CELL_EXT_METHOD_VERTICAL) {

                                    //<editor-fold desc="如果序列个数超过一个，需要增加行数，向 HeightParamMap 中追加">

                                    if (count > 1) {
                                        extRowCount += (count - 1) * reportCell.getRowSpan();
                                        extRowParam(reportCell, count - 1, parentCell.getHeightParamMap());
                                    }

                                    //</editor-fold>

                                    //<editor-fold desc="遍历创建序列中的每一个单元格">

                                    for (int i = 0; i < count; i++) {
                                        relationCell = new RelationCell();

                                        //<editor-fold desc="全局属性">

                                        relationCell.setGenerator(parentCell.getGenerator());
                                        relationCell.setWidthParamMap(parentCell.getWidthParamMap());
                                        relationCell.setHeightParamMap(parentCell.getHeightParamMap());
                                        relationCell.setDataSetResultMap(parentCell.getDataSetResultMap());
                                        relationCell.setDesignCellMap(parentCell.getDesignCellMap());
                                        relationCell.setReportCellMap(parentCell.getReportCellMap());

                                        //</editor-fold>

                                        if (preCell == null) {
                                            relationCell.setRowIndex(realRowIndex + row - rowIndex);
                                        } else {
                                            relationCell.setRowIndex(preCell.getRowIndex() + preCell.getRowSpan());
                                        }
                                        relationCell.setLeftParent(parentCell);
                                        relationCell.setReportCell(reportCell);
                                        relationCell.setDataSetName(thisDataSetName);
                                        GroupResult groupResult = groupResults.get(i);
                                        for (DataRecord data :
                                                groupResult.getResults()) {
                                            relationCell.getDatas().add(data);
                                        }
                                        Element contentElement = new Element("div");
                                        contentElement.addContent(formatCellContent(reportCell, groupResult.getGroupValue()));
                                        relationCell.setContent(contentElement);

                                        generateVerticalCell(relationCell);
                                        realRowSpan += relationCell.getRowSpan();
                                        parentCell.getRightChildren().add(relationCell);
                                        DesignCell designCell = parentCell.getDesignCellMap().get(key);
                                        if (designCell != null) {
                                            designCell.getVerticalChildren().add(relationCell);
                                        }
                                        preCell = relationCell;
                                    }

                                    //</editor-fold>
                                }

                                if (extDirection == ReportDefine.CELL_EXT_METHOD_HORIZONTAL) {

                                    //<editor-fold desc="横向扩展，先占位，后面处理">

                                    String strContent = reportSequence.getExpression();
                                    Element contentElement = new Element("div");
                                    contentElement.addContent(strContent);
                                    relationCell.setContent(contentElement);

                                    generateVerticalCell(relationCell);
                                    realRowSpan += relationCell.getRowSpan();
                                    parentCell.getRightChildren().add(relationCell);
                                    DesignCell designCell = parentCell.getDesignCellMap().get(key);
                                    if (designCell != null) {
                                        designCell.getVerticalChildren().add(relationCell);
                                    }
                                    preCell = relationCell;

                                    //</editor-fold>

                                }

                            }
                            if (optMethod == ReportDefine.DATA_OPT_METHOD_LIST) {
                                int count = datas.size();

                                if (extDirection == ReportDefine.CELL_EXT_METHOD_NONE) {

                                    //<editor-fold desc="不扩展的单元格，将数据用逗号连接起来">

                                    String strContent = "";
                                    for (DataRecord data : datas) {
                                        QueryResult queryResult = data.getResults().get(thisFieldName);
                                        if (queryResult != null) {
                                            strContent += String.format("%s,", queryResult.getValue());
                                        }
                                    }
                                    if (strContent.length() > 0) {
                                        strContent = strContent.substring(0, strContent.length() - 1);
                                    }
                                    Element contentElement = new Element("div");
                                    contentElement.addContent(strContent);
                                    relationCell.setContent(contentElement);

                                    generateVerticalCell(relationCell);
                                    realRowSpan += relationCell.getRowSpan();
                                    parentCell.getRightChildren().add(relationCell);
                                    DesignCell designCell = parentCell.getDesignCellMap().get(key);
                                    if (designCell != null) {
                                        designCell.getVerticalChildren().add(relationCell);
                                    }

                                    //</editor-fold>

                                }

                                if (extDirection == ReportDefine.CELL_EXT_METHOD_VERTICAL) {

                                    //<editor-fold desc="如果序列个数超过一个，需要增加行数，向 HeightParamMap 中追加">

                                    if (count > 1) {
                                        extRowCount += (count - 1) * reportCell.getRowSpan();
                                        extRowParam(reportCell, count - 1, parentCell.getHeightParamMap());
                                    }

                                    //</editor-fold>

                                    //<editor-fold desc="遍历创建序列中的每一个单元格">

                                    for (int i = 0; i < count; i++) {
                                        DataRecord data = datas.get(i);
                                        String strContent = "";
                                        QueryResult queryResult = data.getResults().get(thisFieldName);
                                        if (queryResult != null) {
                                            strContent = queryResult.getValue().toString();
                                        }
                                        relationCell = new RelationCell();

                                        //<editor-fold desc="全局属性">

                                        relationCell.setGenerator(parentCell.getGenerator());
                                        relationCell.setWidthParamMap(parentCell.getWidthParamMap());
                                        relationCell.setHeightParamMap(parentCell.getHeightParamMap());
                                        relationCell.setDataSetResultMap(parentCell.getDataSetResultMap());
                                        relationCell.setDesignCellMap(parentCell.getDesignCellMap());
                                        relationCell.setReportCellMap(parentCell.getReportCellMap());

                                        //</editor-fold>

                                        if (preCell == null) {
                                            relationCell.setRowIndex(realRowIndex + row - rowIndex);
                                        } else {
                                            relationCell.setRowIndex(preCell.getRowIndex() + preCell.getRowSpan());
                                        }
                                        relationCell.setLeftParent(parentCell);
                                        relationCell.setReportCell(reportCell);
                                        relationCell.setDataSetName(thisDataSetName);
                                        relationCell.getDatas().add(datas.get(i));
                                        Element contentElement = new Element("div");
                                        contentElement.addContent(formatCellContent(reportCell, strContent));
                                        relationCell.setContent(contentElement);
                                        generateVerticalCell(relationCell);
                                        realRowSpan += relationCell.getRowSpan();
                                        parentCell.getRightChildren().add(relationCell);
                                        DesignCell designCell = parentCell.getDesignCellMap().get(key);
                                        if (designCell != null) {
                                            designCell.getVerticalChildren().add(relationCell);
                                        }
                                        preCell = relationCell;

                                    }

                                    //</editor-fold>

                                }

                                if (extDirection == ReportDefine.CELL_EXT_METHOD_HORIZONTAL) {

                                    //<editor-fold desc="横向扩展，先占位，后面处理">

                                    String strContent = reportSequence.getExpression();
                                    Element contentElement = new Element("div");
                                    contentElement.addContent(strContent);
                                    relationCell.setContent(contentElement);

                                    generateVerticalCell(relationCell);
                                    realRowSpan += relationCell.getRowSpan();
                                    parentCell.getRightChildren().add(relationCell);
                                    DesignCell designCell = parentCell.getDesignCellMap().get(key);
                                    if (designCell != null) {
                                        designCell.getVerticalChildren().add(relationCell);
                                    }
                                    preCell = relationCell;

                                    //</editor-fold>

                                }

                            }
                            if (optMethod == ReportDefine.DATA_OPT_METHOD_COLLECT) {

                                //<editor-fold desc="汇总计算">

                                double doubleValue = 0;
                                int collectMode = reportSequence.getCollectMode();
                                if (collectMode == ReportDefine.SEQUENCE_COLLECT_MODE_SUM) {
                                    for (int i = 0; i < datas.size(); i++) {
                                        DataRecord queryResultMap = datas.get(i);
                                        QueryResult queryResult = queryResultMap.getResults().get(thisFieldName);
                                        if (queryResult != null) {
                                            try {
                                                doubleValue += Double.parseDouble(queryResult.getValue().toString());
                                            } catch (Exception ex) {
                                            }
                                        }
                                    }
                                }
                                String strValue = String.format("%f", doubleValue);
                                Element contentElement = new Element("div");
                                contentElement.addContent(formatCellContent(reportCell, strValue));
                                relationCell.setContent(contentElement);

                                generateVerticalCell(relationCell);
                                realRowSpan += relationCell.getRowSpan();
                                parentCell.getRightChildren().add(relationCell);
                                DesignCell designCell = parentCell.getDesignCellMap().get(key);
                                if (designCell != null) {
                                    designCell.getVerticalChildren().add(relationCell);
                                }

                                //</editor-fold>

                            }
                        }
                    }

                    //</editor-fold>

                    break;
                }
            }
        }
        parentCell.setExtRowCount(extRowCount);
        parentCell.setRowSpan(realRowSpan > 0 ? realRowSpan : rowSpan);
    }

    private static void generateHorizontalCell(RelationCell parentCell) {
        ReportCell parentReportCell = parentCell.getReportCell();
        if (parentReportCell == null) {
            return;
        }
        int rowCount = 0;
        Map<Integer, SizeParam> heightParamMap = parentCell.getHeightParamMap();
        if (heightParamMap != null) {
            rowCount = heightParamMap.size();
        }
        int rowIndex = parentReportCell.getRowIndex();
        int colIndex = parentReportCell.getColIndex();
        int colSpan = parentReportCell.getColSpan();

        int realColIndex = parentCell.getColIndex();
        RelationCell preCell = null;
        int extColCount = 0;
        int realColSpan = 0;
        for (int _col = colIndex; _col < colIndex + colSpan; _col++) {
            int col = _col;
            for (int _row = rowIndex + 1; _row < rowCount; _row++) {
                int row = _row;
                String key = String.format("%03d%03d", row, col);
                ReportCell reportCell = parentCell.getReportCellMap().get(key);
                if (reportCell != null) {
                    int childColSpan = reportCell.getColSpan();
                    if (childColSpan > 1) {
                        _col += childColSpan - 1;
                    }
                    int extDirection = reportCell.getExtDirection();

                    //<editor-fold desc="非序列单元格">

                    RelationCell relationCell = new RelationCell();

                    //<editor-fold desc="全局属性">

                    relationCell.setGenerator(parentCell.getGenerator());
                    relationCell.setWidthParamMap(parentCell.getWidthParamMap());
                    relationCell.setHeightParamMap(parentCell.getHeightParamMap());
                    relationCell.setDataSetResultMap(parentCell.getDataSetResultMap());
                    relationCell.setDesignCellMap(parentCell.getDesignCellMap());
                    relationCell.setReportCellMap(parentCell.getReportCellMap());

                    //</editor-fold>

                    if (preCell == null) {
                        relationCell.setColIndex(realColIndex + col - colIndex);
                    } else {
                        relationCell.setColIndex(preCell.getColIndex() + preCell.getColSpan());
                    }
                    relationCell.setTopParent(parentCell);
                    relationCell.setReportCell(reportCell);
                    relationCell.setDataSetName(parentCell.getDataSetName());
                    for (DataRecord data : parentCell.getDatas()) {
                        relationCell.getDatas().add(data);
                    }

                    ReportElement reportElement = reportCell.getElement();
                    if (reportElement == null) {

                        generateHorizontalCell(relationCell);
                        realColSpan += relationCell.getColSpan();
                        parentCell.getBottomChildren().add(relationCell);
                        DesignCell designCell = parentCell.getDesignCellMap().get(key);
                        if (designCell != null) {
                            designCell.getHorizontalChildren().add(relationCell);
                        }
                        preCell = relationCell;
                        break;
                    }
                    if (reportElement instanceof ReportText) {
                        ReportText reportText = (ReportText) reportElement;
                        String content = reportText.getText();
                        if (content != null) {
                            Element contentElement = new Element("div");
                            contentElement.addContent(formatCellContent(reportCell, content));
                            relationCell.setContent(contentElement);
                        }

                        generateHorizontalCell(relationCell);
                        realColSpan += relationCell.getColSpan();
                        parentCell.getBottomChildren().add(relationCell);
                        DesignCell designCell = parentCell.getDesignCellMap().get(key);
                        if (designCell != null) {
                            designCell.getHorizontalChildren().add(relationCell);
                        }
                        preCell = relationCell;
                    }
                    if (reportElement instanceof ReportImage) {
                        ReportImage reportImage = (ReportImage) reportElement;
                        Element contentElement = new Element("div");
                        String reportName = parentCell.getGenerator().getReportName();
                        String strId = reportImage.getId();
                        String strExt = reportImage.getExt();
                        Element img = new Element("img");
                        img.setAttribute("src", String.format("/reports/resources/%s/%s%s?r=%d", reportName, strId, strExt, (new Random()).nextInt(100)));
                        img.setAttribute("alt", reportImage.getAlt());
                        img.setAttribute("title", reportImage.getAlt());
                        if (reportImage.getWidth() > 0) {
                            img.setAttribute("width", String.format("%dpx", reportImage.getWidth()));
                        }
                        if (reportImage.getHeight() > 0) {
                            img.setAttribute("height", String.format("%dpx", reportImage.getHeight()));
                        }
                        contentElement.addContent(img);
                        relationCell.setContent(contentElement);

                        generateHorizontalCell(relationCell);
                        realColSpan += relationCell.getColSpan();
                        parentCell.getBottomChildren().add(relationCell);
                        DesignCell designCell = parentCell.getDesignCellMap().get(key);
                        if (designCell != null) {
                            designCell.getHorizontalChildren().add(relationCell);
                        }
                        preCell = relationCell;
                    }

                    //</editor-fold>

                    //<editor-fold desc="序列单元格">

                    if (reportElement instanceof ReportSequence) {
                        ReportSequence reportSequence = (ReportSequence) reportElement;
                        String dataSetName = parentCell.getDataSetName();
                        String thisDataSetName = reportSequence.getDataSetName();
                        String thisFieldName = reportSequence.getDataFieldName();
                        thisFieldName = String.format("%s.%s", thisDataSetName, thisFieldName);
                        List<DataRecord> datas = null;
                        if (dataSetName == null
                                || !thisDataSetName.equals(dataSetName)) {
                            DataSetResult dataSetResult = parentCell.getDataSetResultMap().get(thisDataSetName);
                            if (dataSetResult != null) {
                                datas = dataSetResult.getResult();
                            }
                        } else {
                            datas = parentCell.getDatas();
                        }
                        if (datas != null) {
                            int optMethod = reportSequence.getOptMethod();
                            if (optMethod == ReportDefine.DATA_OPT_METHOD_GROUP) {
                                Map<String, GroupResult> groupResultMap = DataUtil.filterFieldData(thisFieldName, datas);
                                List<GroupResult> groupResults = new ArrayList<>();
                                for (Map.Entry<String, GroupResult> entry : groupResultMap.entrySet()) {
                                    groupResults.add(entry.getValue());
                                }
                                Collections.sort(groupResults, (o1, o2) -> Collator.getInstance(Locale.CHINA).compare(o1.getGroupValue(), o2.getGroupValue()));
                                int count = groupResults.size();

                                if (extDirection == ReportDefine.CELL_EXT_METHOD_NONE) {

                                    //<editor-fold desc="不扩展的单元格，将数据用逗号连接起来">

                                    String strContent = "";
                                    for (GroupResult groupResult : groupResults) {
                                        strContent += String.format("%s,", groupResult.getGroupValue());
                                    }
                                    if (strContent.length() > 0) {
                                        strContent = strContent.substring(0, strContent.length() - 1);
                                    }
                                    Element contentElement = new Element("div");
                                    contentElement.addContent(strContent);
                                    relationCell.setContent(contentElement);

                                    generateHorizontalCell(relationCell);
                                    realColSpan += relationCell.getColSpan();
                                    parentCell.getBottomChildren().add(relationCell);
                                    DesignCell designCell = parentCell.getDesignCellMap().get(key);
                                    if (designCell != null) {
                                        designCell.getHorizontalChildren().add(relationCell);
                                    }
                                    preCell = relationCell;

                                    //</editor-fold>

                                }

                                if (extDirection == ReportDefine.CELL_EXT_METHOD_VERTICAL) {

                                    //<editor-fold desc="纵向扩展">

                                    String strContent = reportSequence.getExpression();
                                    Element contentElement = new Element("div");
                                    contentElement.addContent(strContent);
                                    relationCell.setContent(contentElement);

                                    generateHorizontalCell(relationCell);
                                    realColSpan += relationCell.getColSpan();
                                    parentCell.getBottomChildren().add(relationCell);
                                    DesignCell designCell = parentCell.getDesignCellMap().get(key);
                                    if (designCell != null) {
                                        designCell.getHorizontalChildren().add(relationCell);
                                    }
                                    preCell = relationCell;

                                    //</editor-fold>

                                }

                                if (extDirection == ReportDefine.CELL_EXT_METHOD_HORIZONTAL) {

                                    //<editor-fold desc="如果序列个数超过一个，需要增加列数，向 WidthParamMap 中追加">

                                    if (count > 1) {
                                        extColCount += (count - 1) * reportCell.getColSpan();
                                        extColParam(reportCell, count - 1, parentCell.getWidthParamMap());
                                    }

                                    //</editor-fold>

                                    //<editor-fold desc="遍历创建序列中的每一个单元格">

                                    for (int i = 0; i < count; i++) {
                                        relationCell = new RelationCell();

                                        //<editor-fold desc="全局属性">

                                        relationCell.setGenerator(parentCell.getGenerator());
                                        relationCell.setWidthParamMap(parentCell.getWidthParamMap());
                                        relationCell.setHeightParamMap(parentCell.getHeightParamMap());
                                        relationCell.setDataSetResultMap(parentCell.getDataSetResultMap());
                                        relationCell.setDesignCellMap(parentCell.getDesignCellMap());
                                        relationCell.setReportCellMap(parentCell.getReportCellMap());

                                        //</editor-fold>

                                        if (preCell == null) {
                                            relationCell.setColIndex(realColIndex + col - colIndex);
                                        } else {
                                            relationCell.setColIndex(preCell.getColIndex() + preCell.getColSpan());
                                        }
                                        relationCell.setTopParent(parentCell);
                                        relationCell.setReportCell(reportCell);
                                        relationCell.setDataSetName(thisDataSetName);
                                        GroupResult groupResult = groupResults.get(i);
                                        for (DataRecord data :
                                                groupResult.getResults()) {
                                            relationCell.getDatas().add(data);
                                        }
                                        Element contentElement = new Element("div");
                                        contentElement.addContent(formatCellContent(reportCell, groupResult.getGroupValue()));
                                        relationCell.setContent(contentElement);

                                        generateHorizontalCell(relationCell);
                                        realColSpan += relationCell.getColSpan();
                                        parentCell.getBottomChildren().add(relationCell);
                                        DesignCell designCell = parentCell.getDesignCellMap().get(key);
                                        if (designCell != null) {
                                            designCell.getHorizontalChildren().add(relationCell);
                                        }
                                        preCell = relationCell;
                                    }

                                    //</editor-fold>
                                }

                            }
                            if (optMethod == ReportDefine.DATA_OPT_METHOD_LIST) {
                                int count = datas.size();

                                if (extDirection == ReportDefine.CELL_EXT_METHOD_NONE) {

                                    //<editor-fold desc="不扩展的单元格，将数据用逗号连接起来">

                                    String strContent = "";
                                    for (DataRecord data : datas) {
                                        QueryResult queryResult = data.getResults().get(thisFieldName);
                                        if (queryResult != null) {
                                            strContent += String.format("%s,", queryResult.getValue());
                                        }
                                    }
                                    if (strContent.length() > 0) {
                                        strContent = strContent.substring(0, strContent.length() - 1);
                                    }
                                    Element contentElement = new Element("div");
                                    contentElement.addContent(strContent);
                                    relationCell.setContent(contentElement);

                                    generateHorizontalCell(relationCell);
                                    realColSpan += relationCell.getColSpan();
                                    parentCell.getBottomChildren().add(relationCell);
                                    DesignCell designCell = parentCell.getDesignCellMap().get(key);
                                    if (designCell != null) {
                                        designCell.getHorizontalChildren().add(relationCell);
                                    }
                                    preCell = relationCell;

                                    //</editor-fold>

                                }

                                if (extDirection == ReportDefine.CELL_EXT_METHOD_VERTICAL) {

                                    //<editor-fold desc="纵向扩展">

                                    String strContent = reportSequence.getExpression();
                                    Element contentElement = new Element("div");
                                    contentElement.addContent(strContent);
                                    relationCell.setContent(contentElement);

                                    generateHorizontalCell(relationCell);
                                    realColSpan += relationCell.getColSpan();
                                    parentCell.getBottomChildren().add(relationCell);
                                    DesignCell designCell = parentCell.getDesignCellMap().get(key);
                                    if (designCell != null) {
                                        designCell.getHorizontalChildren().add(relationCell);
                                    }
                                    preCell = relationCell;

                                    //</editor-fold>

                                }

                                if (extDirection == ReportDefine.CELL_EXT_METHOD_HORIZONTAL) {

                                    //<editor-fold desc="如果序列个数超过一个，需要增加列数，向 WidthParamMap 中追加">

                                    if (count > 1) {
                                        extColCount += (count - 1) * reportCell.getColSpan();
                                        extColParam(reportCell, count - 1, parentCell.getWidthParamMap());
                                    }

                                    //</editor-fold>

                                    //<editor-fold desc="遍历创建序列中的每一个单元格">

                                    for (int i = 0; i < count; i++) {
                                        DataRecord data = datas.get(i);
                                        String strContent = "";
                                        QueryResult queryResult = data.getResults().get(thisFieldName);
                                        if (queryResult != null) {
                                            strContent = queryResult.getValue().toString();
                                        }
                                        relationCell = new RelationCell();

                                        //<editor-fold desc="全局属性">

                                        relationCell.setGenerator(parentCell.getGenerator());
                                        relationCell.setWidthParamMap(parentCell.getWidthParamMap());
                                        relationCell.setHeightParamMap(parentCell.getHeightParamMap());
                                        relationCell.setDataSetResultMap(parentCell.getDataSetResultMap());
                                        relationCell.setDesignCellMap(parentCell.getDesignCellMap());
                                        relationCell.setReportCellMap(parentCell.getReportCellMap());

                                        //</editor-fold>

                                        if (preCell == null) {
                                            relationCell.setColIndex(realColIndex + col - colIndex);
                                        } else {
                                            relationCell.setColIndex(preCell.getColIndex() + preCell.getColSpan());
                                        }
                                        relationCell.setTopParent(parentCell);
                                        relationCell.setReportCell(reportCell);
                                        relationCell.setDataSetName(thisDataSetName);
                                        relationCell.getDatas().add(datas.get(i));
                                        Element contentElement = new Element("div");
                                        contentElement.addContent(formatCellContent(reportCell, strContent));
                                        relationCell.setContent(contentElement);

                                        generateHorizontalCell(relationCell);
                                        realColSpan += relationCell.getColSpan();
                                        parentCell.getBottomChildren().add(relationCell);
                                        DesignCell designCell = parentCell.getDesignCellMap().get(key);
                                        if (designCell != null) {
                                            designCell.getHorizontalChildren().add(relationCell);
                                        }
                                        preCell = relationCell;
                                    }

                                    //</editor-fold>

                                }

                            }
                            if (optMethod == ReportDefine.DATA_OPT_METHOD_COLLECT) {

                                //<editor-fold desc="汇总计算">

                                double doubleValue = 0;
                                int collectMode = reportSequence.getCollectMode();
                                if (collectMode == ReportDefine.SEQUENCE_COLLECT_MODE_SUM) {
                                    for (int i = 0; i < datas.size(); i++) {
                                        DataRecord data = datas.get(i);
                                        QueryResult queryResult = data.getResults().get(thisFieldName);
                                        if (queryResult != null) {
                                            try {
                                                doubleValue += Double.parseDouble(queryResult.getValue().toString());
                                            } catch (Exception ex) {
                                            }
                                        }
                                    }
                                }
                                String strValue = String.format("%f", doubleValue);
                                Element contentElement = new Element("div");
                                contentElement.addContent(formatCellContent(reportCell, strValue));

                                relationCell.setContent(contentElement);
                                generateHorizontalCell(relationCell);
                                realColSpan += relationCell.getColSpan();
                                parentCell.getBottomChildren().add(relationCell);
                                DesignCell designCell = parentCell.getDesignCellMap().get(key);
                                if (designCell != null) {
                                    designCell.getHorizontalChildren().add(relationCell);
                                }
                                preCell = relationCell;

                                //</editor-fold>

                            }
                        }
                    }

                    //</editor-fold>

                    break;
                }
            }
        }
        parentCell.setExtColCount(extColCount);
        parentCell.setColSpan(realColSpan > 0 ? realColSpan : colSpan);
    }

    private static void generateGridCell(RelationCell rootCell, Map<String, DesignCell> designCellMap, Map<String, GridCell> gridCellMap) {
        int rowCount = 0;
        int colCount = 0;
        Map<Integer, SizeParam> widthParamMap = rootCell.getWidthParamMap();
        if (widthParamMap != null) {
            colCount = widthParamMap.size();
        }
        Map<Integer, SizeParam> heightParamMap = rootCell.getHeightParamMap();
        if (heightParamMap != null) {
            rowCount = heightParamMap.size();
        }
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                String key = String.format("%03d%03d", row, col);
                DesignCell designCell = designCellMap.get(key);
                if (designCell == null) {
                    continue;
                }
                ReportCell reportCell = designCell.getReportCell();
                if (reportCell == null) {
                    continue;
                }
                int rowIndex;
                int colIndex;
                int rowSpan;
                int colSpan;
                int vCount = designCell.getVerticalChildren().size();
                int hCount = designCell.getHorizontalChildren().size();

                //<editor-fold desc="不扩展">

                if (vCount == 1
                        && hCount == 1) {
                    //未扩展
                    RelationCell vCell = designCell.getVerticalChildren().get(0);
                    RelationCell hCell = designCell.getHorizontalChildren().get(0);
                    rowIndex = vCell.getRowIndex();
                    colIndex = hCell.getColIndex();
                    rowSpan = vCell.getRowSpan();
                    colSpan = hCell.getColSpan();
                    String id = String.format("%010d%010d", rowIndex, colIndex);
                    GridCell gridCell = new GridCell();
                    gridCell.setReportCell(reportCell);
                    gridCell.setKey(id);
                    gridCell.setRowIndex(rowIndex);
                    gridCell.setColIndex(colIndex);
                    gridCell.setRowSpan(rowSpan);
                    gridCell.setColSpan(colSpan);
                    gridCell.setContent(vCell.getContent());
                    gridCellMap.put(id, gridCell);
                }

                //</editor-fold>

                //<editor-fold desc="只有纵扩展">

                if (vCount > 1
                        && hCount == 1) {
                    RelationCell hCell = designCell.getHorizontalChildren().get(0);
                    colIndex = hCell.getColIndex();
                    colSpan = hCell.getColSpan();
                    for (int i = 0; i < vCount; i++) {
                        RelationCell vCell = designCell.getVerticalChildren().get(i);
                        rowIndex = vCell.getRowIndex();
                        rowSpan = vCell.getRowSpan();
                        String id = String.format("%010d%010d", rowIndex, colIndex);
                        GridCell gridCell = new GridCell();
                        gridCell.setReportCell(reportCell);
                        gridCell.setKey(id);
                        gridCell.setRowIndex(rowIndex);
                        gridCell.setColIndex(colIndex);
                        gridCell.setRowSpan(rowSpan);
                        gridCell.setColSpan(colSpan);
                        gridCell.setContent(vCell.getContent());
                        gridCellMap.put(id, gridCell);
                    }
                }

                //</editor-fold>

                //<editor-fold desc="只有横扩展">

                if (hCount > 1
                        && vCount == 1) {
                    RelationCell vCell = designCell.getVerticalChildren().get(0);
                    rowIndex = vCell.getRowIndex();
                    rowSpan = vCell.getRowSpan();
                    for (int i = 0; i < hCount; i++) {
                        RelationCell hCell = designCell.getHorizontalChildren().get(i);
                        colIndex = hCell.getColIndex();
                        colSpan = hCell.getColSpan();
                        String id = String.format("%010d%010d", rowIndex, colIndex);
                        GridCell gridCell = new GridCell();
                        gridCell.setReportCell(reportCell);
                        gridCell.setKey(id);
                        gridCell.setRowIndex(rowIndex);
                        gridCell.setColIndex(colIndex);
                        gridCell.setRowSpan(rowSpan);
                        gridCell.setColSpan(colSpan);
                        gridCell.setContent(hCell.getContent());
                        gridCellMap.put(id, gridCell);
                    }
                }

                //</editor-fold>

                //<editor-fold desc="交叉扩展">

                if (vCount > 1
                        && hCount > 1) {
                    for (int i = 0; i < vCount; i++) {
                        RelationCell vCell = designCell.getVerticalChildren().get(i);
                        rowIndex = vCell.getRowIndex();
                        rowSpan = vCell.getRowSpan();
                        List<DataRecord> vDatas = vCell.getDatas();
                        for (int j = 0; j < hCount; j++) {
                            RelationCell hCell = designCell.getHorizontalChildren().get(j);
                            colIndex = hCell.getColIndex();
                            colSpan = hCell.getColSpan();
                            String strContent = "";
                            List<DataRecord> hDatas = hCell.getDatas();
                            List<DataRecord> datas = new ArrayList<>();
                            for (DataRecord data : vDatas) {
                                datas.add(data);
                            }
                            datas.retainAll(hDatas);
                            ReportElement reportElement = reportCell.getElement();
                            if (reportElement != null
                                    && reportElement instanceof ReportSequence) {
                                ReportSequence reportSequence = (ReportSequence) reportElement;
                                String dataSetName = reportSequence.getDataSetName();
                                String fieldName = reportSequence.getDataFieldName();
                                fieldName = String.format("%s.%s", dataSetName, fieldName);
                                double value = 0;
                                for (int k = 0; k < datas.size(); k++) {
                                    DataRecord data = datas.get(k);
                                    QueryResult queryResult = data.getResults().get(fieldName);
                                    if (queryResult != null) {
                                        String strValue = queryResult.getValue().toString();
                                        try {
                                            value += Double.parseDouble(strValue);
                                        } catch (Exception ex) {
                                        }
                                    }
                                }
                                strContent = formatCellContent(reportCell, String.format("%f", value));
                            }
                            String id = String.format("%010d%010d", rowIndex, colIndex);
                            GridCell gridCell = new GridCell();
                            gridCell.setReportCell(reportCell);
                            gridCell.setKey(id);
                            gridCell.setRowIndex(rowIndex);
                            gridCell.setColIndex(colIndex);
                            gridCell.setRowSpan(rowSpan);
                            gridCell.setColSpan(colSpan);
                            Element contentElement = new Element("div");
                            contentElement.addContent(strContent);
                            gridCell.setContent(contentElement);
                            gridCellMap.put(id, gridCell);
                        }
                    }
                }

                //</editor-fold>
            }
        }
    }

    private static void printDesignCell(RelationCell rootCell, Map<String, DesignCell> designCellMap) {
        int rowCount = 0;
        int colCount = 0;
        Map<Integer, SizeParam> widthParamMap = rootCell.getWidthParamMap();
        if (widthParamMap != null) {
            colCount = widthParamMap.size();
        }
        Map<Integer, SizeParam> heightParamMap = rootCell.getHeightParamMap();
        if (heightParamMap != null) {
            rowCount = heightParamMap.size();
        }
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                String key = String.format("%03d%03d", row, col);
                DesignCell designCell = designCellMap.get(key);
                if (designCell == null) {
                    continue;
                }
                ReportCell reportCell = designCell.getReportCell();
                if (reportCell == null) {
                    continue;
                }
                int rowIndex = reportCell.getRowIndex();
                int colIndex = reportCell.getColIndex();
                int rowSpan = reportCell.getRowSpan();
                int colSpan = reportCell.getColSpan();
                System.out.println(String.format("Row:%d Col:%d RowSpan:%d ColSpan:%d", rowIndex, colIndex, rowSpan, colSpan));
                int verticalNum = designCell.getVerticalChildren().size();
                int horizontalNum = designCell.getHorizontalChildren().size();
                for (int i = 0; i < verticalNum; i++) {
                    RelationCell relationCell = designCell.getVerticalChildren().get(i);
                    String strValue = "";
                    Element contentElement = relationCell.getContent();
                    if (contentElement != null) {
                        strValue = contentElement.getText();
                    }
                    System.out.println(String.format("--V RowIndex:%d ColIndex:%d RowSpan:%d ColSpan:%d Content:%s",
                            relationCell.getRowIndex(),
                            relationCell.getColIndex(),
                            relationCell.getRowSpan(),
                            relationCell.getColSpan(),
                            strValue));
                    for (DataRecord data : relationCell.getDatas()) {
                        System.out.println(String.format("----%s", data));
                    }
                }
                for (int i = 0; i < horizontalNum; i++) {
                    RelationCell relationCell = designCell.getHorizontalChildren().get(i);
                    String strValue = "";
                    Element contentElement = relationCell.getContent();
                    if (contentElement != null) {
                        strValue = contentElement.getText();
                    }
                    System.out.println(String.format("--H RowIndex:%d ColIndex:%d RowSpan:%d ColSpan:%d Content:%s",
                            relationCell.getRowIndex(),
                            relationCell.getColIndex(),
                            relationCell.getRowSpan(),
                            relationCell.getColSpan(),
                            strValue));
                    for (DataRecord data : relationCell.getDatas()) {
                        System.out.println(String.format("----%s", data));
                    }
                }
            }
        }
    }

    private static void printRightChildren(RelationCell relationCell, int level) {
        level++;
        for (int i = 0; i < relationCell.getRightChildren().size(); i++) {
            RelationCell cell = relationCell.getRightChildren().get(i);
            String strContent = "";
            Element contentElement = cell.getContent();
            if (contentElement != null) {
                strContent = contentElement.getText();
            }
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < level; k++) {
                sb.append("---");
            }
            System.out.println(String.format("TR%s RowIndex:%d, RowSpan:%d ExtRowCount:%d Info:%s",
                    sb,
                    cell.getRowIndex(),
                    cell.getRowSpan(),
                    cell.getExtRowCount(),
                    strContent));
            for (DataRecord data : cell.getDatas()) {
                System.out.println(String.format("----%s", data));
            }
            printRightChildren(cell, level);
        }
    }

    private static void printBottomChildren(RelationCell relationCell, int level) {
        level++;
        for (int i = 0; i < relationCell.getBottomChildren().size(); i++) {
            RelationCell cell = relationCell.getBottomChildren().get(i);
            String strContent = "";
            Element contentElement = cell.getContent();
            if (contentElement != null) {
                strContent = contentElement.getText();
            }
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < level; k++) {
                sb.append("---");
            }
            System.out.println(String.format("TB%s ColIndex:%d ColSpan:%d ExtColCount:%d Info:%s",
                    sb,
                    cell.getColIndex(),
                    cell.getColSpan(),
                    cell.getExtColCount(),
                    strContent));
            for (DataRecord data : cell.getDatas()) {
                System.out.println(String.format("----%s", data));
            }
            printBottomChildren(cell, level);
        }
    }

    private static void printGridCells(Map<String, GridCell> gridCellMap) {
        TreeMap<String, GridCell> tempMap = new TreeMap<>(gridCellMap);
        for (Map.Entry<String, GridCell> entry : tempMap.entrySet()) {
            String key = entry.getKey();
            String content = "";
            GridCell gridCell = entry.getValue();
            Element contentElement = gridCell.getContent();
            if (contentElement != null) {
                content = contentElement.getText();
            }
            System.out.println(String.format("Key:%s RowIndex:%d ColIndex:%d RowSpan:%d ColSpan:%d Content:%s",
                    key,
                    gridCell.getRowIndex(),
                    gridCell.getColIndex(),
                    gridCell.getRowSpan(),
                    gridCell.getColSpan(),
                    content));
        }
    }

    private static String formatCellContent(ReportCell reportCell, String value) {
        String strValue = "";
        int formatType = reportCell.getFormatType();
        if (formatType == ReportDefine.CELL_FORMAT_TYPE_NONE) {
            String str = value;
            if (str == null) {
                return strValue;
            }
            try {
                double doubleValue = Double.parseDouble(str);
                try {
                    int intValue = (int) doubleValue;
                    if (intValue == doubleValue) {
                        strValue = String.format("%d", intValue);
                        return strValue;
                    }
                } catch (Exception ex) {
                }
                strValue = String.format("%.2f", doubleValue);
                return strValue;
            } catch (Exception ex) {
            }
            strValue = str;
        }
        return strValue;
    }

    private static void extRowParam(ReportCell reportCell, int count, Map<Integer, SizeParam> heightParamMap) {
        int rowIndex = reportCell.getRowIndex();
        int rowSpan = reportCell.getRowSpan();
        for (int i = 0; i < rowSpan; i++) {
            int row = rowIndex + i;
            SizeParam heightParam = heightParamMap.get(row);
            if (heightParam != null) {
                heightParam.setExtCount(heightParam.getExtCount() + count);
            }
        }
    }

    private static void extColParam(ReportCell reportCell, int count, Map<Integer, SizeParam> widthParamMap) {
        int colIndex = reportCell.getColIndex();
        int colSpan = reportCell.getColSpan();
        for (int i = 0; i < colSpan; i++) {
            int col = colIndex + i;
            SizeParam widthParam = widthParamMap.get(col);
            if (widthParam != null) {
                widthParam.setExtCount(widthParam.getExtCount() + count);
            }
        }
    }

}
