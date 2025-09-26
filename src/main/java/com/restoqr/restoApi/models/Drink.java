package com.restoqr.restoApi.models;

import java.util.List;

public class Drink extends Product{
    private int stock; //cantidad de bebida disponible

    //constructor
    public Drink(String group_id ,String nameProduct, String photoUrl, String description, double price, String category, List<String> subCategory, boolean available, int stock) {
        super(group_id ,nameProduct, photoUrl, description, price, category, subCategory, available);
        this.stock = stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStock() {
        return stock;
    }
}
