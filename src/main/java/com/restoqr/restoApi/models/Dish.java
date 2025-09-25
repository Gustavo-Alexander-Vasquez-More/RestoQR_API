package com.restoqr.restoApi.models;

public class Dish extends Product{
    private String subcategory; //subcategoria del plato (entrada, plato principal, postre, etc)
    private boolean isVegetarian; //indica si el plato es vegetariano o no

    //constructor
    public Dish() {}

    public Dish(String nameProduct, String description, double price, boolean available, String subcategory, boolean isVegetarian) {
        super(nameProduct, description, price, "dish", available);
        this.subcategory = subcategory;
        this.isVegetarian = isVegetarian;
    }
}
