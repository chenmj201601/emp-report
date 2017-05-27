package com.netinfo.emp.report.server.util;

import com.netinfo.emp.report.model.*;
import com.netinfo.emp.report.server.entity.DataSetResult;
import com.netinfo.emp.report.server.entity.QueryResult;
import com.netinfo.emp.report.server.entity.ReportGenerator;
import com.netinfo.emp.report.server.entity.RowState;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Parent;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.util
 * <p>
 * Created by Charley on 2017/5/5.
 */
public class HtmlUtil {

    private static Logger logger = LoggerFactory.getLogger(HtmlUtil.class);

    /**
     * 生成报表主体内容
     *
     * @param generator
     * @return
     */
    public static String generateHtml(ReportGenerator generator) {
        String strContent = "";
        if (generator == null) {
            logger.error(String.format("ReportGenerator is null."));
            return strContent;
        }
        String reportName = generator.getReportName();
        ReportDocument reportDocument = generator.getReportDocument();
        if (reportDocument == null) {
            logger.error(String.format("ReportDocument not exist. %s", reportName));
            return strContent;
        }
        ReportGrid grid = reportDocument.getGrid();
        if (grid == null) {
            return strContent;
        }

        //<editor-fold desc="查询数据">

        Map<String, DataSetResult> dataSetResults;
        if (!generator.isDataLoaded()) {
            dataSetResults = DataUtil.queryAllData(generator);
            generator.setDataLoaded(true);
            generator.setPageIndex(0);
            generator.setPageCount(1);
        } else {
            dataSetResults = generator.getDataSetResults();
        }


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

        //<editor-fold desc="分页相关">

        //默认值
        int maxPageHeight = 1000;
        rowState.setMaxPageHeight(maxPageHeight);
        rowState.setPageHeight(0);
        rowState.setPageIndex(0);
        generator.setPageCount(1);

        //</editor-fold>

        //<editor-fold desc="单元格内容">

        for (int i = 0; i < rowCount; i++) {
            rowState.setRowIndex(i);
            rowState.setRowHeight(listHeights.get(i));
            rowState.setDataIndex(0);
            rowState.setQueryResultsCollection(null);
            List<ReportSequence> reportSequences = getSequenceCellInRow(i, colCount, cellMap);
            if (reportSequences.size() > 0) {
                //存在数据列，需要循环所有行
                for (int m = 0; m < reportSequences.size(); m++) {
                    ReportSequence reportSequence = reportSequences.get(m);
                    String dataSetName = reportSequence.getDataSetName();
                    DataSetResult dataSetResult = dataSetResults.get(dataSetName);
                    if (dataSetResult == null) {
                        generateSingleRow(rowState, generator);
                    } else {
                        List<Map<String, QueryResult>> queryResultsCollection = dataSetResult.getResult();
                        rowState.setQueryResultsCollection(queryResultsCollection);
                        int extMethod = reportSequence.getExtMethod();
                        if (extMethod == ReportDefine.SEQUENCE_EXT_METHOD_NONE) {
                            //不扩展
                            rowState.setDataIndex(0);
                            generateSingleRow(rowState, generator);
                        }
                        if (extMethod == ReportDefine.SEQUENCE_EXT_METHOD_VERTICAL) {
                            //纵向扩展
                            for (int k = 0; k < queryResultsCollection.size(); k++) {
                                rowState.setDataIndex(k);
                                generateSingleRow(rowState, generator);
                            }
                        }
                        if (extMethod == ReportDefine.SEQUENCE_EXT_METHOD_HORIZONTAL) {
                            //横向扩展，暂未实现
                        }
                    }
                }
            } else {
                generateSingleRow(rowState, generator);
            }
        }

        //</editor-fold>

        //<editor-fold desc="对于数据列，合并同值单元格">

        SequenceMergeCells(root, rowCount, colCount);

        //</editor-fold>

        //</editor-fold>

        Format format = Format.getCompactFormat();
        format.setEncoding("utf-8");//UTF-8编码
        format.setIndent("    ");   //4个字符的缩进
        XMLOutputter out = new XMLOutputter(format);
        strContent = out.outputString(document);

        //</editor-fold>

        return strContent;
    }

    /**
     * 生成单行内容
     *
     * @param rowState
     * @param generator
     */
    public static void generateSingleRow(RowState rowState, ReportGenerator generator) {
        int rowIndex = rowState.getRowIndex();
        int rowHeight = rowState.getRowHeight();
        int colCount = rowState.getColCount();
        int dataIndex = rowState.getDataIndex();
        Map<String, ReportCell> cellMap = rowState.getCellMap();
        List<String> skipCells = rowState.getSkipCells();
        Element tableElement = rowState.getTableElement();
        List<Map<String, QueryResult>> queryResultsCollection = rowState.getQueryResultsCollection();
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
                String cellId = String.format("c_%03d_%03d_%010d", rowIndex, j, dataIndex);
                tableCell.setAttribute("id", cellId);
                tableRow.addContent(tableCell);
            } else {
                Element tableCell = new Element("td");
                String cellId = String.format("c_%03d_%03d_%010d", rowIndex, j, dataIndex);
                tableCell.setAttribute("id", cellId);
                tableCell.setAttribute("rowspan", String.format("%d", cell.getRowSpan()));
                tableCell.setAttribute("colspan", String.format("%d", cell.getColSpan()));
                Element div = null;
                ReportElement reportElement = cell.getElement();
                if (reportElement != null) {

                    //<editor-fold desc="静态文本">

                    if (reportElement instanceof ReportText) {
                        //静态文本
                        div = new Element("div");
                        ReportText reportText = (ReportText) reportElement;
                        div.addContent(reportText.getText());
                    }

                    //</editor-fold>

                    //<editor-fold desc="数据列">

                    if (reportElement instanceof ReportSequence) {
                        //数据列
                        div = new Element("div");
                        ReportSequence reportSequence = (ReportSequence) reportElement;
                        if (queryResultsCollection != null) {
                            int extMethod = reportSequence.getExtMethod();
                            tableCell.setAttribute("data-sequence-ext", String.format("%d", extMethod));
                            if (extMethod == ReportDefine.SEQUENCE_EXT_METHOD_NONE) {
                                //不扩展，将字段值用逗号连接起来
                                String content = "";
                                for (int k = 0; k < queryResultsCollection.size(); k++) {
                                    Map<String, QueryResult> queryResults = queryResultsCollection.get(k);
                                    String dataSetName = reportSequence.getDataSetName();
                                    String fieldName = reportSequence.getDataFieldName();
                                    String fieldKey = String.format("%s.%s", dataSetName, fieldName);
                                    QueryResult queryResult = queryResults.get(fieldKey);
                                    if (queryResult != null) {
                                        content += String.format("%s,", queryResult.getValue());
                                    }
                                }
                                div.addContent(content);
                            }
                            if (extMethod == ReportDefine.SEQUENCE_EXT_METHOD_VERTICAL) {
                                //纵向扩展
                                Map<String, QueryResult> queryResults = queryResultsCollection.get(dataIndex);
                                if (queryResults != null) {
                                    String dataSetName = reportSequence.getDataSetName();
                                    String fieldName = reportSequence.getDataFieldName();
                                    String fieldKey = String.format("%s.%s", dataSetName, fieldName);
                                    QueryResult queryResult = queryResults.get(fieldKey);
                                    if (queryResult != null) {
                                        String content = queryResult.getValue().toString();
                                        div.addContent(content);
                                    }
                                }
                            }
                            if (extMethod == ReportDefine.SEQUENCE_EXT_METHOD_HORIZONTAL) {
                                //横向扩展，暂未实现
                            }
                        }
                        boolean merge = reportSequence.isMerge();
                        tableCell.setAttribute("data-sequence-merge", merge ? "1" : "0");
                    }

                    //</editor-fold>

                    //<editor-fold desc="图片">

                    if (reportElement instanceof ReportImage) {
                        div = new Element("div");
                        ReportImage reportImage = (ReportImage) reportElement;
                        String reportName = generator.getReportName();
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
                        div.addContent(img);
                    }

                    //</editor-fold>

                    //<editor-fold desc="链接">

                    String linkUrl = reportElement.getLinkUrl();
                    if (linkUrl != null && !linkUrl.equals("")) {
                        Element a = new Element("a");
                        a.setAttribute("href", linkUrl);
                        a.addContent(div);
                        div = new Element("div");
                        div.addContent(a);
                    }

                    //</editor-fold>

                }

                int styleIndex = cell.getStyle();
                if (styleIndex >= 0) {
                    tableCell.setAttribute("class", String.format("style-%d", cell.getStyle()));
                }

                if (div != null) {
                    tableCell.addContent(div);
                }

                tableRow.addContent(tableCell);
            }
        }

        if (rowState.getPageIndex() == generator.getPageIndex()) {
            //处于当前页的内容返回给客户端
            tableElement.addContent(tableRow);
        }

        rowState.setPageHeight(rowState.getPageHeight() + rowHeight);
        if (rowState.getPageHeight() > rowState.getMaxPageHeight()) {
            //计算PageIndex与PageCount
            generator.setPageCount(generator.getPageCount() + 1);
            rowState.setPageIndex(rowState.getPageIndex() + 1);
            rowState.setPageHeight(0);
        }
    }

    /**
     * 对于数据列，可能需要合并等值单元格
     *
     * @param table
     * @param rowCount
     * @param colCount
     */
    public static void SequenceMergeCells(Element table, int rowCount, int colCount) {

        //<editor-fold desc="将所有数据列单元格放入一个列表中">

        List<Element> sequenceCells = new ArrayList<>();
        List<Element> rows = table.getChildren("tr");
        for (int i = 0; i < rows.size(); i++) {
            Element row = rows.get(i);
            List<Element> cells = row.getChildren("td");
            for (int j = 0; j < cells.size(); j++) {
                Element cell = cells.get(j);
                String dataExt = cell.getAttributeValue("data-sequence-ext");
                if (dataExt != null) {
                    if (dataExt.equals(String.format("%d", ReportDefine.SEQUENCE_EXT_METHOD_VERTICAL))) {
                        sequenceCells.add(cell);
                    }
                }
            }
        }

        //</editor-fold>

        //<editor-fold desc="同一列，按顺序比较，如果内容相同，则合并">

        for (int i = 0; i < rowCount; i++) {
            int rowIndex = i;
            for (int j = 0; j < colCount; j++) {
                int colIndex = j;
                String preKey = String.format("c_%03d_%03d", rowIndex, colIndex);
                Stream<Element> filterStream = sequenceCells.stream().filter(c -> c.getAttributeValue("id").startsWith(preKey));
                Stream<Element> sortedStream = filterStream
                        .sorted(Comparator.comparing(c -> c.getAttributeValue("id")));
                List<Element> cells = sortedStream.collect(Collectors.toList());
                if (cells.size() > 0) {
                    int rowSize = 0;
                    Element first = null;
                    for (int k = 0; k < cells.size(); k++) {
                        if (k == 0) {
                            first = cells.get(k);
                            rowSize = 1;
                        } else {
                            Element current = cells.get(k);
                            if (first.getValue().equals(current.getValue())
                                    && "1".equals(first.getAttributeValue("data-sequence-merge"))) {
                                System.out.println(String.format("%s", first.getValue()));
                                //内容相同，并且指定合并单元格，进行合并，实际上就是删除当前单元格，然后修改rowspan
                                rowSize++;
                                Parent parent = current.getParent();
                                if (parent != null) {
                                    parent.removeContent(current);
                                }
                                if (first != null) {
                                    first.setAttribute("rowspan", String.format("%d", rowSize));
                                }
                            } else {
                                first = current;
                                rowSize = 1;
                            }
                        }
                    }
                }
            }
        }

        //</editor-fold>

    }

    /**
     * 获取指定行包含的数据列
     *
     * @param rowIndex
     * @param colCount
     * @param cellMap
     * @return
     */
    public static List<ReportSequence> getSequenceCellInRow(int rowIndex, int colCount, Map<String, ReportCell> cellMap) {
        List<ReportSequence> reportSequences = new ArrayList<>();
        List<String> dataSetNames = new ArrayList<>();
        for (int i = 0; i < colCount; i++) {
            String key = String.format("%03d%03d", rowIndex, i);
            ReportCell reportCell = cellMap.get(key);
            if (reportCell == null) {
                continue;
            }
            ReportElement reportElement = reportCell.getElement();
            if (reportElement instanceof ReportSequence) {
                ReportSequence reportSequence = (ReportSequence) reportElement;
                String dataSetName = reportSequence.getDataSetName();
                if (!dataSetNames.contains(dataSetName)) {
                    reportSequences.add(reportSequence);
                    dataSetNames.add(dataSetName);
                }
            }
        }
        return reportSequences;
    }

}
