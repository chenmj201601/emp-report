package com.netinfo.emp.report.server.controller;

import com.netinfo.emp.common.EncryptionMode;
import com.netinfo.emp.encryptions.SHAEncryption;
import com.netinfo.emp.report.model.ReportDocument;
import com.netinfo.emp.report.server.entity.ReportGenerator;
import com.netinfo.emp.report.server.util.CSSUtil;
import com.netinfo.emp.report.server.util.HtmlUtil;
import com.netinfo.emp.report.server.util.ReportUtil;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private Map<String, ReportGenerator> reportGenerators = new HashMap<>();

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String genReport(@RequestParam(value = "report_name", required = false, defaultValue = "") String reportName,
                            Model model) {
        try {
            if (reportName.equals("")) {
                System.out.println(String.format("Report not preferred."));
                return VIEW_REPORT;
            }
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            String strNow = format.format(now);
            String strKey = String.format("%s%s", reportName, strNow);
            strKey = SHAEncryption.encryptString(strKey, EncryptionMode.SHA256_00_HEX_UNICODE);
            System.out.println(String.format("ReportKey is %s for report %s", strKey, reportName));
            //创建 ReportGenerator 对象
            ReportGenerator generator = new ReportGenerator();
            generator.setKey(strKey);
            generator.setReportName(reportName);
            //加载报表脚本文件
            File file = ResourceUtils.getFile(String.format("reports/%s.rpt", reportName));
            System.out.println(String.format("Report file name is %s.", file.getName()));
            generator.setReportFile(file);
            SAXBuilder builder = new SAXBuilder();
            //解析ReportDocument
            Document document = builder.build(file);
            ReportDocument reportDocument = ReportUtil.getReportDocument(document);
            reportDocument.setPath(file.getPath());
            generator.setReportDocument(reportDocument);
            reportGenerators.put(strKey, generator);
            model.addAttribute("report_key", strKey);
        } catch (FileNotFoundException ex) {
            System.out.println(String.format("File not exist. %s", reportName));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return VIEW_REPORT;
    }

    @ResponseBody
    @RequestMapping(value = "/html", method = RequestMethod.GET)
    public String genReportHtml(@RequestParam(value = "report_key") String reportKey) {
        ReportGenerator generator = reportGenerators.get(reportKey);
        if (generator == null) {
            System.out.println(String.format("ReportGenerator not exist. %s", reportKey));
            return "";
        }
        String strContent = HtmlUtil.generateHtml(generator);
        return strContent;
    }

    @ResponseBody
    @RequestMapping(value = "/css", method = RequestMethod.GET)
    public String genReportCSS(@RequestParam(value = "report_key") String reportKey) {
        ReportGenerator generator = reportGenerators.get(reportKey);
        if (generator == null) {
            System.out.println(String.format("ReportGenerator not exist. %s", reportKey));
            return "";
        }
        String strContent = CSSUtil.genCSS(generator);
        return strContent;
    }

}
