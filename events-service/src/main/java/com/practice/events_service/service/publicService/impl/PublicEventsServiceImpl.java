package com.practice.events_service.service.publicService.impl;

import com.practice.events_service.dto.modelDTO.EventFullDTO;
import com.practice.events_service.dto.shortDTO.EventShortDTO;
import com.practice.events_service.exception.not_found.EventNotFoundException;
import com.practice.events_service.mapper.EventMapper;
import com.practice.events_service.model.Event;
import com.practice.events_service.repository.EventRepository;
import com.practice.events_service.service.publicService.PublicEventsService;
import com.practice.events_service.utils.CheckService;
import com.practice.stats_client.StatsClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PublicEventsServiceImpl implements PublicEventsService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CheckService checkService;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDTO> getPublishedEvents(String text,
                                                  Long[] categories,
                                                  Boolean paid,
                                                  String rangeStart,
                                                  String rangeEnd,
                                                  Boolean onlyAvailable,
                                                  String sort,
                                                  int from,
                                                  int size,
                                                  HttpServletRequest request) throws IOException, InterruptedException, URISyntaxException {
        checkService.fromAndSizeCheck(from, size);
        checkService.startAndEndTimeCheck(rangeStart, rangeEnd);

        if (sort != null && !sort.equals("EVENT_DATE") && !sort.equals("VIEWS")) {
            sort = null;
        }

        List<Event> events = eventRepository.publicGetPublishedEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        postEndpointHit(null, request);

        return eventMapper.eventListToEventShortDTOList(events);
    }

    @Override
    public EventFullDTO getPublishedEventById(Long eventId, HttpServletRequest request) throws IOException, InterruptedException, URISyntaxException {
        Optional<Event> findPublishedEvent = eventRepository.getPublishedEventById(eventId);
        if (findPublishedEvent.isEmpty()) {
            throw new EventNotFoundException("Событие с id=" + eventId + " не найдено в базе данных!");
        }

        postEndpointHit(eventId, request);

        return eventMapper.eventToEventFullDTO(findPublishedEvent.get());
    }

    private void postEndpointHit(Long eventId, HttpServletRequest request) throws URISyntaxException, IOException, InterruptedException {
        if (request != null && statsClient.checkServiceAvailability() == true) {
            if (eventId != null && statsClient.checkIpExistsByUri(request) == false) {
                eventRepository.incrementEventViews(eventId);
            }

            statsClient.post(request);
        }
    }
}
