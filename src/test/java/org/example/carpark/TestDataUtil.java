package org.example.carpark;

import org.example.carpark.application.dto.CarParkInfoResponse;
import org.example.carpark.infrastructure.persistence.JpaCarParkAvailabilityRepository;
import org.example.carpark.infrastructure.persistence.JpaCarParkInfoRepository;
import org.example.carpark.infrastructure.repository.CarParkAvailabilityRepositoryImpl;
import org.example.carpark.infrastructure.repository.CarParkInfoRepositoryImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

public class TestDataUtil {

    @TestConfiguration
    public static class TestConfig {

        @Bean
        public CarParkInfoRepositoryImpl carParkInfoRepository(JpaCarParkInfoRepository jpaCarParkInfoRepository) {
            return new CarParkInfoRepositoryImpl(jpaCarParkInfoRepository);
        }

        @Bean
        public CarParkAvailabilityRepositoryImpl carParkAvailabilityRepository(JpaCarParkAvailabilityRepository jpaCarParkAvailabilityRepository) {
            return new CarParkAvailabilityRepositoryImpl(jpaCarParkAvailabilityRepository);
        }

    }

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
