/*
    File: MdxQuery.java 1.1, 2006-7-19
    Copyright (c) 1991, 2007 Oracle and / or its affiliates. All rights reserved.
 */
package com.essbase.samples.japi;

import java.util.ArrayList;

import com.essbase.api.base.*;
import com.essbase.api.metadata.IEssMemberSelection;
import com.essbase.api.session.*;
//import com.essbase.api.datasource.*;
import com.essbase.api.dataquery.*;
import com.essbase.api.datasource.EssPerformAllocationError;
import com.essbase.api.datasource.IEssCube;
import com.essbase.api.datasource.IEssOlapServer;
//import com.essbase.api.metadata.*;
import com.essbase.api.domain.*;

/**
 MDX Query Example does the following: Signs on to essbase domain,
 Opens a cube view, Performs MDX query, Closes cube view, verifies 
 MDX expressions and Signs Off.

 <p>Focus of this Sample:
	- How to run MDX queries and different options 
	- How to retrieve the results and parse its content
	- How to verify MDX Expressions.
 </p>
 
 <p>In order for this sample to work in your environment, make sure to
 change the s_* variables to suit your environment. If you change s_appName
 and s_cubeName, make sure to edit the method performMdxQuery() accordingly. 
 </p>
 
 @author Abhijeet Katariya
 @version 1.1, 19 Jul 06
 */
public class MdxQuery {
	// NOTE: Change the following variables to suit your setup.
	private static String s_userName = "system";

	private static String s_password = "password";

	private static String s_olapSvrName = "localhost";

	/* Possible values for s_provider: "Embedded" or
	 * "http://localhost:13080/aps/JAPI"
	 */
	private static String s_provider = "Embedded"; // Default

	private static String s_appName = "Sample";

	private static String s_cubeName = "basic";

	private static final int FAILURE_CODE = 1;

	public static void main(String[] args) {
        int statusCode = 0;
		IEssbase ess = null;
		IEssCubeView cv = null;
		byte ch[] = new byte[2];

		try {

			acceptArgs(args);
			// Create JAPI instance.
			ess = IEssbase.Home.create(IEssbase.JAPI_VERSION);

			// Sign On to the Provider
			IEssDomain dom = ess.signOn(s_userName, s_password, false, null,
			        s_provider);

			// Use case to show execution of MDX Query
			try {
				cv = dom.openCubeView("Mdx Query Example", s_olapSvrName,
				        s_appName, s_cubeName);
				performMdxQuery(cv);
			} catch (Exception x) {
				System.out.println("Error: " + x.getMessage());
				x.printStackTrace();
				statusCode = FAILURE_CODE;
			} finally {
				// Close cube view.
				try {
					if (cv != null)
						cv.close();
				} catch (EssException x) {
					System.err.println("Error: " + x.getMessage());
				}
			}
			
			// Use case to show verification of MDX Expressions
			verifyMdxExpressions(dom);

			if (ess != null && ess.isSignedOn() == true)
				ess.signOff();
		} catch (EssException x) {
			System.err.println("Error: " + x.getMessage());
		}
		// Set status to failure only if exception occurs and do abnormal
		// termination
		// otherwise, it will by default terminate normally
		if (statusCode == FAILURE_CODE) System.exit(FAILURE_CODE);
	}

	private static void performMdxQuery(IEssCubeView cv) throws Exception {
		boolean bDataLess = false;
		boolean bNeedCellAttributes = false;
		boolean bHideData = true;
		// Just having a couple of sample MDX Queries. You may change code to
		// use any of these or try your own.
		String mdxquery = null;
		/*mdxquery = "SELECT \n"
				 + "Union(CrossJoin({[Sales], [Profit]}, {[Actual], [Budget]}), \n"
				 + "Union(CrossJoin([Total Expenses].Children, {[Actual]}), \n"
				 + "{([Opening Inventory], [Variance]), ([Additions], [Variance %])})
				 \n"
				 + ") ON COLUMNS, \n"
				 + "CrossJoin([200].Children, {[East], [West]} ) DIMENSION PROPERTIES
				 [Product].Ounces ON ROWS \n"
				 + "FROM Sample.Basic WHERE {[Jan]}";*/ // Sample query 1

		// Sample query 2
		mdxquery = "SELECT " 
		        + "CrossJoin([Measures].CHILDREN , [Market].CHILDREN) on columns, "
		        + "[Product].Levels(0).Members "
		        + "DIMENSION PROPERTIES [Product].[Intro Date] on rows "
		        + "from Sample.Basic";
		
		//mdxquery = "SELECT {[Product]} on columns, {[Market].CHILDREN} on rows, {[Measures].CHILDREN} on pages from Sample.Basic";

		// This query is built for testing the outline which has the smartlist
		// data for the specified cells.
		// Either change the query to suit your outline with smartlists or
		// update the outline
		// to have smartlist data at the specified cells to test smartlist
		// related query.
		// mdxquery = "select {([Ending Inventory],[Actual],[100-10],[New
		// York])} on columns, {[Mar],[Apr]} on rows from sample.basic";
		IEssOpMdxQuery op = cv.createIEssOpMdxQuery();

		op.setQuery(bDataLess, bHideData, mdxquery, bNeedCellAttributes,
		        IEssOpMdxQuery.EEssMemberIdentifierType.NAME);
		// Enable XMLA Mode to true if you want the MDX Results according to the
		// XMLA Standards.
		// If below statement with "false" setting is not executed, Query will
		// by default be executed in non-XMLA Mode (according to Essbase Standards).
		op.setXMLAMode(false);

		/*
		 * These 3 options fetch Text based Cell values (if any as in case of
		 * Smartlist, Date & Formatted String Based cells) This are applicable
		 * with Essbase version 11.1.1.0.0 and above with a cube that supports "Textual
		 * Measures"
		 */
		op.setNeedFormattedCellValue(true);
		op.setNeedSmartlistName(true);
		op.setNeedFormatString(false);
		op.setNeedFormattedMissingCells(true);

		/*
		 * Enable this option if you want to see Meaningless cells. Applicable
		 * with Varying Attribute based outlines and effective with Essbase 11.1.1.0.0
		 * and above only.
		 */
		op.setNeedMeaninglessCells(false);

		cv.performOperation(op);

		IEssMdDataSet mddata = cv.getMdDataSet();

		printMdDataSet(mddata, op);
		System.out
		        .println("\n------------------ MDX Result in Grid Format ----------------");
		printMdDataSetInGridForm(mddata);
		System.out
		        .println("------------------ ------------------------- ----------------");
	}

	private static void printMdDataSet(IEssMdDataSet mddata, IEssOpMdxQuery op) throws Exception {

		IEssMdAxis[] axes = mddata.getAllAxes();

		int nAxes = axes.length;

		System.out.println("Number of axes: " + nAxes);

		// Get meta info about all the axes eg number of tuples in each axis,
		// number of dimensions in each axis, and if the axis is a slicer axis.
		for (int axisInd = 0; axisInd < nAxes; axisInd++) {
			if (axes[axisInd].isSlicerAxis())
				System.out.print("Slicer ");

			System.out.println("Axis " + axisInd + "  Number Of Tuples : "
			        + axes[axisInd].getTupleCount()
			        + " Number Of Dimensions : "
			        + axes[axisInd].getDimensionCount());
		}

		// Get the meta info about the dimensions represented in each axis.
		// For each dimension in each axis, get dimension name and get
		// property names and their data types that are fetched in the result
		// set for each of the members of these dimensions.

		for (int axisInd = 0; axisInd < nAxes; axisInd++) {
			IEssMdAxis axis = axes[axisInd];
			int nTuples = axis.getTupleCount();

			// Get all the dimensions and their info in this axis
			System.out.println("\nGetting dimensions in axis : " + axisInd);
			IEssMdMember[] dims = axis.getAllDimensions();
			for (int axisDimInd = 0; axisDimInd < dims.length; axisDimInd++) {
				IEssMdMember dim = dims[axisDimInd];
				int propscnt = dim.getCountProperties();
				System.out.println("Dim " + axisDimInd + "  dim name : "
				        + dim.getName() + " #props " + propscnt);

				for (int propInd = 0; propInd < propscnt; propInd++) {
					System.out.println("Property " + propInd + " Name : "
					        + dim.getPropertyName(propInd) + ",  Type : "
					        + dim.getPropertyDataType(propInd));
				}
			}
			// Here is how you can get all the tuple members in an axis. Get all
			// the members in each tuples, get member names and all their
			// properties
			// and property values.
			System.out.println("\nGetting members in all the tuples of axis : "
			        + axisInd);
			for (int tupleInd = 0; tupleInd < nTuples; tupleInd++) {
				System.out.println("\nTuple : " + tupleInd);
				IEssMdMember[] mbrs = axis.getAllTupleMembers(tupleInd);
				printMemberInfo(mbrs);
			}
		}

		// here is how you go through all the clusters to get all the tuples.
		// First get all the clusters in each axis and then loop through them.
		// Get all the members in each cluster. You can get the members for
		// each of the dimension in the cluster or you can get the members
		// in each of the tuple in the cluster. For all the members in each
		// tuple, get member names and all their properties and property values.
		System.out.println("\n\n\nPrinting results through clusters .....");
		for (int axisInd = 0; axisInd < nAxes; axisInd++) {
			IEssMdAxis axis = axes[axisInd];

			// Get all the clusters in this axis
			IEssMdCluster[] clusters = axis.getAllClusters();
			long nClusters = axis.getClusterCount();
			System.out.println("\nAxis " + axisInd + "  Num clusters "
			        + nClusters);

			for (int clusterInd = 0; clusterInd < nClusters; clusterInd++) {
				IEssMdCluster cluster = clusters[clusterInd];
				int clusterTupleCount = cluster.getTupleCount();

				// Get the members based on the dimension index
				System.out.println("\nCluster " + clusterInd + " Size "
				        + clusterTupleCount);
				for (int dimInd = 0; dimInd < cluster.getDimensionCount(); dimInd++) {
					IEssMdMember[] mbrs = cluster
					        .getAllDimensionMembers(dimInd);
					System.out.println("Cluster Dim " + dimInd
					        + " Number Of Members : " + mbrs.length);
				}
				// Get the members based on tuple index
				System.out
				        .println("\nGetting members in all the tuples of this cluster...");
				for (int tupleInd = 0; tupleInd < clusterTupleCount; tupleInd++) {
					System.out.println("\nTuple : " + tupleInd);
					printMemberInfo(cluster.getAllTupleMembers(tupleInd));
				}
			}
		}

		// Now get all the data cells in the result set. For each cell,
		// check whether the cell is missing or no access cell and then
		// get the double value of the cell.
		System.out.println("\n\n\nGetting all the cells...");
		System.out.println("Number of cells : " + mddata.getCellCount());

		for (int ulCellOrdinal = 0; ulCellOrdinal < mddata.getCellCount(); ulCellOrdinal++) {
			if (!op.isNeedFormattedMissingCells() && mddata.isMissingCell(ulCellOrdinal)) {
				System.out.println("Cell[" + ulCellOrdinal + "] = #Missing");
			} else if (mddata.isNoAccessCell(ulCellOrdinal)) {
				System.out.println("Cell[" + ulCellOrdinal + "] = #NoAccess");
			} else {
				printCellValue(mddata, ulCellOrdinal, op);
			}
		}
	}
    
	private static void printCellValue(IEssMdDataSet mddata, int cellOrdinal, IEssOpMdxQuery op)
	        throws EssException {
		
		String actualVal = (mddata.isMissingCell(cellOrdinal)) 
							? "#Missing" : "" + mddata.getCellValue(cellOrdinal);
		// Will contatin Smarlist text name if cell type is Smartlist
		// else if it is date, then, contains the formatted date string
		// else may contain a formatted text if its Double type of cell.
		String fmtdCellTxtVal = mddata.getFormattedValue(cellOrdinal);
		int cellType = mddata.getCellType(cellOrdinal);
		if (fmtdCellTxtVal != null && fmtdCellTxtVal.length() > 0) {
			String printSmlistName = (cellType == IEssMdDataSet.CELLTYPE_SMARTLIST) 
								? (", SmartlistName = " +  mddata.getSmartListName(cellOrdinal))
								: "";
			String printNumericValue = (!mddata.isMissingCell(cellOrdinal)) 
								? (", NumericValue = " + actualVal)
								: "";
			System.out.print("Cell[" + cellOrdinal + "] = " + fmtdCellTxtVal
			        + " {CellType = " + cellType + printNumericValue
			        + printSmlistName
			        + "}");
		} else {
			System.out.print("Cell[" + cellOrdinal + "] = " + actualVal);
		}
		
		if (op.isNeedFormatString()) {
			if (cellType == IEssMdDataSet.CELLTYPE_DATE
			        || cellType == IEssMdDataSet.CELLTYPE_DOUBLE) {
				System.out.print(" [FormatString = "
				        + mddata.getFormatString(cellOrdinal) + "]");
			}
		}
		System.out.println();
	}
	
	/* Prints the MDX result in a Grid form (separator used is tab char)
	 * This example handles any result set having at least 
	 * a ROW and COLUMN axis and upto PAGE axis.
	 */ 
	private static void printMdDataSetInGridForm(IEssMdDataSet mddata)
			throws Exception {
		IEssMdAxis[] axes = mddata.getAllAxes();

		IEssMdAxis slicerOrPovAxis = null;
		IEssMdAxis columnAxis = null;
		IEssMdAxis rowAxis = null;
		IEssMdAxis pageAxis = null;

		// If slicer axis is present then it will be 1st axis.
		int axisIndex = 0;
		if (axes[axisIndex].isSlicerAxis()) {
			slicerOrPovAxis = axes[axisIndex++];
		}
		columnAxis = axes[axisIndex++];

		// Check if row axis are part of result...
		if (axisIndex < axes.length) { // row axis is present.
			rowAxis = axes[axisIndex++];
		}
		// Check if page axis are part of result...
		if (axisIndex < axes.length) { // page axis is present.
			pageAxis = axes[axisIndex];
		}

		if (slicerOrPovAxis != null) {
			IEssMdMember[] mem = slicerOrPovAxis.getAllTupleMembers(0);
			String sTuple = "(";
			int i = 0;
			for (i = 0; i < mem.length - 1; i++) {
				sTuple += "[" + mem[i].getName() + "], ";
			}
			sTuple += "[" + mem[i].getName() + "])";

			System.out.println("Slicer(POV) Tuple: " + sTuple);
		}

		if (pageAxis != null) {
			/*
			 * Identify #of Pages, i.e., #of Tupes in PageAxis. For every Page,
			 * print the Grid belonging to that page.
			 */
			for (int i = 0; i < pageAxis.getTupleCount(); i++) {
				// Print Page Header.
				String pageHeader = "------- PAGE " + i + ", Tuple: (";
				String pageFooter = "------- PAGE " + i + " ends -------";
				IEssMdMember[] mem = pageAxis.getAllTupleMembers(i);
				int j;
				for (j = 0; j < mem.length - 1; j++) {
					pageHeader += "[" + mem[j].getName() + "], ";
				}
				pageHeader += "[" + mem[j].getName() + "]) --------";

				System.out.println(pageHeader);
				printMdDataSetInGridForm_Rows_Columns(mddata, columnAxis,
						rowAxis, i);
				System.out.println(pageFooter);
			}
		} else if (rowAxis != null) {
			printMdDataSetInGridForm_Rows_Columns(mddata, columnAxis, rowAxis, 0);
		}
	}

	private static void printMdDataSetInGridForm_Rows_Columns(
			IEssMdDataSet mddata, IEssMdAxis columnAxis, IEssMdAxis rowAxis,
			int pageNum) throws EssException {
		int cols = columnAxis.getTupleCount();
		int rows = rowAxis.getTupleCount();
		if (cols <= 0 || rows <= 0) {
			System.out.println("This Sample has limited support for printing this MDX result in Grid form " +
					"because this has "+cols+" columns & "+rows+" rows.");
			System.out.println("Note: If there no rows and 'n' number of columns, you can still print result in table format by modifying this sample code."); 
			return;
		}
		IEssMdMember[] mem = rowAxis.getAllTupleMembers(0);
		for (int i = 0; i < mem.length; i++)
			System.out.print("\t");
		for (int j = 0; j < cols; j++) {
			mem = columnAxis.getAllTupleMembers(j);
			printTuple(mem);
		}
		System.out.println();
		int k = pageNum * cols * rows;
		for (int i = 0; i < rows; i++) {
			mem = rowAxis.getAllTupleMembers(i);
			printTuple(mem);
			for (int l = 0; l < mem.length; l++) {
				int propcnt = mem[l].getCountProperties();
				for (int propInd = 0; propInd < propcnt; propInd++) {
					IEssValueAny propval = mem[l].getPropertyValueAny(propInd);
					System.out.print(mem[l].getPropertyName(propInd) + ", "
							+ propval + " ");
				}
			}
			for (int j = 0; j < cols; j++) {
				if (mddata.isMissingCell(k)) {
					System.out.print("#Missing\t");
				} else if (mddata.isNoAccessCell(k)) {
					System.out.print("#NoAccess\t");
				} else {
					String fmtdCellTxtVal = mddata.getFormattedValue(k);
					if (fmtdCellTxtVal != null && fmtdCellTxtVal.length() > 0) {
						System.out.print(fmtdCellTxtVal + "\t");
					} else {
						double val = mddata.getCellValue(k);
						System.out.print(val + "\t");
					}
				}
				k++;
			}
			System.out.println();
		}
	}	

	private static void printTuple(IEssMdMember[] mem) throws EssException {
		System.out.print("(");
		for (int i = 0; i < mem.length - 1; i++)
			System.out.print(mem[i].getName() + ", ");
		System.out.print(mem[mem.length - 1].getName() + ")\t");

	}

	private static void printMemberInfo(IEssMdMember[] members)
	        throws EssException {
		for (int mbrInd = 0; mbrInd < members.length; mbrInd++) {
			IEssMdMember member = members[mbrInd];

			System.out.println("Mbr " + mbrInd + " identifier "
			        + member.getName());
			int propcnt = member.getCountProperties();
			for (int propInd = 0; propInd < propcnt; propInd++) {
				IEssValueAny propval = member.getPropertyValueAny(propInd);
				System.out.println("Property " + propInd + " Name : "
				        + member.getPropertyName(propInd) + ",  Type : "
				        + member.getPropertyDataType(propInd) + ",  Value : "
				        + propval);
			}
		}
	}
	
	public static void verifyMdxExpressions(IEssDomain dom) {
		IEssOlapServer esbServer = null;
		try {
			esbServer = dom.getOlapServer(s_olapSvrName);
			esbServer.connect();
			IEssCube cube = esbServer.getApplication(s_appName).getCube(s_cubeName);
			
			// Case 1: Valid Member Expression 
			verifyMDXExpr(cube, "Ancestor ( [New York], [Market].levels(2) )", 
											IEssCube.ESS_ASO_VERIFY_MEMBER_EXPR);
			
			// Case 2: Invalid Member Expression 
			verifyMDXExpr(cube, "[Produc]", IEssCube.ESS_ASO_VERIFY_MEMBER_EXPR);
			
			// Case 3: Valid Tuple Expression
			verifyMDXExpr(cube, "(Market, Scenario)", IEssCube.ESS_ASO_VERIFY_TUPLE_EXPR);
			
			// Case 4: Invalid Tuple Expression
			verifyMDXExpr(cube, "[Market].Children", IEssCube.ESS_ASO_VERIFY_TUPLE_EXPR);
			
			// Case 5: Valid Set Expression
			verifyMDXExpr(cube, "[Market].Children", IEssCube.ESS_ASO_VERIFY_SET_EXPR);
			
			// Case 6: Invalid Set Expression
			verifyMDXExpr(cube, "(product, jan)", IEssCube.ESS_ASO_VERIFY_SET_EXPR);
			
			// Case 7: Valid Numeric Expression 
			verifyMDXExpr(cube, "Avg ( [Market].[California]:[Market].[Nevada], [Measures].[Sales])", IEssCube.ESS_ASO_VERIFY_NUMERIC_EXPR);
			
			// Case 8: Invalid Numeric Expression
			verifyMDXExpr(cube, "Abs([Scenario].Children)", IEssCube.ESS_ASO_VERIFY_NUMERIC_EXPR);
			
			// Case 9: Valid Set Expression, but, has Warnings  
			verifyMDXExpr(cube, "subset(Scenario.Members, -1, 10)", IEssCube.ESS_ASO_VERIFY_SET_EXPR);
		}
		catch (EssException e) {
			System.out.println("[verifyMdxExpressions] Essbase/Provider Error: " + e.getMessage());
		}
		catch (Throwable t) {
			System.err.println("[verifyMdxExpressions] Unexpected Error. ERROR: " + t);
		}
		finally {
			try {
				if (esbServer != null && esbServer.isConnected()) {
					esbServer.disconnect();
				}
			} catch (EssException e) {}
		}
	} 
	
	private static void verifyMDXExpr(IEssCube cube, String mdxExpr, long exprType) {
		ArrayList errorList = new ArrayList();
		System.out.println("Running - verifyMDXExpression(\""+mdxExpr+"\", "+exprType+")");
		try {
			boolean success = cube.verifyMDXExpression(mdxExpr, exprType, errorList);
			if (success) {
				System.out.println("This is a valid expression.");
			}
			else {
				System.out.println("MDX Expression has warnings. Details: ");
				for (int i = 0; i < errorList.size(); i++) {
					System.out.println(perfAllocErrorToString(
										(EssPerformAllocationError)errorList.get(i)) );
				}
			}
		}
		catch (EssException e) {
			if (!errorList.isEmpty()) {
				System.out.println("MDX Expression incorrect. Error: " + e.getMessage());
				System.out.println("Error Details: ");
				for (int i = 0; i < errorList.size(); i++) {
					System.out.println(perfAllocErrorToString(
										(EssPerformAllocationError)errorList.get(i)) );
				}
			}
			else {
				System.out.println("Possible Failure while running verifyMDXExpression. Error: " + e.getMessage());
			}
		}
		System.out.println("verifyMDXExpression() Completed!" 
						+"\n--------------------------------");
	}
	
	private static String perfAllocErrorToString(EssPerformAllocationError aError) {
		return "(Message #"+aError.getMessageNumber()+")Error at Line "
				+ aError.getLineNumber() + " with argument " + aError.getArgument() + ", token=" + aError.getToken();
	}

	static void acceptArgs(String[] args) throws EssException {
		if (args.length >= 4) {
			s_userName = args[0];
			s_password = args[1];
			s_olapSvrName = args[2];
			s_provider = args[3]; //PROVIDER
		} else if (args.length != 0) {
			System.err.println("ERROR: Incorrect Usage of this sample.");
			System.err.println("Usage: java " + MdxQuery.class.getName()
			        + " <user> <password> <analytic server> <provider>");
			System.exit(1); // Simply end
		}
	}
}
