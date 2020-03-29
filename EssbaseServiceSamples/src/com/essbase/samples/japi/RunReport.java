/*
    File: RunReport.java 1.1, 2006-7-19
    Copyright (c) 1991, 2007 Oracle and / or its affiliates. All rights reserved.
 */
package com.essbase.samples.japi;

import java.io.File;

import com.essbase.api.base.*;
import com.essbase.api.session.*;
import com.essbase.api.datasource.*;
//import com.essbase.api.dataquery.*;
import com.essbase.api.domain.*;
//import com.essbase.api.metadata.*;

/**
    RunReport Example does the following: Signs on to essbase domain,
    Runs a report and Signs Off.

    In order for this sample to work in your environment, make sure to
    change the s_* variables to suit your environment.

    @author Srini Ranga
    @version 1.1, 19 Jul 06
 */
public class RunReport {
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

            // Open connection with OLAP server and get the cube.
            olapSvr = (IEssOlapServer)dom.getOlapServer(s_olapSvrName);
            olapSvr.connect();

            IEssCube cube = olapSvr.getApplication("Sample").getCube("Basic");
            executeReport(cube);
            System.out.println("\nReport Execution Sample Completed.");
        } catch (EssException x) {
            System.out.println("Error: " + x.getMessage());
            statusCode = FAILURE_CODE;
        } finally {
            // Close OLAP server connection and sign off from the domain.
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

    static void executeReport(IEssCube cube) throws EssException {
        try {
            // Execute a report by passing the specification string.
		    String repSpec = "{TABDELIMIT}" +
                "{SUPALL COLHEADING NAMESON BLOCKHEADERS PAGEHEAD INDENTGEN 2 DECIMALS VARIABLE}" +
                "<SINGLECOLUMN " +
                "{INCMISSINGROWS}" +
                "<SUPSHAREOFF" +
                "{INCZEROROWS}" +
                "{MISSINGTEXT \"n/a\"}<SYM <PAGE( \"Year\",\"Market\")" +
                "<SORTASC \"Year\" <SORTASC \"Market\" <COL( \"Scenario\")" +
                "{ OUTALTNAMES }" +
                "<SORTASC \"Actual\" \"Budget\" <ROW( \"Product\",\"Measures\") "+
                "<SORTASC <CHILDREN \"Product\" <SORTASC <DESCENDANT \"Measures\" !";
            System.out.println("Executing a report by passing the specification string....");
            System.out.println("\n\nReport Output for spec: "+repSpec+"\n----------------"+
                "------------------------------------------------------------");
            String output = cube.report(repSpec, true, false);
            System.out.println(output);

            // Execute a report by passing file name in the server.
            String reportFile = "Top";
            System.out.println("\nExecuting a report by passing file name in the server....");
            System.out.println("\n\nReport Output for file: " + reportFile +
                "\n-----------------------------");
            output = cube.report(true, false, reportFile, false);
            System.out.println(output);

            // Copy a report file from server to JAPI client, and execute the
            // report by passing this client file name.
            String tmpDir = System.getProperty("java.io.tmpdir");
            if (!tmpDir.endsWith(File.separator))
            	tmpDir = tmpDir + File.separator;
            reportFile = tmpDir + "Bottom.rep";
            cube.copyOlapFileObjectFromServer(IEssOlapFileObject.TYPE_REPORT,
                "Bottom", reportFile, false);
            System.out.println("\n\nReport Output for file: " + reportFile +
                "\n-----------------------------");
            output = cube.report(true, false, reportFile, true);
            System.out.println(output);

            // Execute a report and get the output as an iterator. This is
            // useful when report returns large resultset.
            reportFile = "Asym";
            System.out.println("\nExecuting a report to get output as an iterator....");
            System.out.println("\n\nReport Output for file: " + reportFile +
                "\n-----------------------------");
            IEssSequentialIterator iterator = cube.report(reportFile, true, false,
                true, false);
            while ((iterator.isEndOfData() == false) &&
                    ((output = iterator.getNextString()) != null))
                System.out.println(output);
        } catch (EssException x) {
            System.err.println("Error: " + x.getMessage());
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
                "Usage: java " + RunReport.class.getName() + " <user> <password> <analytic server> <provider>");
            System.exit(1); // Simply end
        }
    }
}
