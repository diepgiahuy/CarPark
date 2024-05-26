package org.example.carpark.infrastructure.repository;

import org.example.carpark.domain.model.CarParkInfo;
import org.example.carpark.domain.repository.CarParkInfoRepository;
import org.example.carpark.infrastructure.persistence.JpaCarParkInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CarParkInfoRepositoryImpl implements CarParkInfoRepository {

    private final JpaCarParkInfoRepository jpaCarParkInfoRepository;


    @Autowired
    public CarParkInfoRepositoryImpl(JpaCarParkInfoRepository jpaCarParkInfoRepository) {
        this.jpaCarParkInfoRepository = jpaCarParkInfoRepository;
    }
    @Override
    public List<CarParkInfo> saveAll(Iterable<CarParkInfo> carParkInfo) {
        return jpaCarParkInfoRepository.saveAll(carParkInfo);
    }
    @Override
    public long count(){
        return jpaCarParkInfoRepository.count();
    }
}
