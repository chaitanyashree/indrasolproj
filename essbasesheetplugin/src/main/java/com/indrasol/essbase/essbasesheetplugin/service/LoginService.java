package com.indrasol.essbase.essbasesheetplugin.service;

import com.essbase.api.base.EssException;
import com.indrasol.essbase.essbasesheetplugin.model.Credentials;
import com.indrasol.essbase.essbasesheetplugin.model.EssbaseConnection;
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

    public String loadData() {
        String str = "Failed";
        try {
            if(this.essbaseConnection != null && this.essbaseConnection.getOlapSvr() != null) {
                EssbaseHelper.loadData(this.essbaseConnection.getOlapSvr());
                str = "Loaded";
            } else {
                str = "No Connection available";
            }
                
		} catch (EssException e) {
            e.printStackTrace();
            str = e.getMessage();
        }
        return str;
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