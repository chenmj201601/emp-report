package com.netinfo.emp;

import com.netinfo.emp.report.model.ReportDocument;
import com.netinfo.emp.report.server.entity.DataSetResult;
import com.netinfo.emp.report.server.util.DataUtil;
import com.netinfo.emp.report.server.util.ReportUtil;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
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
        Map<String, DataSetResult> dataSetResults = DataUtil.queryAllData(reportDocument);
        System.out.println(String.format("End %d", dataSetResults.size()));
    }
}
