package org.example.carpark.infrastructure.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.carpark.application.dto.CarParkInfoResponse;
import org.example.carpark.domain.model.CarParkAvailability;
import org.example.carpark.domain.model.CarParkInfo;
import org.example.carpark.infrastructure.persistence.JpaCarParkAvailabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class CarParkAvailabilityRepositoryImplTest {

    @Autowired
    private JpaCarParkAvailabilityRepository carParkAvailabilityRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() {
        // Clear existing data
        jdbcTemplate.execute("DELETE FROM car_park_availability");
        jdbcTemplate.execute("DELETE FROM car_park_info");

        // Insert test data
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

        assertThat(result.getTotalElements()).isEqualTo(10);
        CarParkInfoResponse carParkInfoResponse = result.getContent().get(0);
        assertThat(carParkInfoResponse.getAddress()).isEqualTo("Address1");
        assertThat(carParkInfoResponse.getAvailableLots()).isEqualTo(50);
        assertThat(carParkInfoResponse.getTotalLots()).isEqualTo(100);
    }

    @Test
    public void testSaveAll() {
        CarParkAvailability carParkAvailability3 = new CarParkAvailability("CarPark3", 100, 75);
        CarParkAvailability carParkAvailability4 = new CarParkAvailability("CarPark4", 200, 125);

        List<CarParkAvailability> savedEntities = carParkAvailabilityRepository.saveAll(
                List.of(carParkAvailability3, carParkAvailability4)
        );

        assertThat(savedEntities).hasSize(2);
        assertThat(savedEntities).extracting("carParkNo").containsExactlyInAnyOrder("CarPark3", "CarPark4");
    }

    @Test
    public void testFindAllCarPark_OrderedByDistance() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CarParkInfoResponse> result = carParkAvailabilityRepository.findAvailableCarParks(1.0, 1.0, pageable);

        List<CarParkInfoResponse> content = result.getContent();

        assertThat(content).isNotEmpty();
        assertThat(content).isSortedAccordingTo((a, b) -> Double.compare(a.getDistance(), b.getDistance()));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public DataSource dataSource() {
            return org.springframework.boot.jdbc.DataSourceBuilder.create()
                    .url("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1")
                    .driverClassName("org.h2.Driver")
                    .username("sa")
                    .password("")
                    .build();
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }
    }
}
