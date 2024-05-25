package org.example.carpark.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarParkInfo {
    private String carParkNo;
    private String address;
    private double latitude;
    private double longitude;
}
