package com.example.fichajefx.models;

public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    private String nif;
    private String rol;
    private boolean politica_aceptada;

    public RegisterRequest(String nombre, String email, String password, String nif, String rol,
            boolean politica_aceptada) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.nif = nif;
        this.rol = rol;
        this.politica_aceptada = politica_aceptada;
    }
}
