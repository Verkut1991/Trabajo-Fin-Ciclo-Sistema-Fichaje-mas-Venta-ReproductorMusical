package com.mycompany.fichajefx.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BrandingConfig {
    private String cliente_id;
    private String nombre_suite;
    private Map<String, String> colores;
    private String logo_url;

    // Getters y Setters
    public String getCliente_id() {
        return cliente_id;
    }

    public void setCliente_id(String cliente_id) {
        this.cliente_id = cliente_id;
    }

    public String getNombre_suite() {
        return nombre_suite;
    }

    public void setNombre_suite(String nombre_suite) {
        this.nombre_suite = nombre_suite;
    }

    public Map<String, String> getColores() {
        return colores;
    }

    public void setColores(Map<String, String> colores) {
        this.colores = colores;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }
}
