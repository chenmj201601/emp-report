package com.netinfo.emp.report.model;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.model
 * <p>
 * Created by Charley on 2017/5/4.
 */
public class VisualStyle {
    private String fontFamily;
    private int fontSize;
    private int fontStyle;
    private String foreground;
    private String background;
    private int width;
    private int height;
    private int horizontalAlignment;
    private int verticalAlignment;
    private ReportBorder border;
    private String key;

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }

    public String getForeground() {
        return foreground;
    }

    public void setForeground(String foreground) {
        this.foreground = foreground;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public int getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(int verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public ReportBorder getBorder() {
        return border;
    }

    public void setBorder(ReportBorder border) {
        border = border;
    }

    public String getKey() {
        key = String.format("%s_%d_%d_%s_%s_%d_%d_%d_%d_%s",
                fontFamily,
                fontSize,
                fontStyle,
                foreground,
                background,
                width,
                height,
                horizontalAlignment,
                verticalAlignment,
                border != null ? border.getKey() : "");
        return key;
    }

}
