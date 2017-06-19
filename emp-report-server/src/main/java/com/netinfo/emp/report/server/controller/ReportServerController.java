package com.netinfo.emp.report.server.controller;

import com.netinfo.emp.report.server.entity.ReportContent;
import com.netinfo.emp.report.server.entity.ReportGenerator;
import com.netinfo.emp.report.server.service.ReportServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

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

    @Autowired
    private ReportServerService reportServerService;


    @RequestMapping(value = "", method = RequestMethod.GET)
    public String genReport(@RequestParam(value = "report_name", required = false, defaultValue = "") String reportName,
                            Model model,
                            HttpSession session) {
        ReportGenerator generator = reportServerService.genReport(reportName, session);
        if (generator == null) {
            logger.error(String.format("Report generator is null."));
            return VIEW_REPORT;
        }
        model.addAttribute("report_key", generator.getKey());
        model.addAttribute("report_title", generator.getReportTitle());
        model.addAttribute("page_index", generator.getPageIndex());
        model.addAttribute("page_count", generator.getPageCount());
        return VIEW_REPORT;
    }

    @ResponseBody
    @RequestMapping(value = "/html", method = RequestMethod.GET)
    public ReportContent genReportHtml(@RequestParam(value = "report_key") String reportKey,
                                       @RequestParam(value = "page_index", required = false, defaultValue = "0") int pageIndex) {
        return reportServerService.genReportHtml(reportKey, pageIndex);
    }

    @ResponseBody
    @RequestMapping(value = "/css", method = RequestMethod.GET)
    public String genReportCSS(@RequestParam(value = "report_key") String reportKey) {
        return reportServerService.genReportCSS(reportKey);
    }

}
