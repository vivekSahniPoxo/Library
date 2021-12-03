package com.example.library;

public class DataModel_Inventory_InventoryStatus {
    String RFIDNUMBER;
    String Status;

    public DataModel_Inventory_InventoryStatus(String RFIDNUMBER, String status) {
        this.RFIDNUMBER = RFIDNUMBER;
        Status = status;
    }

    public String getRFIDNUMBER() {
        return RFIDNUMBER;
    }

    public void setRFIDNUMBER(String RFIDNUMBER) {
        this.RFIDNUMBER = RFIDNUMBER;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
