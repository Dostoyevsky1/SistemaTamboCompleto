package com.tambo.inventory.dto;

import java.util.List;

public class AuthResponse {
    private String token;
    private final String tipoToken = "Bearer";
    private String username;
    private String nombre;
    private List<String> roles;
    private Long sucursalId;

    public AuthResponse() {
    }

    public AuthResponse(String token, String username, String nombre, List<String> roles, Long sucursalId) {
        this.token = token;
        this.username = username;
        this.nombre = nombre;
        this.roles = roles;
        this.sucursalId = sucursalId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipoToken() {
        return tipoToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Long getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Long sucursalId) {
        this.sucursalId = sucursalId;
    }
}
