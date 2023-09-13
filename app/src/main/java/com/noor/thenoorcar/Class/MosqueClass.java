package com.noor.thenoorcar.Class;

public class MosqueClass {
    private String name,vicinity,distance,latitude,longitude;

    public MosqueClass(String name, String vicinity, String distance, String latitude, String longitude) {
        this.name = name;
        this.vicinity = vicinity;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getDistance() {
        return distance;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getVicinity() {
        return vicinity;
    }
}
