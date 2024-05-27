package org.example.carpark.application.controller;

import org.example.carpark.TestDataUtil;
import org.example.carpark.application.dto.CarParkInfoResponse;
import org.example.carpark.application.dto.UpdateResponse;
import org.example.carpark.domain.service.CarParkAvailabilityService;
import org.example.carpark.domain.service.CarParkService;
import org.example.carpark.infrastructure.csv.CsvCarParkDataLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CarParkController.class)
public class CarParkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarParkAvailabilityService carParkAvailabilityService;

    @MockBean
    private CarParkService carParkService;

    @MockBean
    private CsvCarParkDataLoader carParkDataLoader;

    @Test
    public void testReloadCsv() throws Exception {
        UpdateResponse updateResponse = new UpdateResponse(100);
        when(carParkDataLoader.loadCarParkData(true)).thenReturn(updateResponse);

        mockMvc.perform(get("/carparks/reload-csv"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rowsChanged").value(100));

        verify(carParkDataLoader, times(1)).loadCarParkData(true);
    }

    @Test
    public void testFetchCarLotAvailability() throws Exception {
        UpdateResponse updateResponse = new UpdateResponse(50);
        when(carParkAvailabilityService.fetchAndUpdateAvailability()).thenReturn(updateResponse);

        mockMvc.perform(get("/carparks/availability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rowsChanged").value(50));

        verify(carParkAvailabilityService, times(1)).fetchAndUpdateAvailability();
    }

    @Test
    public void testFindNearestCarPark() throws Exception {

        CarParkInfoResponse carPark1 = TestDataUtil.createCarParkInfoResponse("Car Park 1", 1.1, 1.1, 100, 50, 0.1);
        Page<CarParkInfoResponse> carParksPage = new PageImpl<>(Collections.singletonList(carPark1), PageRequest.of(0, 1), 1);

        when(carParkService.findAllCarPark(1.0, 2.0, 0, 1)).thenReturn(carParksPage);

        mockMvc.perform(get("/carparks/nearest")
                        .param("latitude", "1.0")
                        .param("longitude", "2.0")
                        .param("page", "0")
                        .param("per_page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address").value("Car Park 1"))
                .andExpect(jsonPath("$[0].distance").value(0.1))
                .andExpect(jsonPath("$[0].totalLots").value(100))
                .andExpect(jsonPath("$[0].availableLots").value(50))
                .andExpect(jsonPath("$[0].latitude").value(1.1))
                .andExpect(jsonPath("$[0].longitude").value(1.1));

        verify(carParkService, times(1)).findAllCarPark(1.0, 2.0, 0, 1);
    }

    @Test
    public void testFindNearestCarPark_MissingParameter() throws Exception {
        mockMvc.perform(get("/carparks/nearest")
                        .param("latitude", "1.0")
                        .param("longitude", "2.0")
                        .param("per_page", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFindNearestCarPark_InvalidLatitude() throws Exception {
        mockMvc.perform(get("/carparks/nearest")
                        .param("latitude", "-100.0")  // Invalid latitude
                        .param("longitude", "2.0")
                        .param("page", "0")
                        .param("per_page", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFindNearestCarPark_InvalidLongitude() throws Exception {
        mockMvc.perform(get("/carparks/nearest")
                        .param("latitude", "1.0")
                        .param("longitude", "200.0")  // Invalid longitude
                        .param("page", "0")
                        .param("per_page", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFindNearestCarPark_InvalidPage() throws Exception {
        mockMvc.perform(get("/carparks/nearest")
                        .param("latitude", "1.0")
                        .param("longitude", "2.0")
                        .param("page", "-1")  // Invalid page
                        .param("per_page", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFindNearestCarPark_InvalidPerPage() throws Exception {
        mockMvc.perform(get("/carparks/nearest")
                        .param("latitude", "1.0")
                        .param("longitude", "2.0")
                        .param("page", "0")
                        .param("per_page", "0"))  // Invalid per_page
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFindNearestCarPark_EmptyResult() throws Exception {
        Page<CarParkInfoResponse> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 1), 0);

        when(carParkService.findAllCarPark(1.0, 2.0, 0, 1)).thenReturn(emptyPage);

        mockMvc.perform(get("/carparks/nearest")
                        .param("latitude", "1.0")
                        .param("longitude", "2.0")
                        .param("page", "0")
                        .param("per_page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());


        verify(carParkService, times(1)).findAllCarPark(1.0, 2.0, 0, 1);
    }
}
