package com.restoqr.restoApi.models;

public class Drink extends Product{
    private int stock; //cantidad de bebida disponible
    private boolean isAlcoholic; //indica si la bebida es alcoholica o no

    //constructor
    public Drink(String nameProduct, String description, double price, boolean available, int stock, boolean isAlcoholic) {
        super(nameProduct, description, price, "drink", available);
        this.stock = stock;
        this.isAlcoholic = isAlcoholic;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStock() {
        return stock;
    }

    public void setIsAlcoholic(boolean isAlcoholic) {
        this.isAlcoholic = isAlcoholic;
    }

    public boolean isAlcoholic() {
        return isAlcoholic;
    }
}
