package com.netinfo.emp;

import com.netinfo.emp.report.model.ReportDataField;
import com.netinfo.emp.report.model.ReportDocument;
import com.netinfo.emp.report.server.entity.DataSetResult;
import com.netinfo.emp.report.server.entity.QueryResult;
import com.netinfo.emp.report.server.entity.ReportGenerator;
import com.netinfo.emp.report.server.util.DataUtil;
import com.netinfo.emp.report.server.util.ReportUtil;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Project emp-report
 * Package com.netinfo.emp
 * <p>
 * Created by Charley on 2017/5/9.
 */
public class DataUtilTest {
    @Test
    public void testQueryData() throws IOException, JDOMException {
        File file = ResourceUtils.getFile(String.format("classpath:user.rpt"));
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(file);
        ReportDocument reportDocument = ReportUtil.getReportDocument(document);
        ReportGenerator generator = new ReportGenerator();
        generator.setReportDocument(reportDocument);
        Map<String, DataSetResult> dataSetResults = DataUtil.queryAllData(generator);
        System.out.println(String.format("End %d", dataSetResults.size()));
    }

    @Test
    public void testSql() {
        //<editor-fold desc="查询数据">

        String strUrl = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            strUrl = String.format("jdbc:mysql://%s:%d/%s", "172.16.10.71", 3306, "charley0504");
        } catch (ClassNotFoundException ex) {
            //logger.error(String.format("Driver class not found. %s", ex.getMessage()));
        } catch (Exception ex) {
            //logger.error(String.format("Fail. %s", ex.getMessage()));
            ex.printStackTrace();
        }
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            String strUser = "netinfo";
            String strPassword = "netinfo,123";
            con = DriverManager.getConnection(strUrl, strUser, strPassword);
            pstmt = con.prepareStatement("SELECT  product.id AS product_id, product.name AS product_name, product.price AS product_price, region.group AS region_group, region.id AS region_id, region.name AS region_name, sale.date AS sale_date, sale.id AS sale_id, sale.num AS sale_num, sale.price AS sale_price, sale.prod_id AS sale_prod_id, sale.region_id AS sale_region_id FROM  product, region, sale WHERE   sale.prod_id = product.id AND sale.region_id = region.id");
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                Map<String, QueryResult> row = new HashMap<>();
               /* for (int i = 0; i < reportDataFields.size(); i++) {
                    ReportDataField field = reportDataFields.get(i);
                    String strName = field.getName();
                    String strKey = String.format("%s.%s", dataSetName, strName);
                    QueryResult result = new QueryResult();
                    result.setKey(strKey);
                    result.setField(field);
                    result.setValue(resultSet.getString(strName));
                    row.put(strKey, result);
                }
                datas.add(row);*/
            }
        } catch (SQLException ex) {
            //logger.error(String.format("Get connection fail. %s", ex.getMessage()));
        } catch (Exception ex) {
            //logger.error(String.format("Fail. %s", ex.getMessage()));
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
    }
}
