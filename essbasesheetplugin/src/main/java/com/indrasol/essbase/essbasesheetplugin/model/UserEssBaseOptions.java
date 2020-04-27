package com.indrasol.essbase.essbasesheetplugin.model;

import org.springframework.stereotype.Component;

@Component
public class UserEssBaseOptions {

    private boolean repeatLabel;
    private boolean suppressMissingRows;
    private boolean suppressZeroRows;
    private boolean suppressMissingColumns;
    private boolean suppressZeroColumns;
    private boolean preseveFormatting;
    private boolean adjustColumnWidth;
    private String indentationGroupVal;
    private String noDataMissingInputVal;
    private String noAccessInputVal;
    private int undoRedoCountInputVal;

    private String userId;

    public boolean isRepeatLabel() {
        return repeatLabel;
    }

    public void setRepeatLabel(boolean repeatLabel) {
        this.repeatLabel = repeatLabel;
    }

    public boolean isSuppressMissingRows() {
        return suppressMissingRows;
    }

    public void setSuppressMissingRows(boolean suppressMissingRows) {
        this.suppressMissingRows = suppressMissingRows;
    }

    public boolean isSuppressZeroRows() {
        return suppressZeroRows;
    }

    public void setSuppressZeroRows(boolean suppressZeroRows) {
        this.suppressZeroRows = suppressZeroRows;
    }

    public boolean isSuppressMissingColumns() {
        return suppressMissingColumns;
    }

    public void setSuppressMissingColumns(boolean suppressMissingColumns) {
        this.suppressMissingColumns = suppressMissingColumns;
    }

    public boolean isSuppressZeroColumns() {
        return suppressZeroColumns;
    }

    public void setSuppressZeroColumns(boolean suppressZeroColumns) {
        this.suppressZeroColumns = suppressZeroColumns;
    }

    public boolean isPreseveFormatting() {
        return preseveFormatting;
    }

    public void setPreseveFormatting(boolean preseveFormatting) {
        this.preseveFormatting = preseveFormatting;
    }

    public boolean isAdjustColumnWidth() {
        return adjustColumnWidth;
    }

    public void setAdjustColumnWidth(boolean adjustColumnWidth) {
        this.adjustColumnWidth = adjustColumnWidth;
    }

    public String getIndentationGroupVal() {
        return indentationGroupVal;
    }

    public void setIndentationGroupVal(String indentationGroupVal) {
        this.indentationGroupVal = indentationGroupVal;
    }

    public String getNoDataMissingInputVal() {
        return noDataMissingInputVal;
    }

    public void setNoDataMissingInputVal(String noDataMissingInputVal) {
        this.noDataMissingInputVal = noDataMissingInputVal;
    }

    public String getNoAccessInputVal() {
        return noAccessInputVal;
    }

    public void setNoAccessInputVal(String noAccessInputVal) {
        this.noAccessInputVal = noAccessInputVal;
    }

    public int getUndoRedoCountInputVal() {
        return undoRedoCountInputVal;
    }

    public void setUndoRedoCountInputVal(int undoRedoCountInputVal) {
        this.undoRedoCountInputVal = undoRedoCountInputVal;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserEssBaseOptions [adjustColumnWidth=" + adjustColumnWidth + ", indentationGroupVal="
                + indentationGroupVal + ", noAccessInputVal=" + noAccessInputVal + ", noDataMissingInputVal="
                + noDataMissingInputVal + ", preseveFormatting=" + preseveFormatting + ", repeatLabel=" + repeatLabel
                + ", suppressMissingColumns=" + suppressMissingColumns + ", suppressMissingRows=" + suppressMissingRows
                + ", suppressZeroColumns=" + suppressZeroColumns + ", suppressZeroRows=" + suppressZeroRows
                + ", undoRedoCountInputVal=" + undoRedoCountInputVal + ", userId=" + userId + "]";
    }



    

}