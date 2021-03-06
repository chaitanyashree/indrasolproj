package com.indrasol.essbase.essbasesheetplugin.model;

import org.springframework.stereotype.Component;

@Component
public class Credentials {
    private String userName;
    private String password;
    private String olapServerName;
    private String url;

    private String apiVersion;
    private String apiVersionDetail;

    public Credentials() {
    }

    public Credentials(String userName, String password, String olapServerName, String url) {
        this.userName = userName;
        this.password = password;
        this.olapServerName = olapServerName;
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOlapServerName() {
        return olapServerName;
    }

    public void setOlapServerName(String olapServerName) {
        this.olapServerName = olapServerName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getApiVersionDetail() {
        return apiVersionDetail;
    }

    public void setApiVersionDetail(String apiVersionDetail) {
        this.apiVersionDetail = apiVersionDetail;
    }

    
}