package com.netinfo.emp.report.server.entity;

/**
 * 数据源信息
 * <p>
 * Project emp-report
 * Package com.netinfo.emp.report.server.entity
 * <p>
 * Created by Charley on 2017/5/9.
 */
public class DataSource {
    private String name;
    private int dbType;
    private String host;
    private int port;
    private String dbName;
    private String loginName;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDbType() {
        return dbType;
    }

    public void setDbType(int dbType) {
        this.dbType = dbType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
