package org.example.carpark.application.service.impl;

import org.example.carpark.domain.service.CoordinateConversionService;
import org.locationtech.proj4j.*;
import org.springframework.stereotype.Service;

/**
 * Service implementation for converting coordinates between different coordinate systems.
 */
@Service
public class CoordinateConversionServiceImpl implements CoordinateConversionService {

    private final CoordinateTransform coordinateTransform;

    /**
     * Default constructor initializing the coordinate transformation from SVY21 (EPSG:3414) to WGS84 (EPSG:4326).
     */
    public CoordinateConversionServiceImpl() {
        CRSFactory crsFactory = new CRSFactory();
        //EPSG:3414 is SVY21 format
        CoordinateReferenceSystem svy21 = crsFactory.createFromName("EPSG:3414");
        // EPSG:4326 is WGS84 fomat
        CoordinateReferenceSystem wgs84 = crsFactory.createFromName("EPSG:4326");
        CoordinateTransformFactory coordinateTransformFactory = new CoordinateTransformFactory();
        this.coordinateTransform = coordinateTransformFactory.createTransform(svy21, wgs84);
    }

    /**
     * Constructor with a custom coordinate transform.
     *
     * @param coordinateTransform the coordinate transform to use
     */
    public CoordinateConversionServiceImpl(CoordinateTransform coordinateTransform) {
        this.coordinateTransform = coordinateTransform;
    }

    /**
     * Converts coordinates from one coordinate system to another.
     *
     * @param xCoord the x coordinate in the source coordinate system
     * @param yCoord the y coordinate in the source coordinate system
     * @return an array containing the converted coordinates [latitude, longitude]
     */
    @Override
    public double[] convert(double xCoord, double yCoord) {
        ProjCoordinate srcCoord = new ProjCoordinate(xCoord, yCoord);
        ProjCoordinate dstCoord = new ProjCoordinate();
        coordinateTransform.transform(srcCoord, dstCoord);
        return new double[]{dstCoord.y, dstCoord.x}; // Return latitude, longitude
    }
}
