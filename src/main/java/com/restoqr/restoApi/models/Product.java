package com.restoqr.restoApi.models;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public abstract class Product { //Entidad que define cada producto de la carta
    @Id
    private String id;
    private String nameProduct; //nombre del producto
    private String description; //descripcion del producto
    private double price; //precio del producto
    private String category; //categoria del producto
    private boolean available; //disponibilidad del producto

    //constructor
    public Product() {}

    public Product(String nameProduct, String description, double price, String category, boolean available) {
        this.nameProduct = nameProduct;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
