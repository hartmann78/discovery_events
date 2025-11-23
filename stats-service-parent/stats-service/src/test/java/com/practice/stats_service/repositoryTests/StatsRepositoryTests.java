package com.practice.stats_service.repositoryTests;

import com.practice.stats_dto.EndpointHit;
import com.practice.stats_dto.ViewStats;
import com.practice.stats_service.generators.EndpointHitGenerator;
import com.practice.stats_service.repository.StatsRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StatsRepositoryTests {
    @Autowired
    private StatsRepository statsRepository;

    private final EndpointHitGenerator endpointHitGenerator = new EndpointHitGenerator();
    private EndpointHit endpointHit;
    private static final String app = "events-service";
    private static final String uri = "/events/1";
    private static final String startRange = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).minusDays(1).toString();
    private static final String endRange = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(1).toString();

    @BeforeEach
    void create() {
        endpointHit = endpointHitGenerator.generate();
        statsRepository.save(endpointHit);
    }

    @Test
    @Order(1)
    void post() {
        assertNotNull(endpointHit.getId());

        Optional<EndpointHit> check = statsRepository.findById(endpointHit.getId());
        assertTrue(check.isPresent());
        assertEquals(endpointHit, check.get());
    }

    @Test
    @Order(2)
    void getSingleUriStats() {
        List<ViewStats> viewStats = statsRepository.getSingleUriStats(app, startRange, endRange, new String[]{uri});

        assertFalse(viewStats.isEmpty());
    }

    @Test
    @Order(3)
    void getUniqueIpSingleUriStats() {
        EndpointHit endpointHit2 = endpointHitGenerator.generate();
        statsRepository.save(endpointHit2);

        List<ViewStats> viewStats = statsRepository.getUniqueIpSingleUriStats(app, startRange, endRange, new String[]{uri});

        assertFalse(viewStats.isEmpty());

    }

    @Test
    @Order(4)
    void getAllUriStats() {
        EndpointHit endpointHit2 = endpointHitGenerator.generate();
        endpointHit2.setUri("/events/2");
        statsRepository.save(endpointHit2);

        List<ViewStats> viewStats = statsRepository.getAllUriStats(app, startRange, endRange);
        assertEquals(2, viewStats.size());
    }

    @Test
    @Order(5)
    void getUniqueIpAllUriStats() {
        EndpointHit endpointHit2 = endpointHitGenerator.generate();
        endpointHit2.setUri("/events/2");
        statsRepository.save(endpointHit2);

        List<ViewStats> viewStats = statsRepository.getUniqueIpAllUriStats(app, startRange, endRange);
        assertEquals(2, viewStats.size());
    }

    @Test
    @Order(6)
    void checkIpExistsByUri() {
        String uri = endpointHit.getUri();
        String ip = endpointHit.getIp();

        assertTrue(statsRepository.checkIpExistsByUri(uri, ip));
    }

    @Test
    @Order(7)
    void deleteEndpoint() {
        statsRepository.deleteById(endpointHit.getId());

        Optional<EndpointHit> check = statsRepository.findById(endpointHit.getId());
        assertTrue(check.isEmpty());
    }
}