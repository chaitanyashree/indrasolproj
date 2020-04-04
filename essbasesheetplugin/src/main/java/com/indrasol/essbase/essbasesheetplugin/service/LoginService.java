package com.indrasol.essbase.essbasesheetplugin.service;

import com.essbase.api.base.EssException;
import com.essbase.api.metadata.IEssDimension;
import com.indrasol.essbase.essbasesheetplugin.model.*;
import com.indrasol.essbase.essbasesheetplugin.util.EssbaseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoginService {

    @Autowired
    private EssbaseConnection essbaseConnection;


    public static final int NEXT_LEVEL = 0;
    public static final int BOTTOM_LEVEL = 1;
    public static final int ALL_LEVEL = 2;


    public void login(Credentials credentials) {
        this.essbaseConnection = EssbaseHelper.connect(credentials);
    }

    public List<IEssDimension> loadData() {
        String str = "Failed";
        List<IEssDimension> list = new ArrayList<IEssDimension>();
        try {
            if (this.essbaseConnection != null && this.essbaseConnection.getOlapSvr() != null) {
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
            if (this.essbaseConnection != null && this.essbaseConnection.getOlapSvr() != null) {
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

    public List<Dimension> getAllDimensions(String applicationName, String cubeName) {
        List<Dimension> dimensionList = new ArrayList<Dimension>();
        try {
            if (this.essbaseConnection != null && this.essbaseConnection.getOlapSvr() != null) {
                dimensionList = EssbaseHelper.getAllDimensions(this.essbaseConnection.getOlapSvr(), applicationName,
                        cubeName);
            }

        } catch (EssException e) {
            e.printStackTrace();
        }
        return dimensionList;
    }

    public DataGrid getDefaultGrid(String applicationName, String cubeName) {
        DataGrid grid = new DataGrid();
        try {
            if (this.essbaseConnection != null && this.essbaseConnection.getOlapSvr() != null) {
                // IEssCubeView cubeView =
                // this.essbaseConnection.getOlapSvr().getApplication(applicationName).getCube(cubeName).openCubeView(this.essbaseConnection.getOlapSvr()+"-"+cubeName);
                grid = EssbaseHelper.getDefaultGrid(this.essbaseConnection.getOlapSvr(), applicationName, cubeName);
                // this.essbaseConnection.setCubeView(cubeView);
            }

        } catch (EssException e) {
            e.printStackTrace();
        }
        return grid;
    }

    public DataGrid getZoomInOperation(String applicationName, String cubeName, DataGrid dataGrid,int startRow, int startColumn, int zoomLevel) {
        DataGrid grid = new DataGrid();
        try {
            if (this.essbaseConnection != null && this.essbaseConnection.getOlapSvr() != null) {
                switch(zoomLevel) {
                    case NEXT_LEVEL:
                        grid = EssbaseHelper.getZoomInOperation(this.essbaseConnection.getOlapSvr(), applicationName, cubeName, dataGrid, startRow, startColumn);
                        break;
                    case BOTTOM_LEVEL:
                        grid = EssbaseHelper.getZoomInOperationBottomLevel(this.essbaseConnection.getOlapSvr(), applicationName, cubeName, dataGrid, startRow, startColumn);
                        break;
                    case ALL_LEVEL:
                        grid = EssbaseHelper.getZoomInOperationAllLevel(this.essbaseConnection.getOlapSvr(), applicationName, cubeName, dataGrid, startRow, startColumn);
                        break;
                    default:
                        grid = EssbaseHelper.getZoomInOperation(this.essbaseConnection.getOlapSvr(), applicationName, cubeName, dataGrid, startRow, startColumn);

                }
                
            }

        }catch(EssException e)
    {
        e.printStackTrace();
    }return grid;
    }

    public DataGrid getZoomOutOperation(String applicationName, String cubeName, DataGrid dataGrid, int startRow,
            int startColumn) {
        DataGrid grid = new DataGrid();
        try {
            if (this.essbaseConnection != null && this.essbaseConnection.getOlapSvr() != null) {
                grid = EssbaseHelper.getZoomOutOperation(this.essbaseConnection.getOlapSvr(), applicationName, cubeName,
                        dataGrid, startRow, startColumn);
            }

        } catch (EssException e) {
            e.printStackTrace();
        }
        return grid;
    }

    public List<EMembers> getAllMembersForDimensions(String dimName) {
        List<EMembers> membersList = new ArrayList<EMembers>();
        try {
            if (this.essbaseConnection != null && this.essbaseConnection.getOlapSvr() != null) {
                membersList = EssbaseHelper.getAllMembersForDimensions(this.essbaseConnection.getOlapSvr(), dimName);
            }

        } catch (EssException e) {
            e.printStackTrace();
        }
        return membersList;

    }

    public void disconnect() {
        if (this.essbaseConnection != null) {
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