package org.example.carpark.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.carpark.application.dto.CarParkAvailabilityResponse;
import org.example.carpark.domain.model.CarParkAvailability;
import org.example.carpark.domain.repository.CarParkAvailabilityRepository;
import org.example.carpark.exception.CarParkAvailabilityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
public class CarParkAvailabilityServiceImplTest {

    @Autowired
    private CarParkAvailabilityRepository carParkAvailabilityRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Autowired
    private CarParkAvailabilityServiceImpl carParkAvailabilityService;

    private String url = "http://mock-url.com";

    @BeforeEach
    void setUp() {
        carParkAvailabilityService = new CarParkAvailabilityServiceImpl(carParkAvailabilityRepository, objectMapper, url, restTemplate);
    }

    @Test
    public void testFetchAndSaveAvailability_Success() throws Exception {
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


        List<CarParkAvailability> availabilities = carParkAvailabilityService.fetchAvailability();
        List<CarParkAvailability> savedRecored = carParkAvailabilityService.updateAvailability(availabilities);

        assertNotNull(availabilities);
        assertEquals(1, availabilities.size());
        assertEquals(1, savedRecored.size());

        CarParkAvailability savedAvailability = carParkAvailabilityRepository.findAll().get(0);
        assertEquals("CP1", savedAvailability.getCarParkNo());
        assertEquals(100, savedAvailability.getTotalLots());
        assertEquals(50, savedAvailability.getAvailableLots());

        verify(restTemplate, times(1)).getForObject(url, String.class);
        verify(objectMapper, times(1)).readValue(jsonResponse, CarParkAvailabilityResponse.class);
    }

    @Test
    public void testFetchAvailability_Failure() throws JsonProcessingException {
        when(restTemplate.getForObject(url, String.class)).thenThrow(new RuntimeException("API error"));

        carParkAvailabilityService = new CarParkAvailabilityServiceImpl(carParkAvailabilityRepository, objectMapper, url, restTemplate);

        Exception exception = assertThrows(CarParkAvailabilityException.class, () -> {
            carParkAvailabilityService.fetchAvailability();
        });

        assertEquals("Failed to fetch car park availability", exception.getMessage());

        verify(restTemplate, times(1)).getForObject(url, String.class);
    }

}
