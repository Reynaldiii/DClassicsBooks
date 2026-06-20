package com.example.dclassicsbooks.model;

public class Store {

    public final int imageRes;
    public final String name;
    public final String address;
    public final String status;
    public final String weekdayHours;
    public final String weekendHours;

    public Store(int imageRes, String name, String address, String status, String weekdayHours, String weekendHours) {
        this.imageRes = imageRes;
        this.name = name;
        this.address = address;
        this.status = status;
        this.weekdayHours = weekdayHours;
        this.weekendHours = weekendHours;
    }
}
