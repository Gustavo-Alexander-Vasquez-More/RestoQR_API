package com.restoqr.restoApi.models;

public class Dish extends Product{
    private int subcategory; // 1: entrada, 2: plato principal, 3: postre
    private boolean isVegetarian; //indica si el plato es vegetariano o no

    //constructor
    public Dish() {}

    public Dish(String nameProduct, String description, double price, boolean available, int subcategory, boolean isVegetarian) {
        super(nameProduct, description, price, "dish", available);
        this.subcategory = subcategory;
        this.isVegetarian = isVegetarian;
    }

    public void setSubcategory(int subcategory) {
        this.subcategory = subcategory;
    }

    public int getSubcategory() {
        return subcategory;
    }

    public void setIsVegetarian(boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }
}
