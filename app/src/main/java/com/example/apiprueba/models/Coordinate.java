package com.example.apiprueba.models;

public class Coordinate {
    public String country;
    public double latitude;
    public double longitude;

    public Coordinate(String country, double latitude, double longitude) {
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "country='" + country + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
// country_id,latitude,longitude,brightness,scan,track,acq_date,acq_time,satellite,instrument,confidence,version,bright_t31,frp,daynight
//BOL,-18.16471,-59.19868,301.88,1.04,1.02,2023-10-05,211,Terra,MODIS,30,6.1NRT,290.11,4.1,N
