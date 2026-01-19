package com.practice.events_service.controller.publicControllers;

import com.practice.events_service.dto.modelDTO.EventFullDTO;
import com.practice.events_service.enums.Sort;
import com.practice.events_service.dto.shortDTO.EventShortDTO;
import com.practice.events_service.service.publicService.PublicEventsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

import static com.practice.events_service.utils.SetLog.setLog;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventsController {
    private final PublicEventsService publicEventsService;

    @GetMapping
    public ResponseEntity<List<EventShortDTO>> getPublishedEvents(@RequestParam(required = false) String text,
                                                                  @RequestParam(required = false) Long[] categories,
                                                                  @RequestParam(required = false) Boolean paid,
                                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                                  @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                                                  @RequestParam(required = false) Sort sort,
                                                                  @RequestParam(required = false, defaultValue = "0") int from,
                                                                  @RequestParam(required = false, defaultValue = "10") int size,
                                                                  HttpServletRequest request) throws IOException, InterruptedException, URISyntaxException {
        setLog(log, request);
        return new ResponseEntity<>(publicEventsService.getPublishedEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request), HttpStatus.OK);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDTO> getPublishedEventById(@PathVariable Long eventId,
                                                              HttpServletRequest request) throws IOException, InterruptedException, URISyntaxException {
        setLog(log, request);
        return new ResponseEntity<>(publicEventsService.getPublishedEventById(eventId, request), HttpStatus.OK);
    }
}
