package org.example.carpark.application.dto;

import lombok.Data;

@Data
public class UpdateResponse {
    private int rowsChanged;

    public UpdateResponse(int rowsChanged) {
        this.rowsChanged = rowsChanged;
    }
}
