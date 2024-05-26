package org.example.carpark.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


public interface CarParkInfoResponse {
    String getAddress();
    double getLatitude();
    double getLongitude();
    int getTotalLots();
    int getAvailableLots();
    double getDistance();
}