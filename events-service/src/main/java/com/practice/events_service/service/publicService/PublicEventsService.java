package com.practice.events_service.service.publicService;

import com.practice.events_service.dto.modelDTO.EventFullDTO;
import com.practice.events_service.enums.Sort;
import com.practice.events_service.dto.shortDTO.EventShortDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventsService {
    List<EventShortDTO> getPublishedEvents(String text,
                                           Long[] categories,
                                           Boolean paid,
                                           LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd,
                                           Boolean onlyAvailable,
                                           Sort sort,
                                           int from,
                                           int size,
                                           HttpServletRequest request) throws IOException, InterruptedException, URISyntaxException;

    EventFullDTO getPublishedEventById(Long eventId, HttpServletRequest request) throws IOException, InterruptedException, URISyntaxException;
}
