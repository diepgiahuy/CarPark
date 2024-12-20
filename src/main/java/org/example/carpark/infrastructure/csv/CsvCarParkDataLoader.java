package org.example.carpark.infrastructure.csv;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.carpark.application.dto.UpdateResponse;
import org.example.carpark.domain.model.CarParkInfo;
import org.example.carpark.domain.repository.CarParkInfoRepository;
import org.example.carpark.domain.service.CarParkDataLoader;
import org.example.carpark.domain.service.CoordinateConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CsvCarParkDataLoader implements CarParkDataLoader {

    private final CoordinateConversionService coordinateConversionService;
    private final String csvFilePath;
    private final CarParkInfoRepository carParkInfoRepository;

    /**
     * Constructs a CsvCarParkDataLoader with the specified dependencies.
     *
     * @param coordinateConversionService the service for converting coordinates
     * @param csvFilePath the path to the CSV file
     * @param carParkInfoRepository the repository for storing car park information
     */
    @Autowired
    public CsvCarParkDataLoader(CoordinateConversionService coordinateConversionService,
                                @Value("${csv.file.path}") String csvFilePath,
                                CarParkInfoRepository carParkInfoRepository) {
        this.coordinateConversionService = coordinateConversionService;
        this.csvFilePath = csvFilePath;
        this.carParkInfoRepository = carParkInfoRepository;
    }

    /**
     * Initializes the loader by loading car park data if it does not already exist.
     */
    @PostConstruct
    public void init() {
        loadCarParkData(false);
    }

    /**
     * Loads car park data from the CSV file.
     * If data already exists and force load is not requested, it skips the loading.
     *
     * @param isForce if true, forces reloading the data even if it already exists
     * @return an UpdateResponse indicating the number of rows loaded
     */
    @Override
    public UpdateResponse loadCarParkData(boolean isForce) {
        if (carParkInfoRepository.count() > 0 && !isForce) {
            log.info("Car park data already exists in the database. Skipping CSV load.");
            return new UpdateResponse(0);
        }

        InputStream resourceStream = getClass().getResourceAsStream(csvFilePath);
        if (resourceStream == null) {
            throw new RuntimeException("Resource not found: " + csvFilePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            List<CSVRecord> csvRecords = csvParser.getRecords();
            List<CarParkInfo> carParkInfoList = new ArrayList<>();
            for (CSVRecord csvRecord : csvRecords) {
                CarParkInfo carParkInfo = CarParkInfo.builder()
                        .carParkNo(csvRecord.get(0))
                        .address(csvRecord.get(1))
                        .latitude(convertCoordinates(csvRecord.get(2), csvRecord.get(3))[0])
                        .longitude(convertCoordinates(csvRecord.get(2), csvRecord.get(3))[1])
                        .build();
                carParkInfoList.add(carParkInfo);
            }

            carParkInfoRepository.saveAll(carParkInfoList);
            log.info("Loaded car park data from CSV into the database {}.", carParkInfoList.size());
            return new UpdateResponse(carParkInfoList.size());
        } catch (Exception e) {
            throw new RuntimeException("Error loading car park data from CSV", e);
        }
    }

    /**
     * Converts coordinates from SVY21 to WGS84 format.
     *
     * @param xCoord the X coordinate in SVY21 format
     * @param yCoord the Y coordinate in SVY21 format
     * @return an array containing the converted latitude and longitude
     */
    private double[] convertCoordinates(String xCoord, String yCoord) {
        double x = Double.parseDouble(xCoord);
        double y = Double.parseDouble(yCoord);
        return coordinateConversionService.convert(x, y);
    }
}
