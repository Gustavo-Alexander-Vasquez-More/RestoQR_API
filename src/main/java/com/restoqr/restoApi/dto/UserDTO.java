package com.restoqr.restoApi.dto;

public class UserDTO {
    private String id;
    private String username;
    private int role;

    public UserDTO(String id, String username, int role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    // Getters y setters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public int getRole() { return role; }
}