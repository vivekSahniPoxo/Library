package com.example.library;

public class DataModel_Racking {
    int id;
    private String RackNo;
    private String ShelfNo;
    private String RFIDNo;
    private String EntryDate;

    public DataModel_Racking(String rackNo, String shelfNo, String RFIDNo) {
        RackNo = rackNo;
        ShelfNo = shelfNo;
        this.RFIDNo = RFIDNo;
    }

    public DataModel_Racking(int id, String rackNo, String shelfNo, String RFIDNo, String entryDate) {
        this.id = id;
        RackNo = rackNo;
        ShelfNo = shelfNo;
        this.RFIDNo = RFIDNo;
        EntryDate = entryDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRackNo() {
        return RackNo;
    }

    public void setRackNo(String rackNo) {
        RackNo = rackNo;
    }

    public String getShelfNo() {
        return ShelfNo;
    }

    public void setShelfNo(String shelfNo) {
        ShelfNo = shelfNo;
    }

    public String getRFIDNo() {
        return RFIDNo;
    }

    public void setRFIDNo(String RFIDNo) {
        this.RFIDNo = RFIDNo;
    }

    public String getEntryDate() {
        return EntryDate;
    }

    public void setEntryDate(String entryDate) {
        EntryDate = entryDate;
    }
}
