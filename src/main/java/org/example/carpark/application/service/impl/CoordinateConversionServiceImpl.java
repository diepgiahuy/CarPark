package org.example.carpark.application.service.impl;

import org.example.carpark.domain.service.CoordinateConversionService;
import org.locationtech.proj4j.*;
import org.springframework.stereotype.Service;

@Service
public class CoordinateConversionServiceImpl implements CoordinateConversionService {

    private final CoordinateTransform coordinateTransform;

    public CoordinateConversionServiceImpl() {
        CRSFactory crsFactory = new CRSFactory();
        //EPSG:3414 is SVY21 format
        CoordinateReferenceSystem svy21 = crsFactory.createFromName("EPSG:3414");
        // EPSG:4326 is WGS84 fomat
        CoordinateReferenceSystem wgs84 = crsFactory.createFromName("EPSG:4326");
        CoordinateTransformFactory coordinateTransformFactory = new CoordinateTransformFactory();
        this.coordinateTransform = coordinateTransformFactory.createTransform(svy21, wgs84);
    }

    public CoordinateConversionServiceImpl(CoordinateTransform coordinateTransform) {
        this.coordinateTransform = coordinateTransform;
    }

    @Override
    public double[] convert(double xCoord, double yCoord) {
        ProjCoordinate srcCoord = new ProjCoordinate(xCoord, yCoord);
        ProjCoordinate dstCoord = new ProjCoordinate();
        coordinateTransform.transform(srcCoord, dstCoord);
        return new double[]{dstCoord.y, dstCoord.x}; // Return latitude, longitude
    }
}
