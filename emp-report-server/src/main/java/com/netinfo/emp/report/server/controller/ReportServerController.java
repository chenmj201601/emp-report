package com.netinfo.emp.report.server.controller;

import com.netinfo.emp.report.model.ReportDocument;
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

import java.io.File;
import java.io.FileNotFoundException;

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

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String genReport(@RequestParam(value = "file_name", required = false, defaultValue = "") String fileName,
                            Model model) {
        try {
            if (fileName.equals("")) {
                return VIEW_REPORT;
            }
            File file = ResourceUtils.getFile(String.format("reports/%s.rpt", fileName));
            System.out.println(file.getName());
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(file);
            ReportDocument reportDocument = ReportUtil.getReportDocument(document);
            reportDocument.setPath(file.getPath());
            System.out.println(String.format("End %s", reportDocument.getName()));
            String strHtml = HtmlUtil.generateHtml(reportDocument);
            model.addAttribute("content", strHtml);
        } catch (FileNotFoundException ex) {
            System.out.println(String.format("File not exist. %s", fileName));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return VIEW_REPORT;
    }
}
