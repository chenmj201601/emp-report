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
    private String foreColor;
    private String backColor;
    private int width;
    private int height;
    private int hAlign;
    private int vAlign;
    private ReportBorder border;
    private ReportPadding padding;
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

    public String getForeColor() {
        return foreColor;
    }

    public void setForeColor(String foreground) {
        this.foreColor = foreground;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String background) {
        this.backColor = background;
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

    public int gethAlign() {
        return hAlign;
    }

    public void sethAlign(int horizontalAlignment) {
        this.hAlign = horizontalAlignment;
    }

    public int getvAlign() {
        return vAlign;
    }

    public void setvAlign(int verticalAlignment) {
        this.vAlign = verticalAlignment;
    }

    public ReportBorder getBorder() {
        return border;
    }

    public void setBorder(ReportBorder border) {
        this.border = border;
    }

    public ReportPadding getPadding() {
        return padding;
    }

    public void setPadding(ReportPadding padding) {
        this.padding = padding;
    }

    public String getKey() {
        key = String.format("%s_%d_%d_%s_%s_%d_%d_%d_%d_%s_%s",
                fontFamily,
                fontSize,
                fontStyle,
                foreColor,
                backColor,
                width,
                height,
                hAlign,
                vAlign,
                border != null ? border.getKey() : "",
                padding != null ? padding.getKey() : "");
        return key;
    }

}
