package com.restoqr.restoApi.models;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.List;

@Document(collection = "products")
public abstract class Product { //Entidad que define cada producto de la carta
    @Id
    private String id;
    @Indexed
    private String group_id;
    private String nameProduct; //nombre del producto
    private String photoUrl; //url de la foto del producto
    private String description; //descripcion del producto
    private double price; //precio del producto
    private String category; //categoria del producto
    private List<String> subCategory; //ids de subcategorías del producto
    private boolean available; //disponibilidad del producto

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    //constructor
    public Product() {}

    public Product(String group_id, String nameProduct, String photoUrl, String description, double price, String category, List<String> subCategory, boolean available) {
        this.group_id = group_id;
        this.nameProduct = nameProduct;
        this.photoUrl = photoUrl;
        this.description = description;
        this.price = price;
        this.category = category;
        this.subCategory = subCategory;
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getSubCategory() {
        return subCategory;
    }
    public void setSubCategory(List<String> subCategory) {
        this.subCategory = subCategory;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
