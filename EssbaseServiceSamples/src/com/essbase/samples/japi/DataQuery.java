/*
    File: DataQuery.java 1.1, 2006-7-18
    Copyright (c) 1991, 2007 Oracle and / or its affiliates. All rights reserved.
 */
package com.essbase.samples.japi;

import com.essbase.api.base.*;
import com.essbase.api.session.*;
//import com.essbase.api.datasource.*;
import com.essbase.api.dataquery.*;
import com.essbase.api.metadata.*;
import com.essbase.api.domain.*;

/**
    Data Query Example does the following: Signs on to essbase domain,
    Opens a cube view, Performs Retrieve, ZoomIn, ZoomOut, KeepOnly,
    RemoveOnly, Pivot, and Signs Off.

    In order for this sample to work in your environment, make sure to
    change the s_* variables make sure to edit the method performCubeViewOperation()
    accordingly.

    @author Srini Ranga
    @version 1.1, 18 Jul 2006
 */
public class DataQuery {
    // NOTE: Change the following variables to suit your setup.
    private static String s_userName = "system";
    private static String s_password = "password";
    
    /* Possible values for s_provider: 
       "Embedded"
       "http://localhost:13080/aps/JAPI" */
    private static String s_provider = "Embedded"; // Default
    private static String s_analyticSvrName = "localhost";
//    private static String s_appName = "demo";
//    private static String s_cubeName = "basic";
    private static String s_appName = "Sample";
    private static String s_cubeName = "Basic";

    private static final int FAILURE_CODE = 1;
    
    public static void main(String[] args) {
        int statusCode = 0;
        IEssbase ess = null;
        IEssCubeView cv = null;
        try {
            acceptArgs(args);

            // Create JAPI instance.
            ess = IEssbase.Home.create(IEssbase.JAPI_VERSION);
            
            // Sign on to the provider, and perform data query operations.
            IEssDomain dom = ess.signOn(s_userName, s_password, false, null, s_provider);
            cv = dom.openCubeView("Data Query Example", s_analyticSvrName, s_appName,
                s_cubeName);

            // Set couple of cube view properties.
            cv.setRepeatMemberNames(false);
            cv.setIncludeSelection(true);
            cv.updatePropertyValues();

            // Perform various cube view operations.
//            performCubeViewOperation(ess, cv, "conditionalRetrieve");
            performCubeViewOperation(ess, cv, "retrieve");
            performCubeViewOperation(ess, cv, "zoomIn");
//            performCubeViewOperation(ess, cv, "conditionalZoomIn");
//            performCubeViewOperation(ess, cv, "zoomOut");

            //performMemberSelection(ess, cv);
//
//            performCubeViewOperation(ess, cv, "keepOnly");
//            performCubeViewOperation(ess, cv, "removeOnly");
//            performCubeViewOperation(ess, cv, "pivot");
//            performCubeViewOperation(ess, cv, "report");
//            performCubeViewOperation(ess, cv, "reportFile");
//            performCubeViewOperation(ess, cv, "report_with_no_parsing");
//            performCubeViewOperation(ess, cv, "reportFile_with_no_parsing");
            
        } catch (EssException x){
            System.err.println("ERROR: " + x.getMessage());
            statusCode = FAILURE_CODE;
        } finally{
            // Close cube view.
            try {
                if (cv != null)
                    cv.close();
            } catch (EssException x) {
                System.err.println("Error: " + x.getMessage());
            }

            // Sign off from the domain.
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

    static void performCubeViewOperation(IEssbase ess, IEssCubeView cv,
            String opStr) throws EssException {
        // Create a grid view with the input for the operation.

        IEssGridView grid = cv.getGridView();
       // grid.setSize(3,3);
//        grid.setSize(3, 5);
//        grid.setValue(0, 2, "Product");
//        grid.setValue(0, 3, "Market");
//        grid.setValue(1, 2, "Jan"); ;
//        grid.setValue(1, 3, "Feb");
//        grid.setValue(1, 4, "Mar");
//        grid.setValue(2, 0, "Actual");
//        grid.setValue(2, 1, "Sales");

        // Create the operation specification.
        IEssOperation op = null;
        if (opStr.equals("conditionalRetrieve")) {
            op = cv.createIEssOpRetrieve();
            ((IEssOpRetrieve)op).setQuery("{TabDelim}<Column (Scenario, Year)" +
                "<Row (Market, Product) <Ichild Market <Ichild Product !",
                IEssCubeView.EEssQueryGrammar.ESSBASE);
        } else if (opStr.equals("report")) {
            op = cv.createIEssOpReport();
            ((IEssOpReport)op).set(false, "{TabDelim}<idesc Year !", false);
        } else if (opStr.equals("reportFile")) {
            op = cv.createIEssOpReport();
            ((IEssOpReport)op).set("Actsales", false, false);
        } else if (opStr.equals("report_with_no_parsing")) {
            op = cv.createIEssOpReport();
            ((IEssOpReport)op).set(false, "{TabDelim}<idesc Year !", false);
            ((IEssOpReport)op).setNoParsing(true);
            cv.performOperation(op);
            System.out.print("Query Results for the Operation: " + opStr + "\n" +
            "-----------------------------------------------------\n");
            System.out.println(grid.toString());
            return;
        } else if (opStr.equals("reportFile_with_no_parsing")) {
            op = cv.createIEssOpReport();
            ((IEssOpReport)op).set("Actsales", false, false);
            ((IEssOpReport)op).setNoParsing(true);
            cv.performOperation(op);
            System.out.print("Query Results for the Operation: " + opStr + "\n" +
            "-----------------------------------------------------\n");
            System.out.println(grid.toString());
            return;
        } else if (opStr.equals("retrieve")) {
            op = cv.createIEssOpRetrieve();
        } else if (opStr.equals("zoomIn")) {
            op = cv.createIEssOpZoomIn();
            ((IEssOpZoomIn)op).addRange(1, 0, 1, 1);
        } else if (opStr.equals("conditionalZoomIn")) {
            IEssOpZoomIn opCzi = cv.createIEssOpZoomIn();
            opCzi.addRange(0, 3, 1, 1);
            opCzi.setPreference(true,
                IEssOpZoomIn.EEssZoomInPreference.BOTTOM_LEVEL);
            opCzi.setQuery(true, "<child Market !",
                IEssCubeView.EEssQueryGrammar.ESSBASE);
            op = opCzi;
        } else if (opStr.equals("zoomOut")) {
            op = cv.createIEssOpZoomOut();
            ((IEssOpZoomOut)op).addRange(1, 2, 1, 1);
        } else if (opStr.equals("keepOnly")) {
            op = cv.createIEssOpKeepOnly();
            ((IEssOpKeepOnly)op).addRange(1, 3, 1, 1);
        } else if (opStr.equals("removeOnly")) {
            op = cv.createIEssOpRemoveOnly();
            ((IEssOpRemoveOnly)op).addRange(1, 4, 1, 1);
        } else if (opStr.equals("pivot")) {
            op = cv.createIEssOpPivot();
            ((IEssOpPivot)op).set(0, 3);
        } else
            throw new EssException("Operation not supported.");

        // Perform the operation.
        cv.performOperation(op);

        // Get the result and print the output.
        int cntRows = grid.getCountRows(), cntCols = grid.getCountColumns();
        System.out.print("Query Results for the Operation: " + opStr + "\n" +
            "-----------------------------------------------------\n");
        for (int i = 0; i < cntRows; i++) {
            for (int j = 0; j < cntCols; j++)
                System.out.print(grid.getValue(i, j) + "\t");
            System.out.println();
        }
        System.out.println("\n");
        System.out.println(cv.getHtmlOutput());
    }

    static void performMemberSelection(IEssbase ess, IEssCubeView cv)
            throws EssException {
        String fldSel = "<OutputType Binary <SelectMbrInfo (MemberName, MemberLevel, MemberGen, Consolidation, MemberFormula, MemberAlias, DimensionName, Expense,  MemberNumber, DimensionNumber, ChildMemberName, ParentMemberName, PreviousMemberName, NextMemberName)",
               mbrSel = "@ichild(Product), @ichild(Market)";
        IEssMember[] mbrs = cv.memberSelection(mbrSel, fldSel);
        for (int i = 0; i < mbrs.length; i++) {
            IEssMember mbr = mbrs[i];
            System.out.println("Name: " + mbr.getName());
            System.out.println("Level: " + mbr.getLevelNumber());
            System.out.println("Generation: " + mbr.getGenerationNumber());
            System.out.println("Consolidation: " + mbr.getConsolidationType());
            System.out.println("Formula: " + mbr.getFormula());
            System.out.println("Dimension name: " + mbr.getDimensionName());
            // IEssMember.getChildCount() operation not allowed on Members obtained from IEssCubeView.memberSelection.
//            System.out.println("Child count: " + mbr.getChildCount());
            System.out.println("Parent name: " + mbr.getParentMemberName());
            System.out.println("Member number: " + mbr.getMemberNumber());
            System.out.println("Dimension number: " + mbr.getDimensionNumber());
        }

        mbrs = cv.memberSelection("Year", IEssMemberSelection.QUERY_TYPE_CHILDREN,
            IEssMemberSelection.QUERY_OPTION_MEMBERSONLY, "Year", "", "");
         for (int i = 0; i < mbrs.length; i++) {
            IEssMember mbr = mbrs[i];
            System.out.println("Name: " + mbr.getName() +
                ", Desc: " + mbr.getDescription() +
                ", Level Num: " + mbr.getLevelNumber() +
                ", Gen Num: " + mbr.getGenerationNumber() +
//                ", Child count: " + mbr.getChildCount() +
                ", Dim Name: " + mbr.getDimensionName() /*+
                ", Dim Category: " + mbr.getDimensionCategory().stringValue()*/);
         }
    }

    static void acceptArgs(String[] args) throws EssException {
        if (args.length >= 4) {
            s_userName = args[0];
            s_password = args[1];
            s_analyticSvrName = args[2];
            s_provider = args[3]; //PROVIDER
        } else if (args.length != 0) {
            System.err.println("ERROR: Incorrect Usage of this sample.");
            System.err.println(
                "Usage: java " + DataQuery.class.getName() + " <user> <password> <analytic server> <provider>");
            System.exit(1); // Simply end
        }
    }
}
