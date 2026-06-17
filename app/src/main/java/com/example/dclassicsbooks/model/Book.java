package com.example.dclassicsbooks.model;

public class Book {

    public static final String CATEGORY_FICTION = "Fiction";
    public static final String CATEGORY_NON_FICTION = "Non-Fiction";

    public final int imageRes;
    public final String title;
    public final String author;
    public final String views;
    public final String rating;
    public final String category;

    public Book(int imageRes, String title, String author, String views, String rating, String category) {
        this.imageRes = imageRes;
        this.title = title;
        this.author = author;
        this.views = views;
        this.rating = rating;
        this.category = category;
    }
}
