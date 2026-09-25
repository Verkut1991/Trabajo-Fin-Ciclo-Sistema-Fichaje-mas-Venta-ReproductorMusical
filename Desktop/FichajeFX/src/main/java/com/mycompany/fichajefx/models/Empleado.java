package com.mycompany.fichajefx.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Empleado {
    private int id;
    private String nombre;
    private String email;
    private String nif;
    private String rol;
    private boolean politica_privacidad_aceptada;

    @JsonProperty("pin_enabled")
    private boolean pinEnabled;

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isPolitica_privacidad_aceptada() {
        return politica_privacidad_aceptada;
    }

    public void setPolitica_privacidad_aceptada(boolean politica_privacidad_aceptada) {
        this.politica_privacidad_aceptada = politica_privacidad_aceptada;
    }

    public boolean isPinEnabled() {
        return pinEnabled;
    }

    public void setPinEnabled(boolean pinEnabled) {
        this.pinEnabled = pinEnabled;
    }
}
