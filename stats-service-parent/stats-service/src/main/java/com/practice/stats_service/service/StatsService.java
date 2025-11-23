package com.practice.stats_service.service;

import com.practice.stats_dto.EndpointHit;
import com.practice.stats_dto.ViewStats;

import java.util.List;

public interface StatsService {
    List<ViewStats> get(String start, String end, Boolean unique, String[] uris);

    void post(EndpointHit endpointHit);

    boolean checkIpExistsByUri(String uri, String ip);
}
