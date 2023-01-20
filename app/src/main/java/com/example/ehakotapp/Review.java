package com.example.ehakotapp;

public class Review {
    String clientImage;
    String clientFullName;
    String review;

    public Review(String clientImage, String clientFullName, String review) {
        this.clientImage = clientImage;
        this.clientFullName = clientFullName;
        this.review = review;
    }

    public String getClientImage() {
        return clientImage;
    }

    public void setClientImage(String clientImage) {
        this.clientImage = clientImage;
    }

    public String getClientFullName() {
        return clientFullName;
    }

    public void setClientFullName(String clientFullName) {
        this.clientFullName = clientFullName;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
