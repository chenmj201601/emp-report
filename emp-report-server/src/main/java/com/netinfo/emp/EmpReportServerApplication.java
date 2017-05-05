package com.netinfo.emp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
public class EmpReportServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmpReportServerApplication.class, args);
    }

    @RequestMapping("/")
    @ResponseBody
    public String hello() {
        return String.format("Welcome eMP Report Server !");
    }
}
