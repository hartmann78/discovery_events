package com.practice.stats_service.service;

import com.practice.stats_dto.EndpointHit;
import com.practice.stats_dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    List<ViewStats> get(LocalDateTime start, LocalDateTime end, Boolean unique, String[] uris);

    void post(EndpointHit endpointHit);

    boolean checkIpExistsByUri(String uri, String ip);
}
