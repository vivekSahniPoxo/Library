package com.example.library;

public class DataModel_Racking {
    int id;
    private String Rfid;
    private String publisher;
    private String title;
//    private String EntryDate;


    public DataModel_Racking(String rfid, String publisher, String title) {
        Rfid = rfid;
        this.publisher = publisher;
        this.title = title;
    }

    public DataModel_Racking() {
    }

    public String getRfid() {
        return Rfid;
    }

    public void setRfid(String rfid) {
        Rfid = rfid;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
