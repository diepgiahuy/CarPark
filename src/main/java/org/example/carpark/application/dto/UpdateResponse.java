package org.example.carpark.application.dto;

import lombok.Data;
import org.example.carpark.domain.model.CarParkAvailability;

import java.util.List;

@Data
public class UpdateResponse {
    private int rowsChanged;

    public UpdateResponse(int rowsChanged) {
        this.rowsChanged = rowsChanged;
    }
}
