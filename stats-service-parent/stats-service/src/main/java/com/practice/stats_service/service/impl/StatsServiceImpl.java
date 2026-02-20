package com.practice.stats_service.service.impl;

import com.practice.stats_dto.EndpointHit;
import com.practice.stats_dto.ViewStats;
import com.practice.stats_service.repository.StatsRepository;
import com.practice.stats_service.service.StatsService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public List<ViewStats> get(LocalDateTime start, LocalDateTime end, Boolean unique, String[] uris) {
        String app = "events-service";
        List<ViewStats> answer;

        if (uris == null || uris.length == 0) {
            if (unique == true) {
                answer = statsRepository.getUniqueIpAllUriStats(app, start, end);
            } else {
                answer = statsRepository.getAllUriStats(app, start, end);
            }
        } else {
            if (unique == true) {
                answer = statsRepository.getUniqueIpSingleUriStats(app, start, end, uris);
            } else {
                answer = statsRepository.getSingleUriStats(app, start, end, uris);
            }
        }

        if (answer.size() >= 2) {
            answer.sort(Comparator.comparingLong(ViewStats::getHits).reversed());
        }

        return answer;
    }

    @Override
    public void post(EndpointHit endpointHit) {
        statsRepository.save(endpointHit);
    }

    @Override
    public boolean checkIpExistsByUri(String uri, String ip) {
        return statsRepository.checkIpExistsByUri(uri, ip);
    }
}
