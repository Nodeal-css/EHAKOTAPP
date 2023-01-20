package com.example.ehakotapp;

public class History {
    int book_id;
    String date;
    int amount;

    public History(int book_id, String date, int amount) {
        this.book_id = book_id;
        this.date = date;
        this.amount = amount;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
