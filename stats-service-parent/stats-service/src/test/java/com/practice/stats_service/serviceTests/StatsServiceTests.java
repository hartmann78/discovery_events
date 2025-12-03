package com.practice.stats_service.serviceTests;

import com.practice.stats_dto.EndpointHit;
import com.practice.stats_dto.ViewStats;
import com.practice.stats_service.generators.EndpointHitGenerator;
import com.practice.stats_service.service.StatsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StatsServiceTests {
    @Autowired
    private StatsService statsService;
    @Autowired
    private EndpointHitGenerator endpointHitGenerator;

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
        assertDoesNotThrow(() -> statsService.post(endpointHit));
        assertNotNull(endpointHit.getId());
    }

    @Test
    void getUriStats() {
        List<ViewStats> viewStats = statsService.get(startRange, endRange, false, new String[]{uri});
        assertEquals(1, viewStats.size());
    }

    @Test
    void getUniqueIpUriStats() {
        List<ViewStats> viewStats = statsService.get(startRange, endRange, true, new String[]{uri});
        assertEquals(1, viewStats.size());
    }

    @Test
    void getStatsWithoutUris() {
        String uri2 = "/events/2";

        EndpointHit endpointHit = endpointHitGenerator.generate(app, uri2, eventDate);
        statsService.post(endpointHit);

        List<ViewStats> viewStats = statsService.get(startRange, endRange, false, new String[]{uri2});
        assertFalse(viewStats.isEmpty());
    }

    @Test
    void getUniqueIpStatsWithoutUris() {
        String uri3 = "/events/3";

        EndpointHit endpointHit = endpointHitGenerator.generate(app, uri3, eventDate);
        statsService.post(endpointHit);

        List<ViewStats> viewStats = statsService.get(startRange, endRange, true, new String[]{uri3});
        assertFalse(viewStats.isEmpty());
    }

    @Test
    void checkIpExistsByUri() {
        String uri = endpointHit.getUri();
        String ip = endpointHit.getIp();

        assertTrue(statsService.checkIpExistsByUri(uri, ip));
    }
}
