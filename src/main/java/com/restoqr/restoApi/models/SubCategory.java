package com.restoqr.restoApi.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

public class SubCategory {
    @Id
    private  String id;
    private String nombre; // Nombre de la subcategoría
    private String photoUrl; // URL de la foto representativa
    @CreatedDate
    private Instant createdAt; // Fecha de creación
    @LastModifiedDate
    private Instant updatedAt; // Fecha de última actualización

    public SubCategory() {}

    public SubCategory(String nombre, String photoUrl, Instant createdAt, Instant updatedAt) {
        this.nombre = nombre;
        this.photoUrl = photoUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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
}
