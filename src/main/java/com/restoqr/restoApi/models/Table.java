package com.restoqr.restoApi.models;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tables")
public class Table { //Entidad que define cada mesa del restaurante
    @Id
    private String id;
    private int number; //numero de mesa
    private int capacity; //capacidad de la mesa
    private boolean available; //disponibilidad de la mesa

    public Table() {}

    public Table(int number, int capacity, boolean available) {
        this.number = number;
        this.capacity = capacity;
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
