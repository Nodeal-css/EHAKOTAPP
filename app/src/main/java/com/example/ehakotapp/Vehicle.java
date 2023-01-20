package com.example.ehakotapp;

public class Vehicle {
    String image;
    String model;
    String type;
    String location;
    String vehicle_id;

    public Vehicle(String image, String model, String type, String location, String vehicle_id) {
        this.image = image;
        this.model = model;
        this.type = type;
        this.location = location;
        this.vehicle_id = vehicle_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getvehicle_id() {
        return vehicle_id;
    }

    public void setvehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }
}
