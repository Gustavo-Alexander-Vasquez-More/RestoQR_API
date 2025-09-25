package com.restoqr.restoApi.models;

public class ProductOrderDTO {
    private String id;
    private String nameProduct;
    private String description;
    private double price;
    private String category;
    private boolean available;
    private Integer stock; // solo para drink
    private Boolean isAlcoholic; // solo para drink
    private Integer subcategory; // solo para dish
    private Boolean isVegetarian; // solo para dish

    public ProductOrderDTO() {}

    // getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNameProduct() { return nameProduct; }
    public void setNameProduct(String nameProduct) { this.nameProduct = nameProduct; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Boolean getIsAlcoholic() { return isAlcoholic; }
    public void setIsAlcoholic(Boolean isAlcoholic) { this.isAlcoholic = isAlcoholic; }
    public Integer getSubcategory() { return subcategory; }
    public void setSubcategory(Integer subcategory) { this.subcategory = subcategory; }
    public Boolean getIsVegetarian() { return isVegetarian; }
    public void setIsVegetarian(Boolean isVegetarian) { this.isVegetarian = isVegetarian; }
}

