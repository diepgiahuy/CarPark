package org.example.carpark.domain.service;

import org.example.carpark.application.dto.UpdateResponse;

public interface CarParkDataLoader {
    UpdateResponse loadCarParkData(boolean isForce);
}
