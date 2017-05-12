package com.netinfo.emp;

import com.netinfo.emp.report.server.entity.DataSource;
import com.netinfo.emp.report.server.util.DataSourceUtil;
import org.junit.Test;

import java.util.Map;

/**
 * Project emp-report
 * Package com.netinfo.emp
 * <p>
 * Created by Charley on 2017/5/9.
 */
public class DataSourceUtilTest {
    @Test
    public void testLoadDataSources() {
        Map<String, DataSource> dataSources = DataSourceUtil.loadDataSources();
        System.out.println(String.format("Count:%d", dataSources.size()));
    }
}
