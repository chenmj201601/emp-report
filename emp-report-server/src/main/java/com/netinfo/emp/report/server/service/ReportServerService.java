package com.netinfo.emp.report.server.service;

import com.netinfo.emp.common.EncryptionMode;
import com.netinfo.emp.encryptions.SHAEncryption;
import com.netinfo.emp.report.model.ReportDocument;
import com.netinfo.emp.report.server.EmpReportProperties;
import com.netinfo.emp.report.server.entity.ReportContent;
import com.netinfo.emp.report.server.entity.ReportGenerator;
import com.netinfo.emp.report.server.util.CSSUtil;
import com.netinfo.emp.report.server.util.HtmlUtil;
import com.netinfo.emp.report.server.util.ReportUtil;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.service
 * <p>
 * Created by Charley on 2017/6/2.
 */
@Service
public class ReportServerService {
    private static Logger logger = LoggerFactory.getLogger(ReportServerService.class);

    private Map<String, ReportGenerator> reportGenerators = new HashMap<>();
    @Autowired
    private EmpReportProperties reportProperties;


    /**
     * 生成报表页面
     *
     * @param reportName
     * @param session
     * @return
     */
    public ReportGenerator genReport(String reportName, HttpSession session) {
        ReportGenerator generator = null;
        try {
            if ("".equals(reportName)) {
                logger.error(String.format("Report not preferred."));
                return generator;
            }
            //生成ReportKey，ReportKey 由 SessionID，ReportName 和时间戳构成，经哈希计算得到
            String sessionId = session.getId();
            String strKey = String.format("%s%s%s", sessionId, reportName, System.currentTimeMillis());
            strKey = SHAEncryption.encryptString(strKey, EncryptionMode.SHA256_00_HEX_UNICODE);
            logger.info(String.format("ReportKey is %s for report %s", strKey, reportName));
            //创建 ReportGenerator 对象
            generator = new ReportGenerator();
            generator.setKey(strKey);
            generator.setSessionId(sessionId);
            generator.setReportName(reportName);
            //加载数据源信息xml文件
            File dataSourceFile = ResourceUtils.getFile(String.format("file:%s/datasource.xml", reportProperties.getPath()));
            generator.setDataSourceFile(dataSourceFile);
            //加载报表脚本文件
            File reportFile = ResourceUtils.getFile(String.format("file:%s/reports/%s.rpt", reportProperties.getPath(), reportName));
            logger.info(String.format("Report file name is %s.", reportFile.getName()));
            generator.setReportFile(reportFile);
            SAXBuilder builder = new SAXBuilder();
            //解析ReportDocument
            Document document = builder.build(reportFile);
            ReportDocument reportDocument = ReportUtil.getReportDocument(document);
            logger.info(String.format("Load report document end."));
            generator.setReportDocument(reportDocument);
            if (reportDocument != null) {
                reportDocument.setPath(reportFile.getPath());
                generator.setReportTitle(reportDocument.getTitle());
            }
            int pageIndex = 0;
            int pageCount = 1;
            generator.setPageIndex(pageIndex);
            generator.setPageCount(pageCount);
            generator.setDataLoaded(false);
            reportGenerators.put(strKey, generator);
        } catch (Exception ex) {
            logger.error(String.format("GenReport fail. %s", ex.getMessage()));
            ex.printStackTrace();
            return generator;
        }
        return generator;
    }

    /**
     * 生成报表主体内容
     *
     * @param reportKey
     * @param pageIndex
     * @return
     */
    public ReportContent genReportHtml(String reportKey, int pageIndex) {
        logger.info(String.format("Generating html. Key:%s;PageIndex:%d", reportKey, pageIndex));
        ReportGenerator generator = reportGenerators.get(reportKey);
        if (generator == null) {
            logger.error(String.format("ReportGenerator not exist. %s", reportKey));
            return null;
        }
        generator.setPageIndex(pageIndex);
        ReportContent reportContent = new ReportContent();
        reportContent.setKey(reportKey);
        //生成报表主体内容
        String strContent = HtmlUtil.generateHtml(generator);
        int pageCount = generator.getPageCount();
        reportContent.setPageIndex(pageIndex);
        reportContent.setPageCount(pageCount);
        reportContent.setContent(strContent);
        logger.info(String.format("Generate html end. %d / %d", pageIndex, pageCount));
        return reportContent;
    }

    /**
     * 生成样式表
     *
     * @param reportKey
     * @return
     */
    public String genReportCSS(String reportKey) {
        logger.info(String.format("Generating CSS. %s", reportKey));
        ReportGenerator generator = reportGenerators.get(reportKey);
        if (generator == null) {
            logger.error(String.format("ReportGenerator not exist. %s", reportKey));
            return "";
        }
        //生成CSS
        String strContent = CSSUtil.genCSS(generator);
        logger.info(String.format("Generate CSS end."));
        return strContent;
    }

}
