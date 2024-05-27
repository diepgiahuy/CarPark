package org.example.carpark.infrastructure.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.carpark.TestDataUtil;
import org.example.carpark.domain.model.CarParkInfo;
import org.example.carpark.domain.repository.CarParkInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
@Import(TestDataUtil.TestConfig.class)
public class CarParkInfoRepositoryImplTest {

    @Autowired
    private CarParkInfoRepository carParkInfoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    public void setup() {
        CarParkInfo carParkInfo1 = new CarParkInfo("CarPark1", "Address1", 1.0, 1.0);
        CarParkInfo carParkInfo2 = new CarParkInfo("CarPark2", "Address2", 2.0, 2.0);

        entityManager.persist(carParkInfo1);
        entityManager.persist(carParkInfo2);

        entityManager.flush();
    }

    @Test
    public void testSaveAll() {
        CarParkInfo carParkInfo3 = new CarParkInfo("CarPark3", "Address3", 3.0, 3.0);
        CarParkInfo carParkInfo4 = new CarParkInfo("CarPark4", "Address4", 4.0, 4.0);

        List<CarParkInfo> savedEntities = carParkInfoRepository.saveAll(
                List.of(carParkInfo3, carParkInfo4)
        );
        assertEquals(savedEntities.size(),2);
        assertTrue(savedEntities.containsAll(List.of(carParkInfo3,carParkInfo4)));
    }

    @Test
    public void testCount() {
        long count = carParkInfoRepository.count();
        assertEquals(count,2);
    }

}
