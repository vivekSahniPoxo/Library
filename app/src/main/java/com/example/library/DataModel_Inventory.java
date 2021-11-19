package com.example.library;

public class DataModel_Inventory {
    String RFIDNo ;
    String AccessNo;
    String Author;
    String Title;
    private String color;
    public DataModel_Inventory(String RFIDNo, String accessNo, String author, String title) {
        this.RFIDNo = RFIDNo;
        AccessNo = accessNo;
        Author = author;
        Title = title;
    }

    public DataModel_Inventory() {
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getRFIDNo() {
        return RFIDNo;
    }

    public void setRFIDNo(String RFIDNo) {
        this.RFIDNo = RFIDNo;
    }

    public String getAccessNo() {
        return AccessNo;
    }

    public void setAccessNo(String accessNo) {
        AccessNo = accessNo;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
