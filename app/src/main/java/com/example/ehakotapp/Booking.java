package com.example.ehakotapp;

public class Booking {
    String clientImg;
    String bookStatus;
    String clientName;
    String vehicleModel;
    String bookingID;

    public Booking(String clientImg, String bookStatus, String clientName, String vehicleModel, String bookingID) {
        this.clientImg = clientImg;
        this.bookStatus = bookStatus;
        this.clientName = clientName;
        this.vehicleModel = vehicleModel;
        this.bookingID = bookingID;
    }

    public String getClientImg() {
        return clientImg;
    }

    public void setClientImg(String clientImg) {
        this.clientImg = clientImg;
    }

    public String getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getBook_id() {
        return bookingID;
    }

    public void setBook_id(String bookingID) {
        this.bookingID = bookingID;
    }
}
