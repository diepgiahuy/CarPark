package org.example.carpark.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarParkInfo {

    @Id
    private String carParkNo;

    private String address;
    private double latitude;
    private double longitude;
}