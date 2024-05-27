package org.example.carpark.infrastructure.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.carpark.application.dto.CarParkAvailabilityResponse;
import org.example.carpark.application.service.impl.CarParkAvailabilityServiceImpl;
import org.example.carpark.domain.model.CarParkAvailability;
import org.example.carpark.domain.repository.CarParkAvailabilityRepository;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
public class CarParkAvailabilitySchedulerTest {

    @Autowired
    private CarParkAvailabilityRepository carParkAvailabilityRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private String url = "http://mock-url.com";

    @Autowired
    private CarParkAvailabilityServiceImpl carParkAvailabilityService;

    @BeforeEach
    void setUp() {
        carParkAvailabilityService = new CarParkAvailabilityServiceImpl(carParkAvailabilityRepository, objectMapper, url, restTemplate);
    }

    @Test
    public void testScheduledAvailabilityFetch_Success() throws Exception {
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

        CarParkAvailabilityScheduler scheduler = new CarParkAvailabilityScheduler(carParkAvailabilityService);
        scheduler.scheduleAvailabilityFetch();
        List<CarParkAvailability> carParkAvailabilities = carParkAvailabilityRepository.findAll();
        assertEquals(carParkAvailabilities.size(), 1);

    }
}
