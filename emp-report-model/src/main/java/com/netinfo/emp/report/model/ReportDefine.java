package com.netinfo.emp.report.model;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.model
 * <p>
 * Created by Charley on 2017/5/4.
 */
public class ReportDefine {

    //<editor-fold desc="DBDataType">

    public static final int DB_DATA_TYPE_UNKOWN = 0;
    public static final int DB_DATA_TYPE_SHORT = 1;
    public static final int DB_DATA_TYPE_INT = 2;
    public static final int DB_DATA_TYPE_LONG = 3;
    public static final int DB_DATA_TYPE_NUMBER = 4;
    public static final int DB_DATA_TYPE_CHAR = 11;
    public static final int DB_DATA_TYPE_NCHAR = 12;
    public static final int DB_DATA_TYPE_VARCHAR = 13;
    public static final int DB_DATA_TYPE_NVARCHAR = 14;
    public static final int DB_DATA_TYPE_DATE = 21;

    //</editor-fold>

    //<editor-fold desc="FontStyle">

    public static final int FONT_STYLE_NONE = 0;
    public static final int FONT_STYLE_BOLD = 1;
    public static final int FONT_STYLE_ITALIC = 2;
    public static final int FONT_STYLE_UNDERLINED = 4;

    //</editor-fold>

    //<editor-fold desc="Other">

    public static final int ENLARGE = 10000;

    //</editor-fold>

    //<editor-fold desc="单元格扩展方式">

    public static final int CELL_EXT_METHOD_NONE = 0;
    public static final int CELL_EXT_METHOD_VERTICAL = 1;
    public static final int CELL_EXT_METHOD_HORIZONTAL = 2;

    //</editor-fold>

    //<editor-fold desc="单元格数据格式">

    public static final int CELL_FORMAT_TYPE_NONE = 0;
    public static final int CELL_FORMAT_TYPE_NUMERIC = 1;
    public static final int CELL_FORMAT_TYPE_TEXT = 10;

    //</editor-fold>

    //<editor-fold desc="数据操作方式">

    public static final int DATA_OPT_METHOD_GROUP = 0;
    public static final int DATA_OPT_METHOD_LIST = 1;
    public static final int DATA_OPT_METHOD_COLLECT = 2;

    //</editor-fold>

    //<editor-fold desc="数据分组模式">

    public static final int SEQUENCE_GROUP_MODE_TRANDITIONAL = 0;
    public static final int SEQUENCE_GROUP_MODE_ADJACENT_CONTINUE = 1;

    //</editor-fold>

    //<editor-fold desc="数据汇总模式">
    public static final int SEQUENCE_COLLECT_MODE_SUM = 0;
    public static final int SEQUENCE_COLLECT_MODE_AVG = 1;
    public static final int SEQUENCE_COLLECT_MODE_MAX = 2;
    public static final int SEQUENCE_COLLECT_MODE_MIN = 3;
    //</editor-fold>

    //<editor-fold desc="图片文件格式">

    public static final int IMAGE_EXTENSION_PNG = 1;
    public static final int IMAGE_EXTENSION_BMP = 2;
    public static final int IMAGE_EXTENSION_JPG = 3;
    public static final int IMAGE_EXTENSION_JPEG = 4;

    //</editor-fold>

    //<editor-fold desc="图片拉伸模式">

    public static final int IMAGE_STRETCH_NONE = 0;
    public static final int IMAGE_STRETCH_FILL = 1;
    public static final int IMAGE_STRETCH_UNIFORM = 2;
    public static final int IMAGE_STRETCH_UNIFORM_TO_FILL = 3;

    //</editor-fold>

    //<editor-fold desc="JudeType">

    public static final int JUDGE_EQUAL = 0;
    public static final int JUDGE_LARGER = 1;
    public static final int JUDGE_LOWER = 2;

    //</editor-fold>

    //<editor-fold desc="RelationType">

    public static final int RELATION_AND = 0;
    public static final int RELATION_OR = 1;

    //</editor-fold>

    //<editor-fold desc="OrderDirection">

    public static final int ORDER_DIRECTION_ASC = 0;
    public static final int ORDER_DIRECTION_DESC = 1;

    //</editor-fold>

}
