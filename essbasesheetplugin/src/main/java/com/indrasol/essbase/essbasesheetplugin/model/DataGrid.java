package com.indrasol.essbase.essbasesheetplugin.model;

public class DataGrid {
    private int totalRows;
    private int totalCols;
    private String[][] dataGrid;
    private Integer[][] dataGridMetaData;


    public DataGrid() {

    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getTotalCols() {
        return totalCols;
    }

    public void setTotalCols(int totalCols) {
        this.totalCols = totalCols;
    }

    public String[][] getDataGrid() {
        return dataGrid;
    }

    public void setDataGrid(String[][] dataGrid) {
        this.dataGrid = dataGrid;
    }

    public Integer[][] getDataGridMetaData() {
        return dataGridMetaData;
    }

    public void setDataGridMetaData(Integer[][] dataGridMetaData) {
        this.dataGridMetaData = dataGridMetaData;
    }



    
}
