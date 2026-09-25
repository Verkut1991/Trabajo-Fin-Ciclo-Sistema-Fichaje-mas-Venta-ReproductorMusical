package com.example.fichajefx.models;

public class ClockInRequest {
    private int empleado_id;
    private int cliente_id;
    private String tipo;
    private String ubicacion;

    public ClockInRequest(int empleado_id, int cliente_id, String tipo, String ubicacion) {
        this.empleado_id = empleado_id;
        this.cliente_id = cliente_id;
        this.tipo = tipo;
        this.ubicacion = ubicacion;
    }

    // Getters y Setters
    public int getEmpleado_id() {
        return empleado_id;
    }

    public void setEmpleado_id(int empleado_id) {
        this.empleado_id = empleado_id;
    }

    public int getCliente_id() {
        return cliente_id;
    }

    public void setCliente_id(int cliente_id) {
        this.cliente_id = cliente_id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
