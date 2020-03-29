/*
    File: ExecuteMaxl.java 1.1,
    Copyright (c) 1991, 2009 Oracle and / or its affiliates. All rights reserved.
 */
package com.essbase.samples.japi;

import java.util.ArrayList;

import com.essbase.api.base.*;
import com.essbase.api.session.*;
import com.essbase.api.datasource.IEssMaxlResultSet;
import com.essbase.api.datasource.IEssMaxlSession;
import com.essbase.api.datasource.IEssOlapServer;
import com.essbase.api.domain.*;

/**
    ExecuteMaxl example signs on to Analytic Server, execute Maxl statements and signs off.

    In order for this sample to work in your environment, make sure to
    change the static variables to suit your environment.

    @author Satish Ramanavarapu
 */
public class ExecuteMaxl {
    // NOTE: Change the following variables to suit your setup.
    private static String s_userName = "system";
    private static String s_password = "password";
    
    private static String s_olapSvrName = "localhost";
    /* Possible values for s_provider: 
        "Embedded" or "http://localhost:13080/aps/JAPI" */
    private static String s_provider = "Embedded"; // Default
    
    private static final int FAILURE_CODE = 1;
    
    private static String[] maxl_statements = {
    	"display system",
    	"display user",
    	"create or replace user maxluser1 identified by 'password'",
    	"drop user maxluser1",
    	"execute calculation default on sample.basic",
    	"export database sample.basic using report_file 'asym.rep' to data_file 'exp_input.exp'",
    	"export database sample.basic data to data_file 'maxlout.txt'",
    	"import database sample.basic data from data_file 'maxlout.txt' on error abort",
    	"query database sample.basic get dbstats data_block",
    	"refresh custom definitions on application sample",
    	"display application all",
//    	"display database on application jp\u304D"
    };
    
    public static void main(String[] args) {
        int statusCode = 0; // will set this to FAILURE only if err/exception occurs.
        IEssbase ess = null;
        IEssMaxlSession maxlSess = null;
        try {
            acceptArgs(args);
            // Create JAPI instance.
            ess = IEssbase.Home.create(IEssbase.JAPI_VERSION);
            // Sign On to the Provider
            IEssDomain dom = ess.signOn(s_userName, s_password, false, null, s_provider);
            IEssOlapServer olapSvr = dom.getOlapServer(s_olapSvrName);
            olapSvr.connect();
            
            maxlSess = olapSvr.openMaxlSession("Maxl Test");
            // for older version, replace above line with below
            // maxlSess = dom.openMaxlSession("Maxl Test", s_olapSvrName);
            
            for (int i =0; i < maxl_statements.length; i++) {
            	System.out.println("\nExecuting Statement: "+maxl_statements[i]);
            	try {
            		boolean bMaxlExecuted = maxlSess.execute(maxl_statements[i]);

            		if (bMaxlExecuted) {
            			IEssMaxlResultSet resultSet = maxlSess.getResultSet();
            			printvalues(resultSet);
            			printMessages(maxlSess.getMessages());	// comment out for older version
            		}
            	}
            	catch (EssException e) {
            		printMessages(maxlSess.getMessages());	// comment out for older version
            		System.out.println(e.getMessage());
            	}
            }
            
		} catch (EssException x) {
            System.err.println("Error: " + x.getMessage());
            statusCode = FAILURE_CODE;
        } finally {
            // Sign off.
            try {
            	if (maxlSess != null) maxlSess.close();
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
    
    
    // --- Internal usage methods --- //
    private static void printvalues(IEssMaxlResultSet rs) throws EssException {
        if (rs == null)
            return;

        StringBuffer buf1 = new StringBuffer();
        StringBuffer buf2 = new StringBuffer();
        String       col;

        for(int j = 1; j <= rs.getColumnCount(); j++) {
            col = rs.getColumnName(j);

            buf1.append(col);
            fillBuf(buf2, '-', col.length());

            buf1.append(" ");
            buf2.append(" ");
        }

        System.out.println("\n"+buf1.toString());
        System.out.println(buf2.toString());

        buf2.setLength(0);

        for(; rs.next(); ) {
            buf1.setLength(0);

            for(int j = 1; j <= rs.getColumnCount(); j++) {
                String colVal = rs.getString(j);
                if (colVal == null) col = "";
                else col = colVal;
                buf1.append(col);

                if (rs.getColumnName(j).length() > col.length()) {
                    fillBuf(buf1, ' ', rs.getColumnName(j).length() - col.length());
                }

                buf1.append(" ");
            }

             System.out.println(buf1.toString());
        }
    }
    
    private static void printMessages(ArrayList messages) {
    	if (messages == null) return;
    	System.out.println();
    	for (int i = 0; i < messages.size(); i++) 
    		System.out.println((String)messages.get(i));
    }
    
    private static void fillBuf(StringBuffer buf, char ch, int times) {
        try {
            for(int k = 0; k < times;k++) {
                buf.append(ch);
            }
        }
        catch (Exception e) {
        }
    }

    // TODO : Modify this method to test all IEssMaxlSession.get*() methods
    private static void printvaluesSpecial(IEssMaxlResultSet rs) throws EssException {
        if (rs == null)
            return;

        StringBuffer buf1 = new StringBuffer();
        StringBuffer buf2 = new StringBuffer();
        String       col;

        for(int j = 1; j <= rs.getColumnCount(); j++) {
            col = rs.getColumnName(j);

            buf1.append(col);
            fillBuf(buf2, '-', col.length());

            buf1.append(" ");
            buf2.append(" ");
        }

        System.out.println(buf1.toString());
        System.out.println(buf2.toString());

        buf2.setLength(0);

        for(; rs.next(); ) {
            buf1.setLength(0);

            for(int j = 1; j <= rs.getColumnCount(); j++) {
                col = rs.getString(j);
                buf1.append(col);

                if (rs.getColumnName(j).length() > col.length()) {
                    fillBuf(buf1, ' ', rs.getColumnName(j).length() - col.length());
                }

                buf1.append(" ");
            }

             System.out.println(buf1.toString());
        }
    }

    static void acceptArgs(String[] args) throws EssException {
        if (args.length >= 3) {
            s_userName = args[0];
            s_password = args[1];   
            s_olapSvrName = args[2];
            s_provider = args[3]; //PROVIDER
        } else if (args.length != 0) {
            System.err.println("ERROR: Incorrect Usage of this sample.");
            System.err.println(
                "Usage: java " + ExecuteMaxl.class.getName() + " <user> <password> <analytic server> <provider>");
            System.exit(FAILURE_CODE); // Simply end
        }
    }
}
