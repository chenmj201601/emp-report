package com.netinfo.emp.report.model;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.model
 * <p>
 * Created by Charley on 2017/5/4.
 */
public class ReportImage extends ReportElement {
    private String id;
    private int width;
    private int height;
    private int stretch;
    private int extension;
    private String alt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getStretch() {
        return stretch;
    }

    public void setStretch(int stretch) {
        this.stretch = stretch;
    }

    public int getExtension() {
        return extension;
    }

    public void setExtension(int extension) {
        this.extension = extension;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getExt() {
        String ext = ".png";
        if (extension == ReportDefine.IMAGE_EXTENSION_PNG) {
            ext = ".png";
        }
        if (extension == ReportDefine.IMAGE_EXTENSION_BMP) {
            ext = ".bmp";
        }
        if (extension == ReportDefine.IMAGE_EXTENSION_JPG) {
            ext = ".jpg";
        }
        if (extension == ReportDefine.IMAGE_EXTENSION_JPEG) {
            ext = ".jpeg";
        }
        return ext;
    }
}
