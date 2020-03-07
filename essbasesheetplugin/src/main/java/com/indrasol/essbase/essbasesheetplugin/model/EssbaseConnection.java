package com.indrasol.essbase.essbasesheetplugin.model;

import com.essbase.api.datasource.IEssOlapServer;
import com.essbase.api.session.IEssbase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EssbaseConnection {

    private IEssOlapServer olapSvr;
    private IEssbase ess;


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

    
    
}