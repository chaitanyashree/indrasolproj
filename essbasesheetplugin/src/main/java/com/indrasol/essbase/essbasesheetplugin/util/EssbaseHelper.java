package com.indrasol.essbase.essbasesheetplugin.util;

import java.util.ArrayList;
import java.util.List;

import com.essbase.api.base.EssException;
import com.essbase.api.dataquery.IEssCubeView;
import com.essbase.api.datasource.IEssCube;
import com.essbase.api.datasource.IEssOlapFileObject;
import com.essbase.api.datasource.IEssOlapServer;
import com.essbase.api.domain.IEssDomain;
import com.essbase.api.metadata.IEssCubeOutline;
import com.essbase.api.metadata.IEssDimension;
import com.essbase.api.metadata.IEssMember;
import com.essbase.api.session.IEssbase;
import com.indrasol.essbase.essbasesheetplugin.model.Credentials;
import com.indrasol.essbase.essbasesheetplugin.model.Dimension;
import com.indrasol.essbase.essbasesheetplugin.model.EMembers;
import com.indrasol.essbase.essbasesheetplugin.model.EssbaseConnection;

/**
    Connect example signs on to Analytic Server and signs off.

    In order for this sample to work in your environment, make sure to
    change the static variables to suit your environment.

    @author  Chaitanya 
    @version 1.1, 
 */


public class EssbaseHelper {

    private  static boolean loadData = false;
    
    private static final int FAILURE_CODE = 1;


    public static EssbaseConnection connect(Credentials credentials) {
        int statusCode = 0; // will set this to FAILURE only if err/exception occurs.
    
        IEssbase ess = null;
        IEssOlapServer olapSvr = null;
        EssbaseConnection essbaseConnection = new EssbaseConnection();
        essbaseConnection.setCredentials(credentials);
        try {
            // Create JAPI instance.
            ess = IEssbase.Home.create(IEssbase.JAPI_VERSION);
            essbaseConnection.setEss(ess);
            
            // uncomment below to set OAM credentials in fusion mode
//            ess.setOAMParams("oemUser", "oemPassword", "en-US"); 
            // Sign On to the Provider
            IEssDomain dom 
                = ess.signOn(credentials.getUserName(), credentials.getPassword(), false, null, credentials.getUrl());

            /*
             * Following line demonstrates the JAPI usage in a Active/Passive
             * Essbase Cluster Scenario. In case of Active/Passive Essbase Cluster
             * environment, User has to provide the APS url and the cluster name
             * in the format given bellow as the olapServerName parameter. JAPI
             * client then will parse this and will retrieve the active Essbase
             * node info by contacting the given APS url.
             * The remaining JAPI sequence remain same at it is.
             */
//            IEssOlapServer olapSvr = dom.getOlapServer("http://localhost:13080/aps/Essbase?ClusterName=CLUSTER1&SecureMode=yes");
            
            olapSvr = dom.getOlapServer(credentials.getOlapServerName());
            olapSvr.connect();
            System.out.println("Connection to Analytic server '" +olapSvr.getName()+ "' was successful.");
            String apiVersion = ess.getApiVersion();
            String apiVerDetail = ess.getApiVersionDetail();
            System.out.println("API Version :"+apiVersion);
            System.out.println("API Version Detail :"+apiVerDetail);
            credentials.setApiVersion(apiVersion);
            credentials.setApiVersionDetail(apiVerDetail);

            essbaseConnection.setApiVersion(apiVersion);
            essbaseConnection.setApiVersionDetail(apiVerDetail);

            if (loadData) loadData(olapSvr);
            //return olapSvr;    
            essbaseConnection.setOlapSvr(olapSvr);
            essbaseConnection.setCredentials(credentials);
            return essbaseConnection;
		} catch (EssException x) {
            System.err.println("Error: " + x.getMessage());
            statusCode = FAILURE_CODE;
        } finally {
            ;
        }
        // Set status to failure only if exception occurs and do abnormal termination
        // otherwise, it will by default terminate normally 
        // if (statusCode == FAILURE_CODE) 
        //     System.exit(FAILURE_CODE);        
        return essbaseConnection;
    }
    
    public static List<IEssDimension> loadData(IEssOlapServer olapSvr) throws EssException {
        IEssCube cube = olapSvr.getApplication("Sample").getCube("Basic");
        IEssCubeOutline otl = cube.openOutline();
        List<IEssDimension> dimensionList = EssbaseUtil.getDimensionsAsList(otl);
        System.out.println("\nOutline Viewing sample complete."+dimensionList);
        
    	/*String [][] data = cube.loadData(IEssOlapFileObject.TYPE_RULES, null, IEssOlapFileObject.TYPE_TEXT,
    			"Calcdat", true);
        System.out.println("Loaddata done to Sample/Basic"+data);
        for(int r=0; r<data.length; r++) {
            System.out.printf("r=%d data[%d]=%s",r,r, data[r]);
            for(int c=0; c<data[r].length; c++) {
                System.out.printf("data[%d][%d]=%s",r,c,data[r][c]);
            }
        }
        */
        return dimensionList;


    }

    public static List<Dimension> getAllDimensions(IEssOlapServer olapSvr) throws EssException {
        List<Dimension> listDimension = new ArrayList<Dimension>();
        List<EMembers> listMembers = new ArrayList<EMembers>();
        
        IEssCube cube = olapSvr.getApplication("Sample").getCube("Basic");
        IEssCubeOutline otl = cube.openOutline();
        List<IEssDimension> dimensionObjList = EssbaseUtil.getDimensionsAsList(otl);
        System.out.println("\nOutline Viewing sample complete."+dimensionObjList);
        for(IEssDimension dimObj : dimensionObjList) {
            Dimension dim = new Dimension();
            dim.setDimensionNumber(dimObj.getDimensionNumber());
            dim.setDimensionName(dimObj.getName());
            dim.setDimensionDescription(dimObj.getDescription());
            //load Members

            List<IEssMember> memberListObj = EssbaseUtil.getMembers(dimObj);
            listMembers = new ArrayList<EMembers>();
            for(IEssMember memObj : memberListObj) {
                EMembers mem =  new EMembers();
                mem.setDimensionRootMember(memObj.isDimensionRootMember());
                mem.setChildCount(memObj.getChildCount());
                mem.setGenerationNumber(memObj.getGenerationNumber());
                mem.setLevelNumber(memObj.getLevelNumber());
                mem.setMemberDescription(memObj.getDescription());
                mem.setMemberName(memObj.getName());
                mem.setMemberId(memObj.getMemberId());
                listMembers.add(mem);
            }
            dim.setMembers(listMembers);
            listDimension.add(dim);

        }

        return listDimension;
        
    }
    public static List<EMembers> getAllMembersForDimensions(IEssOlapServer olapSvr, String dimName) throws EssException {
        List<EMembers> listMembers = new ArrayList<EMembers>();
        
        IEssCube cube = olapSvr.getApplication("Sample").getCube("Basic");
        IEssCubeOutline otl = cube.openOutline();
        //IEssCubeView cubeView =  cube.openCubeView(cube.getName());
        //System.out.println("HTML->"+cubeView.getHtmlOutput());
        List<IEssDimension> dimensionObjList = EssbaseUtil.getDimensionsAsList(otl);
        System.out.println("\nOutline Viewing sample complete."+dimensionObjList);
        for(IEssDimension dimObj : dimensionObjList) {
                if(dimObj.getName().equalsIgnoreCase(dimName)){
                //load Members
                List<IEssMember> memberListObj = EssbaseUtil.getMembers(dimObj);
                listMembers = new ArrayList<EMembers>();
                for(IEssMember memObj : memberListObj) {
                    EMembers mem =  new EMembers();
                    mem.setDimensionRootMember(memObj.isDimensionRootMember());
                    mem.setChildCount(memObj.getChildCount());
                    mem.setGenerationNumber(memObj.getGenerationNumber());
                    mem.setLevelNumber(memObj.getLevelNumber());
                    mem.setMemberDescription(memObj.getDescription());
                    mem.setMemberName(memObj.getName());
                    mem.setMemberId(memObj.getMemberId());
                    listMembers.add(mem);
                }
            }

        }

        return listMembers;
        
    }
    
    public static void disconnect(EssbaseConnection essbaseConnection) {
        // Sign off.
        try {
            if (essbaseConnection.getEss() != null && essbaseConnection.getEss().isSignedOn() == true) {
                essbaseConnection.getEss().signOff();
                System.out.println("Signout Off Sucessfully....");
            }
        } catch (EssException x) {
            System.err.println("Error: " + x.getMessage());
        }
    }

}