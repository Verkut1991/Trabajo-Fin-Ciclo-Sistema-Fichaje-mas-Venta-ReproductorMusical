package com.example.fichajefx.models;

import com.google.gson.annotations.SerializedName;

public class HistoryRecord {
    @SerializedName("tipo")
    private String tipo;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("ubicacion")
    private String ubicacion;

    public String getTipo() {
        return tipo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUbicacion() {
        return ubicacion;
    }
}
