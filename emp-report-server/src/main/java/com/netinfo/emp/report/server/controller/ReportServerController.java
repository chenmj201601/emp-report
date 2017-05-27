package com.netinfo.emp.report.server.controller;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.controller
 * <p>
 * Created by Charley on 2017/5/4.
 */
@Controller
@RequestMapping("/report_server")
public class ReportServerController {
    private static final String VIEW_REPORT = "report";

    private static Logger logger = LoggerFactory.getLogger(ReportServerController.class);

    private Map<String, ReportGenerator> reportGenerators = new HashMap<>();
    @Autowired
    private EmpReportProperties reportProperties;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String genReport(@RequestParam(value = "report_name", required = false, defaultValue = "") String reportName,
                            Model model,
                            HttpSession session) {
        try {
            if (reportName.equals("")) {
                logger.error(String.format("Report not preferred."));
                return VIEW_REPORT;
            }
            //生成ReportKey，ReportKey 由 SessionID，ReportName 和时间戳构成，经哈希计算得到
            String sessionId = session.getId();
            String strKey = String.format("%s%s%s", sessionId, reportName, System.currentTimeMillis());
            strKey = SHAEncryption.encryptString(strKey, EncryptionMode.SHA256_00_HEX_UNICODE);
            logger.info(String.format("ReportKey is %s for report %s", strKey, reportName));
            //创建 ReportGenerator 对象
            ReportGenerator generator = new ReportGenerator();
            generator.setKey(strKey);
            generator.setSessionId(sessionId);
            generator.setReportName(reportName);
            //加载报表脚本文件
            File file = ResourceUtils.getFile(String.format("file:%s/%s.rpt", reportProperties.getPath(), reportName));
            logger.info(String.format("Report file name is %s.", file.getName()));
            generator.setReportFile(file);
            SAXBuilder builder = new SAXBuilder();
            //解析ReportDocument
            Document document = builder.build(file);
            ReportDocument reportDocument = ReportUtil.getReportDocument(document);
            reportDocument.setPath(file.getPath());
            generator.setReportDocument(reportDocument);
            logger.info(String.format("Load report document end."));
            reportGenerators.put(strKey, generator);
            model.addAttribute("report_key", strKey);
            String strTitle = reportDocument.getTitle();
            model.addAttribute("report_title", strTitle);
            int pageIndex = 0;
            int pageCount = 1;
            generator.setPageIndex(pageIndex);
            generator.setPageCount(pageCount);
            generator.setDataLoaded(false);
            model.addAttribute("page_index", pageIndex);
            model.addAttribute("page_count", pageCount);
        } catch (FileNotFoundException ex) {
            logger.error(String.format("File not exist. %s %s", reportName, ex.getMessage()));
        } catch (Exception ex) {
            logger.error(String.format("Fail. %s", ex.getMessage()));
            ex.printStackTrace();
        }
        return VIEW_REPORT;
    }

    @ResponseBody
    @RequestMapping(value = "/html", method = RequestMethod.GET)
    public ReportContent genReportHtml(@RequestParam(value = "report_key") String reportKey,
                                       @RequestParam(value = "page_index", required = false, defaultValue = "0") int pageIndex) {
        logger.info(String.format("Generating html Key:%s;PageIndex:%d", reportKey, pageIndex));
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

    @ResponseBody
    @RequestMapping(value = "/css", method = RequestMethod.GET)
    public String genReportCSS(@RequestParam(value = "report_key") String reportKey) {
        logger.info(String.format("Generating CSS %s", reportKey));
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
