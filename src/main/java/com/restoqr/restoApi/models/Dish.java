package com.restoqr.restoApi.models;

import java.util.List;

public class Dish extends Product{
    private boolean isVegetarian; //indica si el plato es vegetariano o no

    //constructor
    public Dish() {}

    public Dish(String group_id, String nameProduct, String photoUrl, String description, double price, String category, List<String> subCategory, boolean available, boolean isVegetarian) {
        super(group_id, nameProduct, photoUrl, description, price, category, subCategory, available);
        this.isVegetarian = isVegetarian;
    }

    public void setIsVegetarian(boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }
}
