package com.netinfo.emp.report.server.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Project emp-report
 * Package com.netinfo.emp.report.server.entity
 * <p>
 * Created by Charley on 2017/6/18.
 */
public class DataRecord {
    private String id;
    private Map<String, QueryResult> results = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, QueryResult> getResults() {
        return results;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("ID:%s;", id));
        for (Map.Entry<String, QueryResult> entry : results.entrySet()) {
            QueryResult queryResult = entry.getValue();
            sb.append(String.format("%s:%s;", queryResult.getKey(), queryResult.getValue()));
        }
        return sb.toString();
    }
}
