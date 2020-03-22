package com.indrasol.essbase.essbasesheetplugin.model;

import java.util.List;

public class Dimension {

    private int dimensionNumber;
    private String dimensionName;
    private String dimensionDescription;

    private List<EMembers> members;


    public Dimension() {
    }


    public int getDimensionNumber() {
        return dimensionNumber;
    }

    public void setDimensionNumber(int dimensionNumber) {
        this.dimensionNumber = dimensionNumber;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public String getDimensionDescription() {
        return dimensionDescription;
    }

    public void setDimensionDescription(String dimensionDescription) {
        this.dimensionDescription = dimensionDescription;
    }

    public List<EMembers> getMembers() {
        return members;
    }

    public void setMembers(List<EMembers> members) {
        this.members = members;
    }


}