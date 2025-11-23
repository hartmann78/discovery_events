package com.practice.stats_service.serviceTests;

import com.practice.stats_dto.EndpointHit;
import com.practice.stats_dto.ViewStats;
import com.practice.stats_service.generators.EndpointHitGenerator;
import com.practice.stats_service.service.StatsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StatsServiceTests {
    @Autowired
    private StatsService statsService;
    @Autowired
    private EndpointHitGenerator endpointHitGenerator;

    private static final String startRange = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).minusDays(1).toString();
    private static final String endRange = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(1).toString();

    private static EndpointHit endpointHit;

    @Test
    @Order(1)
    void post() {
        endpointHit = endpointHitGenerator.generate();
        assertDoesNotThrow(() -> statsService.post(endpointHit));
        assertNotNull(endpointHit.getId());
    }

    @Test
    @Order(2)
    void getUriStats() {
        String[] uris = new String[]{"/events/1"};

        List<ViewStats> viewStats = statsService.get(startRange, endRange, false, uris);
        assertEquals(1, viewStats.size());
    }

    @Test
    @Order(3)
    void getUniqueIpUriStats() {
        String[] uris = new String[]{"/events/1"};

        List<ViewStats> viewStats = statsService.get(startRange, endRange, true, uris);
        assertEquals(1, viewStats.size());
    }

    @Test
    @Order(4)
    void getStatsWithoutUris() {
        EndpointHit endpointHit = endpointHitGenerator.generate();
        endpointHit.setUri("/events/2");
        statsService.post(endpointHit);

        String[] uris = new String[]{};

        List<ViewStats> viewStats = statsService.get(startRange, endRange, false, uris);
        assertEquals(2, viewStats.size());
    }

    @Test
    @Order(5)
    void getUniqueIpStatsWithoutUris() {
        EndpointHit endpointHit = endpointHitGenerator.generate();
        endpointHit.setUri("/events/3");
        statsService.post(endpointHit);

        String[] uris = new String[]{};

        List<ViewStats> viewStats = statsService.get(startRange, endRange, true, uris);
        assertEquals(3, viewStats.size());
    }

    @Test
    @Order(6)
    void checkIpExistsByUri() {
        String uri = endpointHit.getUri();
        String ip = endpointHit.getIp();

        assertTrue(statsService.checkIpExistsByUri(uri, ip));
    }
}
