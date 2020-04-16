package com.essbase.samples.japi;

import com.essbase.api.base.EssException;
import com.essbase.api.base.IEssValueAny;
import com.essbase.api.dataquery.IEssCubeView;
import com.essbase.api.dataquery.IEssGridView;
import com.essbase.api.dataquery.IEssOpPivot;
import com.essbase.api.dataquery.IEssOpRetrieve;
import com.essbase.api.dataquery.IEssOpUpdate;
import com.essbase.api.domain.IEssDomain;
import com.essbase.api.session.IEssbase;

public class Pivot {
    // NOTE: Change the following variables to suit your setup.
    private static String s_userName = "admin";
    private static String s_password = "password";

    private static String s_olapSvrName = "localhost";

    /* Possible values for s_provider:
        "Embedded" or "http://localhost:13080/aps/JAPI" */
    private static String s_provider = "Embedded"; // Default

    private static String s_appName = "Sample";
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

            performPivot(ess, cv);
            cv.close();

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

    static void performPivot(IEssbase ess, IEssCubeView cv)
            throws EssException {
        // Performs a retrieve, then locks and updates (increments) a cell
        // value or just sets a cell value if #missing, then unlocks and
        // retrieves the result and prints it.

        IEssGridView grid = cv.getGridView();
        grid.setSize(8, 6);
        grid.setValue(0, 2, "Year");
        grid.setValue(0, 3, "Market");
        grid.setValue(1, 2, "Measures"); ;
        grid.setValue(2, 0, "Scenario");
        grid.setValue(2, 1, "100");
        grid.setValue(3, 0, "Scenario");
        grid.setValue(3, 1, "200");
        grid.setValue(4, 0, "Scenario");
        grid.setValue(4, 1, "300");
        grid.setValue(5, 0, "Scenario");
        grid.setValue(5, 1, "300");
        grid.setValue(6, 0, "Scenario");
        grid.setValue(6, 1, "Diet");
        grid.setValue(7, 0, "Scenario");
        grid.setValue(7, 1, "Product");
        int cntRows,cntCols ;

        IEssOpRetrieve opret=cv.createIEssOpRetrieve();
        cv.performOperation(opret);
        System.out.print("Query Results for the Operation: Retrieve after an Update\n" +
                "---------------------------------------------------------\n");
        cntRows = grid.getCountRows(); cntCols = grid.getCountColumns();
        for (int i = 0; i < cntRows; i++) {
            for (int j = 0; j < cntCols; j++)
                System.out.print(grid.getValue(i, j) + "\t");
            System.out.println();
        }
        System.out.println("\n");

        IEssOpPivot op = cv.createIEssOpPivot();
        op.set(2, 1, 0, 3);
         //op.setaddnlpovmbrsforpivot(new String[]{"200","300","diet"});

        cv.performOperation(op); // Retrieve
        op.set(2, 2, 0, 2);

           cv.performOperation(op);
        // since the cube is not loaded with data by default, this might not have a double value.
        // grid.getDoubleValue(2, 2);
        System.out.println("\nPivot on 2nd-row, 1st-column: " + grid.getValue(2,0).toString());

        cntRows = grid.getCountRows(); cntCols = grid.getCountColumns();
        System.out.print("Query Results for the Operation: Retrieve after an Update\n" +
                "---------------------------------------------------------\n");
        for (int i = 0; i < cntRows; i++) {
            for (int j = 0; j < cntCols; j++)
                System.out.print(grid.getValue(i, j) + "\t");
            System.out.println();
        }
        if(grid.getAdditionalMembersForPivot() != null)
            for(String s:grid.getAdditionalMembersForPivot())

                System.out.println("The additional members are :" +s);
        System.out.println("\n");
        op.set(0, 2, 2, 0);
        cv.performOperation(op);
        System.out.println("\nPivot on 2nd-row, 1st-column: " + grid.getValue(2,0).toString());

        cntRows = grid.getCountRows(); cntCols = grid.getCountColumns();
        System.out.print("Query Results for the Operation: Retrieve after an Update\n" +
                "---------------------------------------------------------\n");
        for (int i = 0; i < cntRows; i++) {
            for (int j = 0; j < cntCols; j++)
                System.out.print(grid.getValue(i, j) + "\t");
            System.out.println();
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
                    "Usage: java " + Pivot.class.getName() + " <user> <password> <analytic server> <provider>");
            System.exit(1); // Simply end
        }
    }
}