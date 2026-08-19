package com.practice.events_service.service.publicService.impl;

import com.practice.events_service.amqp.StatsEventPublisher;
import com.practice.events_service.dto.modelDTO.EventFullDTO;
import com.practice.events_service.enums.Sort;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PublicEventsServiceImpl implements PublicEventsService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CheckService checkService;
    private final StatsClient statsClient;
    private final StatsEventPublisher statsEventPublisher;

    @Override
    public List<EventShortDTO> getPublishedEvents(String text,
                                                  Long[] categories,
                                                  Boolean paid,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  Boolean onlyAvailable,
                                                  Sort sort,
                                                  int from,
                                                  int size,
                                                  HttpServletRequest request) throws IOException, InterruptedException, URISyntaxException {
        checkService.fromAndSizeCheck(from, size);
        checkService.startAndEndTimeCheck(rangeStart, rangeEnd);

        if (sort != Sort.EVENT_DATE && sort != Sort.VIEWS) {
            sort = Sort.NULL;
        }

        List<Event> events = eventRepository.publicGetPublishedEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort.toString(), from, size);
        postEndpointHit(null, request);

        return events.stream().map(eventMapper::eventToEventShortDTO).toList();
    }

    @Override
    public EventFullDTO getPublishedEventById(Long eventId, HttpServletRequest request) throws IOException, InterruptedException, URISyntaxException {
        Optional<Event> findPublishedEvent = eventRepository.getPublishedEventById(eventId);
        if (findPublishedEvent.isEmpty()) {
            throw new EventNotFoundException("Событие с id=" + eventId + " не найдено в базе данных!");
        }

        Event event = findPublishedEvent.get();

        if (postEndpointHit(eventId, request)) {
            event.setViews(event.getViews() + 1);
        }

        return eventMapper.eventToEventFullDTO(event);
    }

    private boolean postEndpointHit(Long eventId, HttpServletRequest request) throws URISyntaxException, IOException, InterruptedException {
        if (request == null) {
            return false;
        }

        boolean viewCounted = false;

        if (statsClient.checkServiceAvailability() && eventId != null && statsClient.checkIpExistsByUri(request) == false) {
            eventRepository.incrementEventViews(eventId);
            viewCounted = true;
        }

        statsEventPublisher.publishHit(request);

        return viewCounted;
    }
}

