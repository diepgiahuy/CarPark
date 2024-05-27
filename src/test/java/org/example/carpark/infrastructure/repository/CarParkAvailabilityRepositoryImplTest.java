package org.example.carpark.infrastructure.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.carpark.TestDataUtil;
import org.example.carpark.application.dto.CarParkInfoResponse;
import org.example.carpark.domain.model.CarParkAvailability;
import org.example.carpark.domain.model.CarParkInfo;
import org.example.carpark.domain.repository.CarParkAvailabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
@Import(TestDataUtil.TestConfig.class)
public class CarParkAvailabilityRepositoryImplTest {

    @Autowired
    private CarParkAvailabilityRepository carParkAvailabilityRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @BeforeEach
    public void setup() {
        for (int i = 1; i <= 20; i++) {
            CarParkInfo carParkInfo = new CarParkInfo("CarPark" + i, "Address" + i, i * 1.0, i * 1.0);
            entityManager.persist(carParkInfo);

            CarParkAvailability carParkAvailability = new CarParkAvailability("CarPark" + i, 100, i % 2 == 0 ? 0 : 50);
            entityManager.persist(carParkAvailability);
        }

        entityManager.flush();
    }

    @Test
    public void testFindAvailableCarParks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CarParkInfoResponse> result = carParkAvailabilityRepository.findAvailableCarParks(1.0, 1.0, pageable);

        assertEquals(result.getTotalElements(),10);

        CarParkInfoResponse carParkInfoResponse = result.getContent().get(0);
        assertEquals(carParkInfoResponse.getAddress(),"Address1");
        assertEquals(carParkInfoResponse.getAvailableLots(),50);
        assertEquals(carParkInfoResponse.getTotalLots(),100);
    }

    @Test
    public void testSaveAll() {
        CarParkAvailability carParkAvailability3 = new CarParkAvailability("CarPark3", 100, 75);
        CarParkAvailability carParkAvailability4 = new CarParkAvailability("CarPark4", 200, 125);

        List<CarParkAvailability> savedEntities = carParkAvailabilityRepository.saveAll(
                List.of(carParkAvailability3, carParkAvailability4)
        );

        assertEquals(savedEntities.size(),2);
        assertTrue(savedEntities.containsAll(List.of(carParkAvailability3, carParkAvailability4)));
    }

    @Test
    public void testFindAllCarPark_OrderedByDistance() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CarParkInfoResponse> result = carParkAvailabilityRepository.findAvailableCarParks(1.0, 1.0, pageable);

        List<CarParkInfoResponse> content = result.getContent();
        assertFalse(content.isEmpty());

        boolean isSorted = true;
        for (int i = 0; i < content.size() - 1; i++) {
            if(content.get(i).getDistance() > content.get(i + 1).getDistance()){
                isSorted = false;
                break;
            }
        }
        assertTrue(isSorted, "The list is sorted ascending order");
    }
}
