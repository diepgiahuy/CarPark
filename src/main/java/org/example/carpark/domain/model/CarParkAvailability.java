package org.example.carpark.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarParkAvailability {

    @Id
    @Column(name = "car_park_no", nullable = false)
    private String carParkNo;

    @Column(name = "total_lots")
    private Integer totalLots;

    @Column(name = "available_lots")
    private Integer availableLots;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarParkAvailability that = (CarParkAvailability) o;
        return Objects.equals(carParkNo, that.carParkNo) &&
            Objects.equals(totalLots, that.totalLots) &&
            Objects.equals(availableLots, that.availableLots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carParkNo, totalLots, availableLots);
    }
}