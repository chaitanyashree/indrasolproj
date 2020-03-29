/*
    File: GetMembers.java 1.1, 2006-7-18
    Copyright (c) 1991, 2007 Oracle and / or its affiliates. All rights reserved.
 */
package com.essbase.samples.japi;

import com.essbase.api.base.*;
import com.essbase.api.session.*;
import com.essbase.api.datasource.*;
//import com.essbase.api.dataquery.*;
import com.essbase.api.domain.*;
import com.essbase.api.metadata.*;
                                                                       
/**
    GetMembers Example does the following: Signs on to essbase domain,
    Performs various metadata operations and Signs Off.

    In order for this sample to work in your environment, make sure to
    change the s_* to suit your environment.

    @author Srini Ranga
    @version 1.1, 18 Jul 06
 */
public class GetMembers {
    // NOTE: Change the following variables to suit your setup.
    private static String s_userName = "system";
    private static String s_password = "password";
    private static String s_olapSvrName = "localhost";
    /* Possible values for s_provider: 
        "Embedded" or "http://localhost:13080/aps/JAPI" */
    private static String s_provider = "Embedded"; // Default
    
    private static final int FAILURE_CODE = 1;

    public static void main(String[] args) {
        int statusCode = 0;
        IEssbase ess = null;
        IEssOlapServer olapSvr = null;
        try {
            acceptArgs(args);

            // Create JAPI instance.
            ess = IEssbase.Home.create(IEssbase.JAPI_VERSION);

            // Sign On to the Provider
            IEssDomain dom 
                = ess.signOn(s_userName, s_password, false, null, s_provider);

            // Open connection with olap server and get the cube.
            olapSvr = (IEssOlapServer)dom.getOlapServer(s_olapSvrName);
            olapSvr.connect();
            IEssCube cube = olapSvr.getApplication("Sample").getCube("Basic");

            // Get a member directly from cube and do member operations.
            getMemberFromCube(cube);

            // Perform member selection query and do member operations.
            performMemberSelection(cube);

            // Open the outline and do member operations.
            queryOutline(cube);
		} catch (EssException x) {
            System.err.println("Error: " + x.getMessage());
            statusCode = FAILURE_CODE;
        } finally {
            try {
                if (olapSvr != null && olapSvr.isConnected() == true)
                    olapSvr.disconnect();
            } catch (EssException x) {
                System.err.println("Error: " + x.getMessage());
            }

            try {
                if (ess != null && ess.isSignedOn() == true)
                    ess.signOff();
            } catch (EssException x) {
                System.err.println("Error: " + x.getMessage());
            }
        }
        // Set status to failure only if exception occurs and do abnormal termination
        // otherwise, it will by default terminate normally
        if (statusCode == FAILURE_CODE) System.exit(FAILURE_CODE);
    }

    static void displayMemberProperties(IEssMember mbr) throws EssException {
        System.out.println("\nDisplaying member properties...\n");
        System.out.println("Name: " + mbr.getName());
        System.out.println("Description: " + mbr.getDescription());
        System.out.println("Dimension Root Member: " + mbr.isDimensionRootMember());
        System.out.println("Level Number: " + mbr.getLevelNumber());
        System.out.println("Generation Number: " + mbr.getGenerationNumber());
        System.out.println("Unary consolidation type: " + mbr.getConsolidationType());
        System.out.println("Dimension Name: " + mbr.getDimensionName());

        // Following properties cannot be used with member objects obtained via
        // IEssCube.getMember
        System.out.println("Two pass calculation member: " + mbr.isTwoPassCalculationMember());
        System.out.println("Expense member: " + mbr.isExpenseMember());
        System.out.println("Currency conversion type: " + mbr.getCurrencyConversionType());
        System.out.println("Currency category or name: " + mbr.getCurrencyCategoryOrName());
        System.out.println("Time balance option: " + mbr.getTimeBalanceOption());
        System.out.println("Time balance skip option: " + mbr.getTimeBalanceSkipOption());
        System.out.println("Share option: " + mbr.getShareOption());
        //System.out.println("Dimension storage type: " + mbr.getDimensionStorageType());
        System.out.println("Dimension category: " + mbr.getDimensionCategory());
        System.out.println("Dimension storage category: " + mbr.getDimensionStorageCategory());
        System.out.println("Child count: " + mbr.getChildCount());
        System.out.println("Attributes associated: " + mbr.isAttributesAssociated());

        //IEssIterator atts = mbr.getAssociatedAttributes();

        System.out.println("Attribute value: " + mbr.getAttributeValue());
        System.out.println("Member number: " + mbr.getMemberNumber());
        System.out.println("Dimension number: " + mbr.getDimensionNumber());

        /* // Following properties will expect the app to be running.
        System.out.println("Parent member name: " + mbr.getParentMemberName());
        System.out.println("First child member name: " + mbr.getFirstChildMemberName());
        System.out.println("Previous sibling member name: " + mbr.getPreviousSiblingMemberName());
        System.out.println("Next sibling member name: " + mbr.getNextSiblingMemberName()); */
    }

    static void displayMemberPropertiesLight(IEssMember mbr) throws EssException {
        System.out.println("\nDisplaying member properties...\n");
        System.out.println("Name: " + mbr.getName());
        System.out.println("Dimension Root Member: " + mbr.isDimensionRootMember());
        System.out.println("Level Number: " + mbr.getLevelNumber());
        System.out.println("Generation Number: " + mbr.getGenerationNumber());
        System.out.println("Unary consolidation type: " + mbr.getConsolidationType());
        System.out.println("Dimension Name: " + mbr.getDimensionName());
        System.out.println("Parent member name: " + mbr.getParentMemberName());
        System.out.println("Member number: " + mbr.getMemberNumber());
        System.out.println("Dimension number: " + mbr.getDimensionNumber());
        
        // Fix for bug#6569334 : Removing deprecated APIs. 
//        System.out.println("Next Sibling Member Name: " + mbr.getNextSiblingMemberName());
//        System.out.println("First Child Member Name: " + mbr.getFirstChildMemberName());
//        System.out.println("Previous Sibling Member Name: " + mbr.getPreviousSiblingMemberName());

    }

    static void displayDimensionProperties(IEssDimension dim) throws EssException {
        System.out.println("\nDisplaying dimension properties...\n");
        System.out.println("Name: " + dim.getName());
        System.out.println("Description: " + dim.getDescription());
        System.out.println("Dimension number: " + dim.getDimensionNumber());
        System.out.println("Dimension storage type: " + dim.getStorageType());
        System.out.println("Dimension tag: " + dim.getTag());
        System.out.println("Declared size: " + dim.getDeclaredSize());
        System.out.println("Actual size: " + dim.getActualSize());
        System.out.println("Attribute dimension data type: " +
            dim.getAttributeDimensionDataType());
    }

    static void displayDimensionPropertiesLight(IEssDimension dim)
            throws EssException {
        System.out.println("\nDisplaying dimension properties...\n");
        System.out.println("Name: " + dim.getName());
        System.out.println("Dimension number: " + dim.getDimensionNumber());
        System.out.println("Dimension storage type: " + dim.getStorageType());
        System.out.println("Dimension tag: " + dim.getTag());
        System.out.println("Declared size: " + dim.getDeclaredSize());
        System.out.println("Actual size: " + dim.getActualSize());
        System.out.println("Attribute dimension data type: " +
            dim.getAttributeDimensionDataType());
    }

    static void performMemberOperations(IEssMember mbr) throws EssException {
        System.out.println("\nPerforming member operations...\n");
        System.out.println("Alias: " + mbr.getAlias("Default"));
    }

    static void getMemberFromCube(IEssCube cube) throws EssException {
        System.out.println
                ("\nGetting a Member from Cube (Year): \n" +
                 "---------------------------------");
        IEssMember mbr = cube.getMember("Year");
        displayMemberPropertiesLight(mbr);

        System.out.println
                ("\nGetting a Member from Cube (Year): \n" +
                 "---------------------------------");
        IEssDimension dim = cube.getDimension("Year");
        IEssMember rootMbr = dim.getDimensionRootMember();
        displayDimensionPropertiesLight(dim);
        displayMemberPropertiesLight(rootMbr);

        System.out.println
                ("\nGetting a Dimension from Cube (Year): \n" +
                 "---------------------------------");
        dim = cube.getDimension("Caffeinated");
        displayDimensionPropertiesLight(dim);
    }

    static void performMemberSelection(IEssCube cube) throws EssException {
        IEssMemberSelection mbrSel = null;
        try {
            mbrSel = cube.openMemberSelection("Sample member selection");
            mbrSel.executeQuery("Year", IEssMemberSelection.QUERY_TYPE_CHILDREN,
                IEssMemberSelection.QUERY_OPTION_MEMBERSONLY, "Year", "", "");
            IEssIterator mbrs = mbrSel.getMembers();
            System.out.println
                ("\nPerforming Member Selection (Select children of Year): \n" +
                 "------------------------------------------------------");
            for (int i = 0; i < mbrs.getCount(); i++) {
                IEssMember mbr = (IEssMember)mbrs.getAt(i);
                displayMemberProperties(mbr);
            }
            System.out.println();

            queryAttributes(mbrSel);
            
            mbrSel.close();
            mbrSel = null;
        } catch (EssException x) {
            System.err.println("Error: " + x.getMessage());
        } finally {
            try {
                if (mbrSel != null) 
                    mbrSel.close();
            } catch (EssException x) {
                System.err.println("Error: " + x.getMessage());
            }
        }
    }

    static void queryAttributes(IEssMemberSelection mbrSel) throws EssException {
    	System.out.println("---- queryAttributes [BEGIN] ---");
    	IEssAttributeQuery aQuery = mbrSel.createAttributeQuery();
    	aQuery.set(IEssAttributeQuery.MBR_TYPE_BASE_MEMBER, IEssAttributeQuery.MBR_TYPE_ATTRIBUTE_MEMBER);
    	aQuery.setInputMember("100-10");
    	IEssMember[] mbrs = mbrSel.queryAttributes(aQuery);
    	if (mbrs!=null) {
	        for (int i = 0; i < mbrs.length; i++) {
	            IEssMember mbr = (IEssMember)mbrs[i];
	            displayMemberProperties(mbr);
	        }
    	}
    	System.out.println("---- queryAttributes [END] ---");
    }
    
    static void queryOutline(IEssCube cube) throws EssException {
        IEssCubeOutline outline = null;
        try {
            System.out.println("\nPerforming Outline Query (find member Qtr1) \n" +
                "-------------------------------------------");
            outline = cube.openOutline();

            IEssMember mbr = outline.findMember("Qtr1");
            displayMemberProperties(mbr);

            IEssIterator mbrs = outline.findMembers(new String[]{"Qtr2", "Qtr4"});
            for (int i = 0, cnt = mbrs.getCount(); i < cnt; i++) {
                displayMemberProperties((IEssMember)mbrs.getAt(i));
            }

            IEssDimension dim = outline.findDimension("Year");
            displayDimensionProperties(dim);
            performMemberOperations(dim.getDimensionRootMember());

            System.out.println("\nFinding attribute member (03-25-1996)...\n");
            IEssIterator attMbrs = outline.findAttributeMembers("03-25-1996", "Intro Date");
            for(int i = 0; i < attMbrs.getCount(); i++) {
                IEssMember attMbr = (IEssMember) attMbrs.getAt(i);
                System.out.println("Name: " + attMbr.getName());
                IEssValueAny val = attMbr.getAttributeValue(); 
                System.out.println("Value:" + val);
            }

            System.out.println("\nFinding attribute member (Can)...\n");
            mbr = outline.findMember("Can");
            System.out.println("Value: " + mbr.getAttributeValue());

            outline.close();
            outline = null;
        } catch (EssException x) {
            System.err.println("Error: " + x.getMessage());
        } finally {
            try {
                if (outline != null)
                    outline.close();
            } catch (EssException x) {
                System.err.println("Error: " + x.getMessage());
            }
        }
    }

    static void acceptArgs(String[] args) throws EssException {
        if (args.length >= 4) {
            s_userName = args[0];
            s_password = args[1];
            s_olapSvrName = args[2];
            s_provider = args[3]; //PROVIDER
        } else if (args.length != 0) {
            System.err.println("ERROR: Incorrect Usage of this sample.");
            System.err.println(
                "Usage: java " + GetMembers.class.getName() + " <user> <password> <analytic server> <provider>");
            System.exit(1); // Simply end
        }
    }
}

