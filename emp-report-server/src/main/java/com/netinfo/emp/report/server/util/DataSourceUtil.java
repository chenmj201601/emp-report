package com.netinfo.emp.report.server.util;

import com.netinfo.emp.report.server.entity.DataSource;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.util
 * <p>
 * Created by Charley on 2017/5/9.
 */
public class DataSourceUtil {

    private static Logger logger = LoggerFactory.getLogger(DataSourceUtil.class);

    /**
     * 加载所有数据源信息
     *
     * @return
     */
    public static Map<String, DataSource> loadDataSources() {
        Map<String, DataSource> mapDataSources = new HashMap<>();
        try {
            File file = ResourceUtils.getFile("datasource.xml");
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(file);
            Element root = document.getRootElement();
            if (root == null) {
                return mapDataSources;
            }
            Element dataSourcesElement = root.getChild("DataSources");
            if (dataSourcesElement != null) {
                List<Element> dataSources = dataSourcesElement.getChildren("DataSource");
                if (dataSources.size() > 0) {
                    for (int i = 0; i < dataSources.size(); i++) {
                        Element dataSourceElement = dataSources.get(i);
                        String strName = dataSourceElement.getAttributeValue("Name");
                        Element dbInfoElement = dataSourceElement.getChild("DBInfo");
                        if (dbInfoElement != null) {
                            DataSource dataSource = new DataSource();
                            dataSource.setName(strName);
                            dataSource.setDbType(Integer.parseInt(dbInfoElement.getAttributeValue("TypeID")));
                            dataSource.setHost(dbInfoElement.getAttributeValue("Host"));
                            dataSource.setPort(Integer.parseInt(dbInfoElement.getAttributeValue("Port")));
                            dataSource.setDbName(dbInfoElement.getAttributeValue("DBName"));
                            dataSource.setLoginName(dbInfoElement.getAttributeValue("LoginName"));
                            dataSource.setPassword(dbInfoElement.getAttributeValue("Password"));
                            mapDataSources.put(strName, dataSource);
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            logger.error(String.format("DataSource config file not exist."));
            ex.printStackTrace();
        } catch (Exception ex) {
            logger.error(String.format("Fail. %s", ex.getMessage()));
            ex.printStackTrace();
        }
        return mapDataSources;
    }
}
