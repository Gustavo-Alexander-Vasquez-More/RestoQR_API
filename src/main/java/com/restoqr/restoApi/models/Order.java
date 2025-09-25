package com.restoqr.restoApi.models;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private int numOrden; //numero de orden
    private int numberTable; //numero de mesa
    private List<Product> products; //productos pedidos
    private String status; //estado del pedido (pendiente, en preparacion, listo, entregado)
    private double totalPrice; //precio total del pedido

    public Order() {}

    public Order(int numOrden, int numberTable, List<Product> products, String status, double totalPrice) {
        this.numOrden = numOrden;
        this.numberTable = numberTable;
        this.products = products;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public int getNumOrden() {
        return numOrden;
    }
    public void setNumOrden(int numOrden) {
        this.numOrden = numOrden;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumberTable() {
        return numberTable;
    }

    public void setNumberTable(int numberTable) {
        this.numberTable = numberTable;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
