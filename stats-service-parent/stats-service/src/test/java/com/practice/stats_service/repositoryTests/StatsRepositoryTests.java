package com.practice.stats_service.repositoryTests;

import com.practice.stats_dto.EndpointHit;
import com.practice.stats_dto.ViewStats;
import com.practice.stats_service.generators.EndpointHitGenerator;
import com.practice.stats_service.repository.StatsRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class StatsRepositoryTests {
    @Autowired
    private StatsRepository statsRepository;

    private final EndpointHitGenerator endpointHitGenerator = new EndpointHitGenerator();

    private EndpointHit endpointHit;
    private final String app = "events-service";
    private final String uri = "/events/1";
    private final LocalDateTime eventDate = LocalDateTime.now();
    private final LocalDateTime startRange = eventDate.minusDays(1);
    private final LocalDateTime endRange = eventDate.plusDays(1);

    @Test
    @BeforeEach
    void post() {
        endpointHit = endpointHitGenerator.generate(app, uri, eventDate);
        statsRepository.save(endpointHit);

        Optional<EndpointHit> check = statsRepository.findById(endpointHit.getId());
        assertTrue(check.isPresent());
        assertNotNull(check.get().getId());
        assertEquals(endpointHit, check.get());
    }

    @Test
    void getSingleUriStats() {
        List<ViewStats> viewStats = statsRepository.getSingleUriStats(app, startRange, endRange, new String[]{uri});

        assertFalse(viewStats.isEmpty());
    }

    @Test
    void getUniqueIpSingleUriStats() {
        EndpointHit endpointHit2 = endpointHitGenerator.generate(app, uri, eventDate);
        statsRepository.save(endpointHit2);

        List<ViewStats> viewStats = statsRepository.getUniqueIpSingleUriStats(app, startRange, endRange, new String[]{uri});

        assertFalse(viewStats.isEmpty());

    }

    @Test
    void getAllUriStats() {
        EndpointHit endpointHit2 = endpointHitGenerator.generate(app, uri, eventDate);
        endpointHit2.setUri("/events/2");
        statsRepository.save(endpointHit2);

        List<ViewStats> viewStats = statsRepository.getAllUriStats(app, startRange, endRange);
        assertEquals(2, viewStats.size());
    }

    @Test
    void getUniqueIpAllUriStats() {
        EndpointHit endpointHit2 = endpointHitGenerator.generate(app, uri, eventDate);
        endpointHit2.setUri("/events/2");
        statsRepository.save(endpointHit2);

        List<ViewStats> viewStats = statsRepository.getUniqueIpAllUriStats(app, startRange, endRange);
        assertEquals(2, viewStats.size());
    }

    @Test
    void checkIpExistsByUri() {
        String uri = endpointHit.getUri();
        String ip = endpointHit.getIp();

        assertTrue(statsRepository.checkIpExistsByUri(uri, ip));
    }

    @Test
    void deleteEndpoint() {
        statsRepository.deleteById(endpointHit.getId());

        Optional<EndpointHit> check = statsRepository.findById(endpointHit.getId());
        assertTrue(check.isEmpty());
    }
}