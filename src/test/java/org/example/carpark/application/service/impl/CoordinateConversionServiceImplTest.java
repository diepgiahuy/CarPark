package org.example.carpark.application.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class CoordinateConversionServiceImplTest {

    private CoordinateConversionServiceImpl coordinateConversionService;

    @BeforeEach
    void setUp() {
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem svy21 = crsFactory.createFromName("EPSG:3414");
        CoordinateReferenceSystem wgs84 = crsFactory.createFromName("EPSG:4326");
        CoordinateTransformFactory coordinateTransformFactory = new CoordinateTransformFactory();
        CoordinateTransform coordinateTransform = coordinateTransformFactory.createTransform(svy21, wgs84);
        coordinateConversionService = new CoordinateConversionServiceImpl(coordinateTransform);
    }

    @Test
    public void testConvert_Success() {
        double[] result = coordinateConversionService.convert(28001.642, 38744.572);
        double[] expected = {1.3666666666666671, 103.8333333333333};

        assertArrayEquals(expected, result, 0.0001);
    }
}
