package com.indrasol.essbase.essbasesheetplugin.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.essbase.api.base.EssException;
import com.essbase.api.metadata.IEssDimension;
import com.indrasol.essbase.essbasesheetplugin.model.*;
import com.indrasol.essbase.essbasesheetplugin.util.EssbaseHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class  LoginService {

    @Autowired
    private EssbaseConnection essbaseConnection;


    public void  login(Credentials credentials)  {
        this.essbaseConnection =  EssbaseHelper.connect(credentials);
    }

    public List<IEssDimension> loadData() {
        String str = "Failed";
        List<IEssDimension> list = new ArrayList<IEssDimension>();
        try {
            if(this.essbaseConnection != null && this.essbaseConnection.getOlapSvr() != null) {
                list = EssbaseHelper.loadData(this.essbaseConnection.getOlapSvr());
                str = "Loaded";
            } else {
                str = "No Connection available";
            }
                
		} catch (EssException e) {
            e.printStackTrace();
            str = e.getMessage();
        }
        return list;
    }

    public List<EApplication> getAllApplications() {
        String str = "Failed";
        List<EApplication> appList = new ArrayList<EApplication>();
        try {
            if(this.essbaseConnection != null && this.essbaseConnection.getOlapSvr() != null) {
                appList = EssbaseHelper.getAllApplications(this.essbaseConnection.getOlapSvr());
                str = "Loaded";
            } else {
                str = "No Connection available";
            }

        } catch (EssException e) {
            e.printStackTrace();
            str = e.getMessage();
        }
        return appList;
    }

    public List<Dimension> getAllDimensions() {
        List<Dimension> dimensionList = new ArrayList<Dimension>();
        try {
            if(this.essbaseConnection != null && this.essbaseConnection.getOlapSvr() != null) {
                dimensionList = EssbaseHelper.getAllDimensions(this.essbaseConnection.getOlapSvr());
            } 
                
		} catch (EssException e) {
            e.printStackTrace();
        }
        return dimensionList;
    }

    public List<EMembers> getAllMembersForDimensions(String dimName) {
        List<EMembers> membersList = new ArrayList<EMembers>();
        try {
            if(this.essbaseConnection != null && this.essbaseConnection.getOlapSvr() != null) {
                membersList = EssbaseHelper.getAllMembersForDimensions(this.essbaseConnection.getOlapSvr(), dimName);
            } 
                
		} catch (EssException e) {
            e.printStackTrace();
        }
        return membersList;

    }

    public void disconnect() {
        if(this.essbaseConnection != null) {
            EssbaseHelper.disconnect(essbaseConnection);
            this.essbaseConnection = new EssbaseConnection();
        }
    }

    public EssbaseConnection getEssbaseConnection() {
        return essbaseConnection;
    }

    public void setEssbaseConnection(EssbaseConnection essbaseConnection) {
        this.essbaseConnection = essbaseConnection;
    }

    

}