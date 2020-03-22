package com.indrasol.essbase.essbasesheetplugin.model;

public class EMembers {

    private String memberId;
    private int levelNumber;
    private int generationNumber;
    private String memberName;
    private String memberDescription;
    private boolean isDimensionRootMember;
    private int childCount;

    

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberDescription() {
        return memberDescription;
    }

    public void setMemberDescription(String memberDescription) {
        this.memberDescription = memberDescription;
    }

    public boolean isDimensionRootMember() {
        return isDimensionRootMember;
    }

    public void setDimensionRootMember(boolean isDimensionRootMember) {
        this.isDimensionRootMember = isDimensionRootMember;
    }

    public int getChildCount() {
        return childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }
    
    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public int getGenerationNumber() {
        return generationNumber;
    }

    public void setGenerationNumber(int generationNumber) {
        this.generationNumber = generationNumber;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    
    public EMembers() {
    }




}
