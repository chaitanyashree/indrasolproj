package com.indrasol.essbase.essbasesheetplugin.model;

import com.essbase.api.dataquery.IEssCubeView;
import com.essbase.api.datasource.IEssOlapServer;
import com.essbase.api.session.IEssbase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EssbaseConnection {

    private IEssOlapServer olapSvr;
    private IEssbase ess;
    private IEssCubeView cubeView;

    private String apiVersion;
    private String apiVersionDetail;

    @Autowired
    private UserEssBaseOptions userEssBaseOptions;


    @Autowired
    private Credentials credentials;


    public IEssOlapServer getOlapSvr() {
        return this.olapSvr;
    }

    public void setOlapSvr(IEssOlapServer olapSvr) {
        this.olapSvr = olapSvr;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public IEssbase getEss() {
        return ess;
    }

    public void setEss(IEssbase ess) {
        this.ess = ess;
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

    public IEssCubeView getCubeView() {
        return cubeView;
    }

    public void setCubeView(IEssCubeView cubeView) {
        this.cubeView = cubeView;
    }

    public UserEssBaseOptions getUserEssBaseOptions() {
        return userEssBaseOptions;
    }

    public void setUserEssBaseOptions(UserEssBaseOptions userEssBaseOptions) {
        this.userEssBaseOptions = userEssBaseOptions;
    }



    
}