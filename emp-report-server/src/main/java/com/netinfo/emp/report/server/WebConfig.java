package com.netinfo.emp.report.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server
 * <p>
 * Created by Charley on 2017/5/25.
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private EmpReportProperties reportProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/reports/**").addResourceLocations(String.format("file:%s", reportProperties.getPath()));
        super.addResourceHandlers(registry);
    }
}
