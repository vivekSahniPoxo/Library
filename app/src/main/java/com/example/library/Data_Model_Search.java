package com.example.library;

public class Data_Model_Search {

    private Integer id;

    private String libraryName;
      private String iSBNNo;
    private String libraryItemType;
    private String bookAddedIn;
    private String bookCategory;
    private String itemStatus;
    private String subjectTitle;
    private String language;
    private String callNo;
    private String edition;
    private String seriesNo;
    private String seriesTitle;
    private String color;

//    public Data_Model_Search(String publisher, String rFIDNo) {
//        this.publisher = publisher;
//        this.rFIDNo = rFIDNo;
//    }



    public Data_Model_Search(String accessNo, String title) {
        this.accessNo = accessNo;
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getiSBNNo() {
        return iSBNNo;
    }

    public void setiSBNNo(String iSBNNo) {
        this.iSBNNo = iSBNNo;
    }

    public String getLibraryItemType() {
        return libraryItemType;
    }

    public void setLibraryItemType(String libraryItemType) {
        this.libraryItemType = libraryItemType;
    }

    public String getBookAddedIn() {
        return bookAddedIn;
    }

    public void setBookAddedIn(String bookAddedIn) {
        this.bookAddedIn = bookAddedIn;
    }

    public String getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(String bookCategory) {
        this.bookCategory = bookCategory;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getSubjectTitle() {
        return subjectTitle;
    }

    public void setSubjectTitle(String subjectTitle) {
        this.subjectTitle = subjectTitle;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCallNo() {
        return callNo;
    }

    public void setCallNo(String callNo) {
        this.callNo = callNo;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getSeriesNo() {
        return seriesNo;
    }

    public void setSeriesNo(String seriesNo) {
        this.seriesNo = seriesNo;
    }

    public String getSeriesTitle() {
        return seriesTitle;
    }

    public void setSeriesTitle(String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getrFIDNo() {
        return rFIDNo;
    }

    public void setrFIDNo(String rFIDNo) {
        this.rFIDNo = rFIDNo;
    }

    public String getAccessNo() {
        return accessNo;
    }

    public void setAccessNo(String accessNo) {
        this.accessNo = accessNo;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getYearOfPublication() {
        return yearOfPublication;
    }

    public void setYearOfPublication(String yearOfPublication) {
        this.yearOfPublication = yearOfPublication;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    private String publisher;
    private String rFIDNo;
    private String accessNo;
    private String author;
    private String title;
    private String volume;
    private String yearOfPublication;
    private String pages;
    private String registrationDate;

    private String size;

    private String keywords;

    private String userName;

    private String employeeName;

    private String entryDate;


}
