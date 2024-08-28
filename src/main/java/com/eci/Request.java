package com.eci;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private String path;
    private Map<String, String> queryParams = new HashMap<>();

    public Request(String requestLine) {
        String[] parts = requestLine.split("\\?");
        this.path = parts[0];
        if (parts.length > 1) {
            String[] params = parts[1].split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length > 1) {
                    queryParams.put(keyValue[0], keyValue[1]);
                }
            }
        }
    }

    public String getPath() {
        return path;
    }

    public String getValues(String name, String defaultValue) {
        return queryParams.getOrDefault(name, defaultValue);
    }
}
