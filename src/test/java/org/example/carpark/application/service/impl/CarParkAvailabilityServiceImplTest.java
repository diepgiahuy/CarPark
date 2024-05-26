package org.example.carpark.application.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.carpark.application.dto.CarParkAvailabilityResponse;
import org.example.carpark.application.dto.UpdateResponse;
import org.example.carpark.domain.model.CarParkAvailability;
import org.example.carpark.domain.repository.CarParkAvailabilityRepository;
import org.example.carpark.exception.CarParkAvailabilityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarParkAvailabilityServiceImplTest {

    @Mock
    private CarParkAvailabilityRepository carParkAvailabilityRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CarParkAvailabilityServiceImpl carParkAvailabilityService;

    private String url = "http://mock-url.com";

    @BeforeEach
    void setUp() {
        carParkAvailabilityService = new CarParkAvailabilityServiceImpl(carParkAvailabilityRepository, objectMapper, url, restTemplate);
    }

    @Test
    public void testFetchAvailability_Success() throws Exception {
        String jsonResponse = "{\"items\":[{\"carpark_data\":[{\"carpark_number\":\"CP1\",\"carpark_info\":[{\"total_lots\":100,\"lots_available\":50}]}]}]}";

        CarParkAvailabilityResponse response = new CarParkAvailabilityResponse();
        CarParkAvailabilityResponse.Item item = new CarParkAvailabilityResponse.Item();
        CarParkAvailabilityResponse.CarparkDatum datum = new CarParkAvailabilityResponse.CarparkDatum();
        CarParkAvailabilityResponse.CarparkInfo info = new CarParkAvailabilityResponse.CarparkInfo();

        info.setTotalLots(100);
        info.setLotsAvailable(50);

        datum.setCarparkNumber("CP1");
        datum.setCarparkInfo(List.of(info));

        item.setCarparkData(List.of(datum));
        response.setItems(List.of(item));

        when(restTemplate.getForObject(url, String.class)).thenReturn(jsonResponse);
        when(objectMapper.readValue(jsonResponse, CarParkAvailabilityResponse.class)).thenReturn(response);
        when(carParkAvailabilityRepository.saveAll(any())).thenReturn(List.of(new CarParkAvailability()));

        UpdateResponse updateResponse = carParkAvailabilityService.fetchAvailability();

        assertNotNull(updateResponse);
        assertEquals(1, updateResponse.getRowsChanged());

        verify(restTemplate, times(1)).getForObject(url, String.class);
        verify(objectMapper, times(1)).readValue(jsonResponse, CarParkAvailabilityResponse.class);
        verify(carParkAvailabilityRepository, times(1)).saveAll(any());
    }

    @Test
    public void testFetchAvailability_Failure() throws Exception {
        when(restTemplate.getForObject(url, String.class)).thenThrow(new RuntimeException("API error"));

        Exception exception = assertThrows(CarParkAvailabilityException.class, () -> {
            carParkAvailabilityService.fetchAvailability();
        });

        assertEquals("Failed to fetch car park availability", exception.getMessage());

        verify(restTemplate, times(1)).getForObject(url, String.class);
        verify(objectMapper, times(0)).readValue(anyString(), eq(CarParkAvailabilityResponse.class));
        verify(carParkAvailabilityRepository, times(0)).saveAll(any());
    }
}