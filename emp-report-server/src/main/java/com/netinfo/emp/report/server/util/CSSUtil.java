package com.netinfo.emp.report.server.util;

import com.netinfo.emp.report.model.ReportBorder;
import com.netinfo.emp.report.model.ReportDefine;
import com.netinfo.emp.report.model.ReportDocument;
import com.netinfo.emp.report.model.VisualStyle;
import com.netinfo.emp.report.server.entity.ReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.util
 * <p>
 * Created by Charley on 2017/5/11.
 */
public class CSSUtil {

    private static Logger logger = LoggerFactory.getLogger(CSSUtil.class);

    /**
     * 生成 CSS 文档
     *
     * @param generator
     * @return
     */
    public static String genCSS(ReportGenerator generator) {
        String strCSS = "";
        try {
            if (generator == null) {
                logger.error(String.format("ReportGenerator is null."));
                return strCSS;
            }
            String reportName = generator.getReportName();
            ReportDocument reportDocument = generator.getReportDocument();
            if (reportDocument == null) {
                logger.error(String.format("ReportDocument not exist. %s", reportName));
                return strCSS;
            }
            StringBuilder sb = new StringBuilder();
            List<VisualStyle> styles = reportDocument.getStyles();
            for (int i = 0; i < styles.size(); i++) {
                String strClassName = String.format("style-%d", i);
                sb.append(String.format(".%s{\r\n", strClassName));
                VisualStyle style = styles.get(i);
                String fontFamily = style.getFontFamily();
                if (!fontFamily.equals("")) {
                    sb.append(String.format("font-family:%s;\r\n", fontFamily));
                }
                int fontSize = style.getFontSize();
                if (fontSize > 0) {
                    sb.append(String.format("font-size:%dpx;\r\n", fontSize));
                }
                int fontStyle = style.getFontStyle();
                if ((fontStyle & ReportDefine.FONT_STYLE_BOLD) > 0) {
                    sb.append(String.format("font-weight:bold;\r\n"));
                }
                if ((fontStyle & ReportDefine.FONT_STYLE_ITALIC) > 0) {
                    sb.append(String.format("font-style:italic;\r\n"));
                }
                if ((fontStyle & ReportDefine.FONT_STYLE_UNDERLINED) > 0) {
                    sb.append(String.format("text-decoration:underline;\r\n"));
                }
                int hAlign = style.gethAlign();
                if (hAlign >= 0
                        && hAlign < 3) {
                    if (hAlign == 0) {
                        sb.append(String.format("text-align:left;\r\n"));
                    }
                    if (hAlign == 1) {
                        sb.append(String.format("text-align:center;\r\n"));
                    }
                    if (hAlign == 2) {
                        sb.append(String.format("text-align:right;\r\n"));
                    }
                }
                String foreColor = style.getForeColor();
                if (foreColor != null
                        && !foreColor.equals("")
                        && foreColor.length() > 3) {
                    sb.append(String.format("color:#%s;\r\n", foreColor.substring(3)));
                }
                String backColor = style.getBackColor();
                if (backColor != null
                        && !backColor.equals("")
                        && backColor.length() > 3) {
                    sb.append(String.format("background-color:#%s;\r\n", backColor.substring(3)));
                }
                ReportBorder border = style.getBorder();
                if (border != null) {
                    sb.append(String.format("border-color:#555;\r\n"));
                    sb.append(String.format("border-style:solid;\r\n"));
                    int left = border.getLeft();
                    if (left >= 0) {
                        sb.append(String.format("border-left-width:%dpx;\r\n", left));
                    }
                    int top = border.getTop();
                    if (left >= 0) {
                        sb.append(String.format("border-top-width:%dpx;\r\n", top));
                    }
                    int right = border.getRight();
                    if (left >= 0) {
                        sb.append(String.format("border-right-width:%dpx;\r\n", right));
                    }
                    int bottom = border.getBottom();
                    if (left >= 0) {
                        sb.append(String.format("border-bottom-width:%dpx;\r\n", bottom));
                    }
                }
                sb.append(String.format("}\r\n"));
            }
            strCSS = sb.toString();
        } catch (Exception ex) {
            logger.error(String.format("Fail. %s", ex.getMessage()));
            ex.printStackTrace();
        }
        return strCSS;
    }
}
