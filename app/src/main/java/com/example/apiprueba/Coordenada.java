package com.example.apiprueba;

public class Coordenada {
    private String latitude;
    private String longitude;

    public Coordenada(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}

