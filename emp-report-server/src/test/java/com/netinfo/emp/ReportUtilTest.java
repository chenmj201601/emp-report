package com.netinfo.emp;

import com.netinfo.emp.report.model.ReportDocument;
import com.netinfo.emp.report.server.util.ReportUtil;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

/**
 * Project emp-report
 * Package com.netinfo.emp
 * <p>
 * Created by Charley on 2017/5/4.
 */
@SpringBootTest
public class ReportUtilTest {

    @Test
    public void testGetReportDocument()
            throws IOException, JDOMException {
        File file = ResourceUtils.getFile(String.format("classpath:user.rpt"));
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(file);
        ReportDocument reportDocument = ReportUtil.getReportDocument(document);
        Assert.assertNotNull(reportDocument);
        System.out.println(String.format("End %s", reportDocument.getName()));
    }
}
