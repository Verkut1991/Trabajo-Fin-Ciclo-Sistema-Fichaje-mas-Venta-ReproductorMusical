package com.example.fichajefx.models;

public class WorkTimeResponse {
    private int totalMinutes;
    private int hours;
    private int minutes;
    private String formatted;
    private String estado;

    public int getTotalMinutes() {
        return totalMinutes;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public String getFormatted() {
        return formatted;
    }

    public String getEstado() {
        return estado;
    }
}
