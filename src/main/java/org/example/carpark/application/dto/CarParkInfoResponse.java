package org.example.carpark.application.dto;

public interface CarParkInfoResponse {
    String getAddress();

    double getLatitude();

    double getLongitude();

    int getTotalLots();

    int getAvailableLots();

    double getDistance();
}