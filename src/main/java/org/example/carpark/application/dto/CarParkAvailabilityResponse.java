package org.example.carpark.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CarParkAvailabilityResponse {
    private List<Item> items;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        @JsonProperty("timestamp")
        private String timestamp;

        @JsonProperty("carpark_data")
        private List<CarparkDatum> carparkData;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CarparkDatum {
        @JsonProperty("carpark_number")
        private String carparkNumber;

        @JsonProperty("update_datetime")
        private String updateDatetime;

        @JsonProperty("carpark_info")
        private List<CarparkInfo> carparkInfo;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CarparkInfo {
        @JsonProperty("total_lots")
        private Integer totalLots;

        @JsonProperty("lot_type")
        private String lotType;

        @JsonProperty("lots_available")
        private Integer lotsAvailable;
    }
}
