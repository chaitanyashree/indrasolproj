/*
    File: RunXmlReport.java 1.1, 04/04/2001
    Copyright (c) 1991, 2007 Oracle and / or its affiliates. All rights reserved.
 */
package com.essbase.samples.japi;

import com.essbase.api.base.*;
import com.essbase.api.session.*;
import com.essbase.api.datasource.*;
//import com.essbase.api.dataquery.*;
import com.essbase.api.domain.*;
//import com.essbase.api.metadata.*;

/**
    RunXmlReport Example does the following: Signs on to essbase domain,
    Runs a report and Signs Off.

    In order for this sample to work in your environment, make sure to
    change the s_* variables to suit your environment.

    @author Srini Ranga
    @version 1.1, 19 Jul 06
 */
public class RunXmlReport {
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

            // Create API instance.
            ess = IEssbase.Home.create(IEssbase.JAPI_VERSION);

            // Sign On to the Provider
            IEssDomain dom 
            = ess.signOn(s_userName, s_password, false, null, s_provider);

            // Open connection with OLAP server and get the cube.
            olapSvr = (IEssOlapServer)dom.getOlapServer(s_olapSvrName);
            olapSvr.connect();

            IEssCube cube = olapSvr.getApplication("Sample").getCube("Basic");
            executeReport(cube);
            System.out.println("XML Report execution sample compteled.");
        } catch (EssException x) {
            System.out.println("Error: " + x.getMessage());
            statusCode = FAILURE_CODE;
        } finally {
            // Close OLAP server connection and sign off from the domain.
            try {
                if (olapSvr != null && olapSvr.isConnected() == true)
                    olapSvr.disconnect();
            } catch (EssException x) {
                System.out.println("Error: " + x.getMessage());
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
          String repSpec = "  <PAGE (Measures, Market) " +
          " South Sales " +
          " <SYM " +
          //" <Outxml " +
          " <COLUMN (Scenario, Year) " +
          " Actual Budget " +
          " Jan Feb " +
          " <IDESC \"100\" " +
          " ! ";
            System.out.println("\n\nReport Output for: "+repSpec+"\n----------------"+
                "------------------------------------------------------------");
            String output = cube.report(repSpec, true, false);
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
                "Usage: java " + RunXmlReport.class.getName() + " <user> <password> <analytic server> <provider>");
            System.exit(1); // Simply end
        }
    }
}
