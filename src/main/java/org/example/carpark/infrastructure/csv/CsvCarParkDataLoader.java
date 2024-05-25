package org.example.carpark.infrastructure.csv;

import ch.hsr.geohash.GeoHash;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.carpark.domain.model.CarParkInfo;
import org.example.carpark.domain.service.CarParkDataLoader;
import org.example.carpark.domain.service.CoordinateConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CsvCarParkDataLoader implements CarParkDataLoader {

    private final CoordinateConversionService coordinateConversionService;
    private final String csvFilePath;
    private final Map<String, List<CarParkInfo>> geohashMap = new HashMap<>();

    @Autowired
    public CsvCarParkDataLoader(CoordinateConversionService coordinateConversionService,
                                @Value("${csv.file.path}") String csvFilePath) {
        this.coordinateConversionService = coordinateConversionService;
        this.csvFilePath = csvFilePath;
    }

    @Override
    @PostConstruct
    public void loadCarParkData() {
        InputStream resourceStream = getClass().getResourceAsStream(csvFilePath);
        if (resourceStream == null) {
            throw new ResourceNotFoundException("Resource not found: " + csvFilePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                CarParkInfo carParkInfo = new CarParkInfo();
                carParkInfo.setCarParkNo(csvRecord.get(0));
                carParkInfo.setAddress(csvRecord.get(1));
                double xCoord = 30381.1007417506;
                double yCoord = 32195.1006872542;
                double[] latLon = coordinateConversionService.convert(xCoord, yCoord);
                carParkInfo.setLatitude(latLon[0]);
                carParkInfo.setLongitude(latLon[1]);


                String geohash = GeoHash.withCharacterPrecision(latLon[0], latLon[1], 6).toBase32();
                geohashMap.computeIfAbsent(geohash, k -> new ArrayList<>()).add(carParkInfo);
            }
        } catch (Exception e) {
            log.error("[CsvCarParkDataLoader][loadCarParkData] error when loading {}",e.getMessage());
        }
    }

    public List<CarParkInfo> getCarParksByGeohash(String geohashPrefix) {
        List<CarParkInfo> results = new ArrayList<>();
        for (Map.Entry<String, List<CarParkInfo>> entry : geohashMap.entrySet()) {
            if (entry.getKey().startsWith(geohashPrefix)) {
                results.addAll(entry.getValue());
            }
        }
        return results;
    }
}
