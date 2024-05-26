package org.example.carpark;

import org.example.carpark.application.dto.CarParkInfoResponse;

public class TestDataUtil {

    public static CarParkInfoResponse createCarParkInfoResponse(String address, double latitude, double longitude, int totalLots, int availableLots, double distance) {
        return new CarParkInfoResponse() {
            @Override
            public String getAddress() {
                return address;
            }

            @Override
            public double getLatitude() {
                return latitude;
            }

            @Override
            public double getLongitude() {
                return longitude;
            }

            @Override
            public int getTotalLots() {
                return totalLots;
            }

            @Override
            public int getAvailableLots() {
                return availableLots;
            }

            @Override
            public double getDistance() {
                return distance;
            }
        };
    }
}
