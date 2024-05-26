package org.example.carpark.infrastructure.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.carpark.domain.model.CarParkInfo;
import org.example.carpark.infrastructure.persistence.JpaCarParkInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(CarParkInfoRepositoryImplTest.TestConfig.class)
public class CarParkInfoRepositoryImplTest {

    @Autowired
    private CarParkInfoRepositoryImpl carParkInfoRepository;

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

        assertThat(savedEntities).hasSize(2);
        assertThat(savedEntities).extracting("carParkNo").containsExactlyInAnyOrder("CarPark3", "CarPark4");
    }

    @Test
    public void testCount() {
        long count = carParkInfoRepository.count();
        assertThat(count).isEqualTo(2);
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .build();
        }

        @Bean
        public CarParkInfoRepositoryImpl carParkInfoRepository(JpaCarParkInfoRepository jpaCarParkInfoRepository) {
            return new CarParkInfoRepositoryImpl(jpaCarParkInfoRepository);
        }
    }
}
