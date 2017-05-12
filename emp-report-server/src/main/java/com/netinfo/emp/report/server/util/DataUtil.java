package com.netinfo.emp.report.server.util;

import com.netinfo.emp.report.model.ReportDataField;
import com.netinfo.emp.report.model.ReportDataSet;
import com.netinfo.emp.report.model.ReportDataTable;
import com.netinfo.emp.report.model.ReportDocument;
import com.netinfo.emp.report.server.entity.DataSetResult;
import com.netinfo.emp.report.server.entity.DataSource;
import com.netinfo.emp.report.server.entity.QueryResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.util
 * <p>
 * Created by Charley on 2017/5/8.
 */
public class DataUtil {

    //查询数据，返回数据列表
    public static List<Map<String, QueryResult>> queryData(ReportDataSet reportDataSet) {
        List<Map<String, QueryResult>> datas = new ArrayList<>();
        if (reportDataSet == null) {
            return datas;
        }

        //<editor-fold desc="构造查询语句">

        List<ReportDataTable> reportDataTables = reportDataSet.getTables();
        List<ReportDataField> reportDataFields = reportDataSet.getFields();
        ReportDataTable reportDataTable = reportDataTables.get(0);
        String strTableName = reportDataTable.getName();
        String strFields = "";
        for (int i = 0; i < reportDataFields.size(); i++) {
            strFields += String.format(" %s,", reportDataFields.get(i).getName());
        }
        if (!strFields.equals("")) {
            strFields = strFields.substring(0, strFields.length() - 1);
        }
        String strSql = String.format("select %s from %s", strFields, strTableName);
        System.out.println(String.format("Sql: %s", strSql));

        //</editor-fold>

        //<editor-fold desc="检索 DataSource">

        String dataSourceName = reportDataSet.getDataSourceName();
        Map<String, DataSource> dataSources = DataSourceUtil.loadDataSources();
        DataSource dataSource = dataSources.get(dataSourceName);
        if (dataSource == null) {
            System.out.println(String.format("DataSource not exist. %s", dataSourceName));
            return datas;
        }

        //</editor-fold>

        //<editor-fold desc="查询数据">

        int dbType = dataSource.getDbType();
        String strUrl = "";
        try {
            if (dbType == 1) {
                Class.forName("com.mysql.jdbc.Driver");
                strUrl = String.format("jdbc:mysql://%s:%d/%s", dataSource.getHost(), dataSource.getPort(), dataSource.getDbName());
            }
            if (dbType == 2) {
                Class.forName("com.microsoft.sqlserver.Driver");
                strUrl = String.format("jdbc:sqlserver://%s:%d/%s", dataSource.getHost(), dataSource.getPort(), dataSource.getDbName());
            }
        } catch (ClassNotFoundException ex) {
            System.out.println(String.format("Driver class not found. %s", ex.getMessage()));
        } catch (Exception ex) {
            System.out.println(String.format("Fail. %s", ex.getMessage()));
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
                Map<String, QueryResult> row = new HashMap<>();
                for (int i = 0; i < reportDataFields.size(); i++) {
                    ReportDataField field = reportDataFields.get(i);
                    String strName = field.getName();
                    QueryResult result = new QueryResult();
                    result.setName(strName);
                    result.setField(field);
                    result.setValue(resultSet.getString(strName));
                    row.put(strName, result);
                }
                datas.add(row);
            }
        } catch (SQLException ex) {
            System.out.println(String.format("Get connection fail. %s", ex.getMessage()));
        } catch (Exception ex) {
            System.out.println(String.format("Fail. %s", ex.getMessage()));
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

    public static Map<String, DataSetResult> queryAllData(ReportDocument reportDocument) {
        Map<String, DataSetResult> dataSetResults = new HashMap<>();
        if(reportDocument==null){
            return dataSetResults;
        }
        List<ReportDataSet> reportDataSets = reportDocument.getDataSets();
        for (int i = 0; i < reportDataSets.size(); i++) {
            ReportDataSet reportDataSet = reportDataSets.get(i);
            String strName = reportDataSet.getName();
            List<Map<String, QueryResult>> datas = queryData(reportDataSet);
            DataSetResult dataSetResult = new DataSetResult();
            dataSetResult.setName(strName);
            dataSetResult.setDataSet(reportDataSet);
            for (int k = 0; k < datas.size(); k++) {
                dataSetResult.getResult().add(datas.get(k));
            }
            System.out.println(String.format("Data count for %s is %d", strName, dataSetResult.getResult().size()));
            dataSetResults.put(strName, dataSetResult);
        }
        return dataSetResults;
    }

}
