package com.indrasol.essbase.essbasesheetplugin.model;

import java.util.List;

public class EApplication {
    private String applicationName;
    private List<String> cubeNames;

    public EApplication() {
    }

    public EApplication(String applicationName, List<String> cubeNames) {
        this.applicationName = applicationName;
        this.cubeNames = cubeNames;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public List<String> getCubeNames() {
        return cubeNames;
    }

    public void setCubeNames(List<String> cubeNames) {
        this.cubeNames = cubeNames;
    }
}
