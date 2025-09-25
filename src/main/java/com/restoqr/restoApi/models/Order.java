package com.restoqr.restoApi.models;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.List;

@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String nameClient;
    private int numOrden; //numero de orden
    private int numberTable; //numero de mesa
    private List<ProductOrderDTO> products; //productos pedidos
    private int status; //estado del pedido (1: pendiente, 2: en_preparacion, 3: listo, 4: entregado, 5: cerrado)
    private double totalPrice; //precio total del pedido

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public Order() {}

    public Order(String nameClient, int numOrden, int numberTable, List<ProductOrderDTO> products, int status, double totalPrice) {
        this.nameClient = nameClient;
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

    public List<ProductOrderDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductOrderDTO> products) {
        this.products = products;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
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

    public String getNameClient() {
        return nameClient;
    }

    public void setNameClient(String nameClient) {
        this.nameClient = nameClient;
    }

    public enum Status {
        PENDIENTE(1),
        EN_PREPARACION(2),
        LISTO(3),
        ENTREGADO(4),
        CERRADO(5);
        private final int value;
        Status(int value) { this.value = value; }
        public int getValue() { return value; }
    }
}
