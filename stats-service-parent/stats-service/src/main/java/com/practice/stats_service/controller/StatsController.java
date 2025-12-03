package com.practice.stats_service.controller;

import com.practice.stats_dto.EndpointHit;
import com.practice.stats_dto.ViewStats;
import com.practice.stats_service.service.StatsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHit> postEndpointHit(@RequestBody @Valid EndpointHit endpointHit, HttpServletRequest request) {
        statsService.post(endpointHit);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStats>> getViewStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                        @RequestParam(required = false, defaultValue = "false") Boolean unique,
                                                        @RequestParam(required = false) String[] uris,
                                                        HttpServletRequest request) {
        return new ResponseEntity<>(statsService.get(start, end, unique, uris), HttpStatus.OK);
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkIpExistsByUri(@RequestParam String uri,
                                                      @RequestParam String ip,
                                                      HttpServletRequest request) {
        return new ResponseEntity<>(statsService.checkIpExistsByUri(uri, ip), HttpStatus.OK);
    }
}

