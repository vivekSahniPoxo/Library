package com.example.library;

public class List_Data_Model {
    String RfidNumber;
    String ShelveNumber;
    String RackNumber;

    public String getRfidNumber() {
        return RfidNumber;
    }

    public void setRfidNumber(String rfidNumber) {
        RfidNumber = rfidNumber;
    }

    public String getShelveNumber() {
        return ShelveNumber;
    }

    public void setShelveNumber(String shelveNumber) {
        ShelveNumber = shelveNumber;
    }

    public String getRackNumber() {
        return RackNumber;
    }

    public void setRackNumber(String rackNumber) {
        RackNumber = rackNumber;
    }

    public List_Data_Model(String rfidNumber, String shelveNumber, String rackNumber) {
        RfidNumber = rfidNumber;
        ShelveNumber = shelveNumber;
        RackNumber = rackNumber;
    }


}
