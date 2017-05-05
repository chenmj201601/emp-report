package com.netinfo.emp.report.server.util;

import com.netinfo.emp.report.model.*;
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
    public static String generateHtml(ReportDocument reportDocument) {
        String strContent = "";
        ReportGrid grid = reportDocument.getGrid();
        if (grid == null) {
            return strContent;
        }

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
        for (int i = 0; i < colCount; i++) {
            //设定列的宽度
            Element col = new Element("col");
            col.setAttribute("width", listWidths.get(i).toString());
            root.addContent(col);
        }
        List<String> listSkipCells = new ArrayList<>();
        for (int i = 0; i < reportDocument.getCells().size(); i++) {
            //考虑合并单元格的情况，如果有单元格跨了多行或多列，
            //将被跨的行或列的索引记录下来，后面生成每个单元格标签的时候需要跳过
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
        //按行，按列依次生成单元格，注意，listSkipCells 中的需要跳过
        for (int i = 0; i < rowCount; i++) {
            int height = listHeights.get(i);
            Element tableRow = new Element("tr");
            tableRow.setAttribute("style", String.format("height:%dpx;", height));
            for (int j = 0; j < colCount; j++) {
                String key = String.format("%03d%03d", i, j);
                if (listSkipCells.contains(key)) {
                    continue;
                }
                ReportCell cell = cellMap.get(key);
                if (cell == null) {
                    Element tableCell = new Element("td");
                    tableRow.addContent(tableCell);
                } else {
                    Element tableCell = new Element("td");
                    tableCell.setAttribute("rowspan", String.format("%d", cell.getRowSpan()));
                    tableCell.setAttribute("colspan", String.format("%d", cell.getColSpan()));
                    String strStyle;
                    VisualStyle style = null;
                    if (cell.getElement() != null) {
                        ReportElement reportElement = cell.getElement();
                        Element div = new Element("div");

                        //<editor-fold desc="单元格内容样式">

                        int styleIndex = reportElement.getStyle();
                        if (styleIndex >= 0
                                && styleIndex < reportDocument.getStyles().size()) {
                            style = reportDocument.getStyles().get(styleIndex);
                            strStyle = "";
                            strStyle += String.format("font-family:%s;", style.getFontFamily());
                            strStyle += String.format("font-size:%dpx;", style.getFontSize());
                            if (style.getFontStyle() > 0) {
                                if ((style.getFontStyle() & ReportDefine.FONT_STYLE_BOLD) > 0) {
                                    strStyle += String.format("font-weight:bold;");
                                }
                                if ((style.getFontStyle() & ReportDefine.FONT_STYLE_ITALIC) > 0) {
                                    strStyle += String.format("font-style:italic;");
                                }
                                if ((style.getFontStyle() & ReportDefine.FONT_STYLE_UNDERLINED) > 0) {
                                    strStyle += String.format("text-decoration:underline;");
                                }
                            }
                            if (style.getHorizontalAlignment() >= 0
                                    && style.getHorizontalAlignment() < 3) {
                                if (style.getHorizontalAlignment() == 0) {
                                    strStyle += String.format("text-align:left;");
                                }
                                if (style.getHorizontalAlignment() == 1) {
                                    strStyle += String.format("text-align:center;");
                                }
                                if (style.getHorizontalAlignment() == 2) {
                                    strStyle += String.format("text-align:right;");
                                }
                            }
                            String foreColor = style.getForeground();
                            if (foreColor != null
                                    && !foreColor.equals("")
                                    && foreColor.length() > 3) {
                                strStyle += String.format("color:#%s;", foreColor.substring(3));
                            }
                            div.setAttribute("style", strStyle);
                        }

                        //</editor-fold>

                        if (reportElement instanceof ReportText) {
                            ReportText reportText = (ReportText) reportElement;
                            div.addContent(reportText.getText());
                        }
                        if (reportElement instanceof ReportSequence) {
                            ReportSequence reportSequence = (ReportSequence) reportElement;
                            div.addContent(reportSequence.getExpression());
                        }

                        tableCell.addContent(div);
                    }

                    //<editor-fold desc="单元格边框及样式">

                    strStyle = "";
                    ReportBorder border = cell.getBorder();
                    if (border != null) {
                        strStyle += String.format("border-style:solid;border-width:0px;");
                        if (border.getLeft() > 0) {
                            strStyle += String.format("border-left-width:%dpx;", border.getLeft());
                        }
                        if (border.getTop() > 0) {
                            strStyle += String.format("border-top-width:%dpx;", border.getTop());
                        }
                        if (border.getRight() > 0) {
                            strStyle += String.format("border-right-width:%dpx;", border.getRight());
                        }
                        if (border.getBottom() > 0) {
                            strStyle += String.format("border-bottom-width:%dpx;", border.getBottom());
                        }
                    }
                    if (style != null) {
                        String fillColor = style.getBackground();
                        if (fillColor != null
                                && !fillColor.equals("")
                                && fillColor.length() > 3) {
                            strStyle += String.format("background-color:#%s;", fillColor.substring(3));
                        }
                    }
                    if (!strStyle.equals("")) {
                        tableCell.setAttribute("style", strStyle);
                    }

                    //</editor-fold>

                    tableRow.addContent(tableCell);
                }
            }
            root.addContent(tableRow);
        }

        //</editor-fold>


        Format format = Format.getCompactFormat();
        format.setEncoding("utf-8");//UTF-8编码
        format.setIndent("    ");   //4个字符的缩进
        XMLOutputter out = new XMLOutputter(format);
        strContent = out.outputString(document);

        return strContent;
    }
}
