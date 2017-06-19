package com.netinfo.emp.report.server.util;

import com.netinfo.emp.report.model.ReportDataField;
import com.netinfo.emp.report.model.ReportDataSet;
import com.netinfo.emp.report.model.ReportDataTable;
import com.netinfo.emp.report.model.ReportDocument;
import com.netinfo.emp.report.server.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.util
 * <p>
 * Created by Charley on 2017/5/8.
 */
public class DataUtil {

    private static Logger logger = LoggerFactory.getLogger(DataUtil.class);

    /**
     * 查询数据，返回数据列表
     *
     * @param reportDataSet
     * @param generator
     * @return
     */
    public static List<DataRecord> queryData(ReportDataSet reportDataSet, ReportGenerator generator) {
        List<DataRecord> datas = new ArrayList<>();
        if (reportDataSet == null) {
            return datas;
        }
        String dataSetName = reportDataSet.getName();

        //<editor-fold desc="构造查询语句">

        List<ReportDataTable> reportDataTables = reportDataSet.getTables();
        List<ReportDataField> reportDataFields = reportDataSet.getFields();
        String strSql = reportDataSet.getSql();     //设计器已经将Sql语句构造好，这里直接拿来用即可，无需构造
        logger.info(String.format("Sql: %s", strSql));

        //</editor-fold>

        //<editor-fold desc="检索 DataSource">

        String dataSourceName = reportDataSet.getDataSourceName();
        Map<String, DataSource> dataSources = DataSourceUtil.loadDataSources(generator);
        DataSource dataSource = dataSources.get(dataSourceName);
        if (dataSource == null) {
            logger.error(String.format("DataSource not exist. %s", dataSourceName));
            return datas;
        }

        //</editor-fold>

        //<editor-fold desc="查询数据">

        int dbType = dataSource.getDbType();
        String strUrl = "";
        try {
            if (dbType == 1) {
                Class.forName("com.mysql.jdbc.Driver");
                strUrl = String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=gbk", dataSource.getHost(), dataSource.getPort(), dataSource.getDbName());
            }
            if (dbType == 2) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                strUrl = String.format("jdbc:sqlserver://%s:%d;DatabaseName=%s", dataSource.getHost(), dataSource.getPort(), dataSource.getDbName());
            }
        } catch (ClassNotFoundException ex) {
            logger.error(String.format("Driver class not found. %s", ex.getMessage()));
        } catch (Exception ex) {
            logger.error(String.format("Fail. %s", ex.getMessage()));
            ex.printStackTrace();
        }
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            String strUser = dataSource.getLoginName();
            String strPassword = dataSource.getPassword();
            con = DriverManager.getConnection(strUrl, strUser, strPassword);
            pstmt = con.prepareStatement(strSql);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                DataRecord dataRecord = new DataRecord();
                dataRecord.setId(UUID.randomUUID().toString());
                for (int i = 0; i < reportDataFields.size(); i++) {
                    ReportDataField field = reportDataFields.get(i);
                    String strName = field.getName();
                    String strKey = String.format("%s.%s", dataSetName, strName);
                    QueryResult result = new QueryResult();
                    result.setKey(strKey);
                    result.setField(field);
                    result.setValue(resultSet.getString(strName));
                    dataRecord.getResults().put(strKey, result);
                }
                datas.add(dataRecord);
            }
        } catch (SQLException ex) {
            logger.error(String.format("Get connection fail. %s", ex.getMessage()));
        } catch (Exception ex) {
            logger.error(String.format("Fail. %s", ex.getMessage()));
            ex.printStackTrace();
        } finally {

            //<editor-fold desc="关闭 JDBC 对象">

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            //</editor-fold>

        }

        //</editor-fold>

        return datas;
    }

    /**
     * 查询所有数据集的数据
     *
     * @param generator
     * @return
     */
    public static Map<String, DataSetResult> queryAllData(ReportGenerator generator) {
        if (generator == null) {
            return null;
        }
        Map<String, DataSetResult> dataSetResults = generator.getDataSetResults();
        dataSetResults.clear();
        ReportDocument reportDocument = generator.getReportDocument();
        if (reportDocument == null) {
            return dataSetResults;
        }
        List<ReportDataSet> reportDataSets = reportDocument.getDataSets();
        for (int i = 0; i < reportDataSets.size(); i++) {
            ReportDataSet reportDataSet = reportDataSets.get(i);
            String strName = reportDataSet.getName();
            List<DataRecord> datas = queryData(reportDataSet, generator);
            DataSetResult dataSetResult = new DataSetResult();
            dataSetResult.setName(strName);
            dataSetResult.setDataSet(reportDataSet);
            for (int k = 0; k < datas.size(); k++) {
                dataSetResult.getResult().add(datas.get(k));
            }
            logger.info(String.format("Data count for %s is %d", strName, dataSetResult.getResult().size()));
            dataSetResults.put(strName, dataSetResult);
        }
        return dataSetResults;
    }

    /**
     * 数据分组
     *
     * @param fieldName
     * @param datas
     * @return
     */
    public static Map<String, GroupResult> filterFieldData(String fieldName, List<DataRecord> datas) {
        Map<String, GroupResult> resultMap = new HashMap<>();
        if (datas == null) {
            return resultMap;
        }
        for (int i = 0; i < datas.size(); i++) {
            DataRecord data = datas.get(i);
            QueryResult fieldResult = data.getResults().get(fieldName);
            if (fieldResult == null) {
                continue;
            }
            String strValue = fieldResult.getValue().toString();
            GroupResult result = resultMap.get(strValue);
            if (result == null) {
                result = new GroupResult();
                result.setFieldName(fieldName);
                result.setGroupValue(strValue);
                resultMap.put(strValue, result);
            }
            result.getResults().add(data);
        }
        return resultMap;
    }

}
