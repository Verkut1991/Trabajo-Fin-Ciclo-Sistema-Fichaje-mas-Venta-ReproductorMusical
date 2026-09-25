package com.example.fichajefx.models;

public class AuthResponse {
    private String message;
    private Empleado empleado;
    private String error;

    public String getMessage() {
        return message;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public String getError() {
        return error;
    }

    public static class Empleado {
        private int id;
        private String nombre;
        private String email;
        private String nif;
        private String rol;
        private int cliente_id;

        public int getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public String getEmail() {
            return email;
        }

        public String getNif() {
            return nif;
        }

        public String getRol() {
            return rol;
        }

        public int getClienteId() {
            return cliente_id;
        }
    }
}
