package com.indrasol.essbase.essbasesheetplugin.util;

import java.util.ArrayList;
import java.util.List;

import com.essbase.api.base.EssException;
import com.essbase.api.base.IEssBaseObject;
import com.essbase.api.dataquery.*;
import com.essbase.api.metadata.IEssCubeOutline;
import com.essbase.api.metadata.IEssDimension;
import com.essbase.api.metadata.IEssMember;

import com.essbase.api.session.IEssbase;
import com.indrasol.essbase.essbasesheetplugin.model.DataGrid;
import org.springframework.stereotype.Component;

@Component
public class EssbaseUtil {

	public static List<IEssDimension> getDimensionsAsList(IEssCubeOutline cubeOutLine) throws EssException {
		List<IEssDimension> dimenionsList = new ArrayList<IEssDimension>();
		IEssBaseObject[] dimensions = cubeOutLine.getDimensions().getAll();
		for (int i = 0; i < dimensions.length; i++) {
			IEssDimension dimension = (IEssDimension) dimensions[i];
			dimenionsList.add(dimension);
			System.out.println("Dimensions:" + dimension.getName());
		}
		return dimenionsList;
	}

	public static List<IEssMember> getMembers(IEssDimension dimension) throws EssException {
		List<IEssMember> membersList = new ArrayList<IEssMember>();
		IEssBaseObject[] members = dimension.getDimensionRootMember().getChildMembers().getAll();
		for (int i = 0; i < members.length; i++) {
			IEssMember member = (IEssMember) members[i];
			membersList.add(member);
			System.out.println("member:" + member.getName());
		}
		return membersList;
	}

	public static List<IEssMember> getSubMembers(IEssMember member) throws EssException {
		List<IEssMember> membersList = new ArrayList<IEssMember>();
		IEssBaseObject[] members = member.getChildMembers().getAll();
		for (int i = 0; i < members.length; i++) {
			IEssMember subMember = (IEssMember) members[i];
			membersList.add(subMember);
			System.out.println("Submember:" + subMember.getName());
		}
		return membersList;
    }

    public static DataGrid performZoomInViewOperations(IEssCubeView cv,
													   DataGrid dataGrid, int startRow, int startColumn) throws EssException {
		String[][] gridView = new String[][]{};
		Integer[][] gridMetaData = new Integer[][]{};
		DataGrid updatedGrid = new DataGrid();
		//int startRow=1,startColumn=0;
		// Create a grid view with the input for the operation.
		IEssGridView grid = cv.getGridView();
        grid.setSize(dataGrid.getTotalRows(), dataGrid.getTotalCols());
        gridView = dataGrid.getDataGrid();

        for(int r=0; r<gridView.length; r++) {
            for(int c=0; c<gridView[r].length; c++){
                grid.setValue(r,c,gridView[r][c]);
                //System.out.println(gridView[r][c]);
            }
        }

		IEssOperation op = null;

		op = cv.createIEssOpZoomIn();
        IEssOpZoomIn opCzi  = ((IEssOpZoomIn)op);
        //opCzi.addRange(startRow, startColumn, dataGrid.getTotalRows(), dataGrid.getTotalCols());
        //opCzi.setPreference(true, IEssOpZoomIn.EEssZoomInPreference.BOTTOM_LEVEL);
		//((IEssOpZoomIn)op).addRange(startRow, startColumn,  grid.getCountRows(), grid.getCountColumns());
        opCzi.addCell(startRow, startColumn);

		// Perform the operation.
		cv.performOperation(opCzi);
		// Get the result and print the output.
		int cntRows = grid.getCountRows(), cntCols = grid.getCountColumns();
		gridView= new String[cntRows][cntCols];
		gridMetaData = new Integer[cntRows][cntCols];
        updatedGrid.setTotalRows(cntRows);
        updatedGrid.setTotalCols(cntCols);
		System.out.print("Query Results for the Operation: Zoom In"  + "\n" +
				"-----------------------------------------------------\n");
		for (int i = 0; i < cntRows; i++) {
			for (int j = 0; j < cntCols; j++) {
				System.out.print(grid.getValue(i, j) + "\t");
				gridView[i][j] = grid.getValue(i, j).toString();
				//String dt = grid.getFormattedValue(i,j);
				//System.out.print("\t|"+dt+"|");
				gridMetaData[i][j] = grid.getCellType(i, j).intValue();
			}
			System.out.println();
		}
		System.out.println("\n");
		updatedGrid.setDataGrid(gridView);
		updatedGrid.setDataGridMetaData(gridMetaData);
		return updatedGrid;


	}

	public static DataGrid performZoomOutViewOperations(IEssCubeView cv,
													   DataGrid dataGrid, int startRow, int startColumn) throws EssException {
		String[][] gridView = new String[][]{};
		Integer[][] gridMetaData = new Integer[][]{};
		DataGrid updatedGrid = new DataGrid();
		//int startRow=1,startColumn=0;
		// Create a grid view with the input for the operation.
		IEssGridView grid = cv.getGridView();
		//grid.setSize(1,1);
		grid.setSize(dataGrid.getTotalRows(), dataGrid.getTotalCols());

		System.out.println("totalRows=>"+dataGrid.getTotalRows()+"\t"+dataGrid.getTotalCols());
		
		gridView = dataGrid.getDataGrid();
		gridMetaData = dataGrid.getDataGridMetaData();
		System.out.println("totalRows=>"+dataGrid.getTotalRows()+"\t"+dataGrid.getTotalCols());
		System.out.println("gridMetaData.rows=>"+gridMetaData.length);

        for(int r=0; r<gridView.length; r++) {
            for(int c=0; c<gridView[r].length; c++){
				if(gridMetaData[r][c] == IEssCell.EEssCellType.MEMBER.intValue()) {
					grid.setValue(r,c,gridView[r][c]);
				}
                
                //System.out.println(gridView[r][c]);
            }
        }
		System.out.println("@@@@totalRows=>"+dataGrid.getTotalRows()+"\t"+dataGrid.getTotalCols());
		IEssOperation op = null;


		op = cv.createIEssOpZoomOut();
		

        IEssOpZoomOut opCzo  = ((IEssOpZoomOut)op);
        //opCzi.addRange(startRow, startColumn, dataGrid.getTotalRows(), dataGrid.getTotalCols());
        //opCzi.setPreference(true, IEssOpZoomIn.EEssZoomInPreference.BOTTOM_LEVEL);
		//((IEssOpZoomIn)op).addRange(startRow, startColumn,  grid.getCountRows(), grid.getCountColumns());
		//opCzo.addRange(startRow, startColumn,  1, 1);
        opCzo.addCell(startRow, startColumn);

		// Perform the operation.
		cv.performOperation(opCzo);
		// Get the result and print the output.
		int cntRows = grid.getCountRows(), cntCols = grid.getCountColumns();
		System.out.println("cntRows="+cntRows+"\tcntCols="+cntCols);
		gridView= new String[cntRows][cntCols];
		gridMetaData = new Integer[cntRows][cntCols];
        updatedGrid.setTotalRows(cntRows);
        updatedGrid.setTotalCols(cntCols);
		System.out.print("Query Results for the Operation: Zoom Out"  + "\n" +
				"-----------------------------------------------------\n");
		for (int i = 0; i < cntRows; i++) {
			for (int j = 0; j < cntCols; j++) {
				System.out.print(grid.getValue(i, j) + "\t");
				gridView[i][j] = grid.getValue(i, j).toString();
				//String dt = grid.getFormattedValue(i,j);
				//System.out.print("\t|"+dt+"|");
				gridMetaData[i][j] = grid.getCellType(i, j).intValue();
				

			}
			System.out.println();
		}
		System.out.println("\n");
		updatedGrid.setDataGrid(gridView);
		updatedGrid.setDataGridMetaData(gridMetaData);
		
		return updatedGrid;


	}


	public static DataGrid performCubeViewOperation(IEssCubeView cv,
													String opStr) throws EssException {
		String[][] gridView = new String[][]{};
		Integer[][] gridMetaData = new Integer[][]{};
		DataGrid dataGrid = new DataGrid();
		// Create a grid view with the input for the operation.
		IEssGridView grid = cv.getGridView();
//		grid.setSize(3, 5);
//		grid.setValue(0, 2, "Product");
//		grid.setValue(0, 3, "Market");
//		grid.setValue(1, 2, "Jan"); ;
//		grid.setValue(1, 3, "Feb");
//		grid.setValue(1, 4, "Mar");
//		grid.setValue(2, 0, "Actual");
//		grid.setValue(2, 1, "Sales");

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
			return dataGrid;
		} else if (opStr.equals("reportFile_with_no_parsing")) {
			op = cv.createIEssOpReport();
			((IEssOpReport)op).set("Actsales", false, false);
			((IEssOpReport)op).setNoParsing(true);
			cv.performOperation(op);
			System.out.print("Query Results for the Operation: " + opStr + "\n" +
					"-----------------------------------------------------\n");
			System.out.println(grid.toString());
			return dataGrid;
		} else if (opStr.equals("retrieve")) {
			op = cv.createIEssOpRetrieve();
		} else if (opStr.equals("zoomIn")) {
			op = cv.createIEssOpZoomIn();
			((IEssOpZoomIn)op).addRange(0, 3, 1, 1);
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
		gridView= new String[cntRows][cntCols];
		gridMetaData = new Integer[cntRows][cntCols];

		dataGrid.setTotalRows(cntRows);
		dataGrid.setTotalCols(cntCols);
		System.out.print("Query Results for the Operation: " + opStr + "\n" +
				"-----------------------------------------------------\n");
		for (int i = 0; i < cntRows; i++) {
			for (int j = 0; j < cntCols; j++) {
				System.out.print(grid.getValue(i, j) + "\t");
				gridView[i][j] = grid.getValue(i, j).toString();
				gridMetaData[i][j] = grid.getCellType(i, j).intValue();
			}
			System.out.println();
		}
		System.out.println("\n");
		dataGrid.setDataGrid(gridView);
		dataGrid.setDataGridMetaData(gridMetaData);
		return dataGrid;
	}


}
