/*
    File: GridDataUpdate.java 1.1, 2006-7-18
    Copyright (c) 1991, 2007 Oracle and / or its affiliates. All rights reserved.
 */
package com.essbase.samples.japi;

import com.essbase.api.base.*;
import com.essbase.api.session.*;
//import com.essbase.api.datasource.*;
import com.essbase.api.dataquery.*;
import com.essbase.api.domain.*;

/**
    GridDataUpdate Example does the following: Signs on to essbase domain,
    Opens a cube view, Performs update, and Signs Off.

    In order for this sample to work in your environment, make sure to
    change the s_* to suit your environment. If you change s_appName
    and s_cubeName, make sure to edit the method performCubeViewOperation()
    accordingly.

    @author Srini Ranga
    @version 1.1, 18 Jul 06
 */
public class GridDataUpdate {
    // NOTE: Change the following variables to suit your setup.
    private static String s_userName = "system";
    private static String s_password = "password";

    private static String s_olapSvrName = "localhost";
    /* Possible values for s_provider:
        "Embedded" or "http://localhost:13080/aps/JAPI" */
    private static String s_provider = "Embedded"; // Default

    private static String s_appName = "demo";
    private static String s_cubeName = "basic";

    private static final int FAILURE_CODE = 1;

    public static void main(String[] args) {
        int statusCode = 0;
        IEssbase ess = null;
        IEssCubeView cv = null;
        try {
            acceptArgs(args);

            // Create JAPI instance.
            ess = IEssbase.Home.create(IEssbase.JAPI_VERSION);

            // Sign On to the Provider
            IEssDomain dom
                = ess.signOn(s_userName, s_password, false, null, s_provider);
            
            // example for BSO cubes
            cv = dom.openCubeView("Data Update Example",
                s_olapSvrName, s_appName, s_cubeName);

            performUpdate(ess, cv);
            cv.close();
            
            // example for ASo cubes
            cv = dom.openCubeView("Data Update Example for ASO cube",
                    s_olapSvrName, "ASOSamp", "Sample");

                performUpdateOnASO(ess, cv);

        } catch (EssException x) {
            System.err.println("ERROR: " + x.getMessage());
            statusCode = FAILURE_CODE;
        } finally {
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

    static void performUpdate(IEssbase ess, IEssCubeView cv)
            throws EssException {
        // Performs a retrieve, then locks and updates (increments) a cell
        // value or just sets a cell value if #missing, then unlocks and
        // retrieves the result and prints it.

        IEssGridView grid = cv.getGridView();
        grid.setSize(3, 5);
        grid.setValue(0, 2, "Product");
        grid.setValue(0, 3, "Market");
        grid.setValue(1, 2, "Jan"); ;
        grid.setValue(1, 3, "Feb");
        grid.setValue(1, 4, "Mar");
        grid.setValue(2, 0, "Actual");
        grid.setValue(2, 1, "Sales");
        cv.performOperation(cv.createIEssOpRetrieve()); // Retrieve

       // since the cube is not loaded with data by default, this might not have a double value.
       // grid.getDoubleValue(2, 2);
        System.out.println("\nData Cell at 3rd-row, 3rd-column: " + grid.getValue(2,2).toString());

        int row = 2, col = 2;
        if (grid.getCellContentType(row, col) ==
                IEssGridView.CELL_CONTENT_TYPE_DOUBLE) {
            IEssValueAny val = grid.getValue(row, col);
            double dblVal = val.getDouble();
            grid.setValue(row, col, dblVal+1);   // increment cell value.
        } else if (grid.getCellContentType(row, col) ==
                IEssGridView.CELL_CONTENT_TYPE_MISSING) {
            grid.setValue(row, col, 9999);   // replace #missing with 9999.
        }

        IEssOpUpdate opUpd = cv.createIEssOpUpdate();
        cv.performOperation(opUpd);                    // Update

        cv.performOperation(cv.createIEssOpRetrieve()); // Retrieve again.

        int cntRows = grid.getCountRows(), cntCols = grid.getCountColumns();
        System.out.print("Query Results for the Operation: Retrieve after an Update\n" +
            "---------------------------------------------------------\n");
        for (int i = 0; i < cntRows; i++) {
            for (int j = 0; j < cntCols; j++)
                System.out.print(grid.getValue(i, j) + "\t");
            System.out.println();
        }
        System.out.println("\n");
    }

    static void performUpdateOnASO(IEssbase ess, IEssCubeView cv)
    throws EssException {

    	// The grid should contain leaf level members for all dimensions.
    	// Derived cells will not be updated and only storage-type cells can be updated. 
    	// Essbase will just log a warning and not error if the cells are not stored-type.

    	IEssGridView grid = cv.getGridView();
    	grid.setSize(3, 11);
    	grid.setValue(0, 10, "36310");
    	grid.setValue(1, 0, "044862");
    	grid.setValue(1, 1, "Under 20,000");
    	grid.setValue(1, 2, "1 to 13 Years"); ;
    	grid.setValue(1, 3, "Coupon");
    	grid.setValue(1, 4, "Cash");
    	grid.setValue(1, 5, "Sale");
    	grid.setValue(1, 6, "Jan");
    	grid.setValue(1, 7, "Units");
    	grid.setValue(1, 8, "Curr Year");
    	grid.setValue(1, 9, "HDTV");
    	cv.performOperation(cv.createIEssOpRetrieve()); // Retrieve

    	System.out.println("\nData Cell at 1st-row, 10th-column: " + grid.getValue(1,10).toString());

    	int row = 1, col = 10;
    	if (grid.getCellContentType(row, col) ==
    		IEssGridView.CELL_CONTENT_TYPE_DOUBLE) {
    		IEssValueAny val = grid.getValue(row, col);
    		double dblVal = val.getDouble();
    		grid.setValue(row, col, dblVal+1);   // increment cell value.
    	} else if (grid.getCellContentType(row, col) ==
    		IEssGridView.CELL_CONTENT_TYPE_MISSING) {
    		grid.setValue(row, col, 5);   // replace #missing.
    	}

    	IEssOpUpdate opUpd = cv.createIEssOpUpdate();
    	opUpd.setPreviousLockRequired(true);	// REQUIRED for ASO cubes.
    	cv.performOperation(opUpd);                    // Update

    	cv.performOperation(cv.createIEssOpRetrieve()); // Retrieve again.

    	int cntRows = grid.getCountRows(), cntCols = grid.getCountColumns();
    	System.out.print("Query Results for the Operation: Retrieve after an Update\n" +
    	"---------------------------------------------------------\n");
    	for (int i = 0; i < cntRows; i++) {
    		for (int j = 0; j < cntCols; j++)
    			System.out.print(grid.getValue(i, j) + "\t");
    		System.out.println();
    	}
    	System.out.println("\n");
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
                "Usage: java " + GridDataUpdate.class.getName() + " <user> <password> <analytic server> <provider>");
            System.exit(1); // Simply end
        }
    }
}
