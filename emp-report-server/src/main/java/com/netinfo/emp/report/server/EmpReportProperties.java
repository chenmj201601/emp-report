package com.netinfo.emp.report.server;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server
 * <p>
 * Created by Charley on 2017/5/25.
 */
@Component
@ConfigurationProperties("emp.report")
public class EmpReportProperties {

    private String path = "reports";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
