package com.practice.events_service.service.adminService.impl;

import com.practice.events_service.dto.modelDTO.EventFullDTO;
import com.practice.events_service.dto.updateRequest.UpdateEventAdminRequest;
import com.practice.events_service.dto.updateRequest.UpdateEventCommentsState;
import com.practice.events_service.exception.conflict.EventIsCanceledException;
import com.practice.events_service.exception.conflict.EventIsPublishedException;
import com.practice.events_service.exception.other.BadRequestException;
import com.practice.events_service.mapper.EventMapper;
import com.practice.events_service.model.Category;
import com.practice.events_service.model.Event;
import com.practice.events_service.repository.EventRepository;
import com.practice.events_service.service.adminService.AdminEventsService;
import com.practice.events_service.utils.CheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AdminEventsServiceImpl implements AdminEventsService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CheckService checkService;

    @Override
    public List<EventFullDTO> getEvents(Long[] users,
                                        String[] states,
                                        Long[] categories,
                                        String rangeStart,
                                        String rangeEnd,
                                        int from,
                                        int size) {
        checkService.fromAndSizeCheck(from, size);
        checkService.startAndEndTimeCheck(rangeStart, rangeEnd);

        List<Event> events = eventRepository.adminGetEvents(users, states, categories, rangeStart, rangeEnd, from, size);

        return eventMapper.eventListToEventEvenFullDTOList(events);
    }

    @Override
    public EventFullDTO patchEventById(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = checkService.findEvent(eventId);

        if (event.getState() == Event.State.PUBLISHED) {
            throw new EventIsPublishedException("Событие уже опубликовано!");
        }

        if (event.getState() == Event.State.CANCELED) {
            throw new EventIsCanceledException("Событие уже отменено!");
        }

        Category category = Stream.ofNullable(updateEventAdminRequest.getCategory())
                .map(checkService::findCategory)
                .findAny()
                .orElse(null);

        checkService.eventDateAfterNowPlusTwoHoursCheck(updateEventAdminRequest.getEventDate());

        event = eventMapper.patchEventByUpdateEventAdminRequest(event, updateEventAdminRequest, category);

        if (updateEventAdminRequest.getStateAction() == UpdateEventAdminRequest.StateAction.PUBLISH_EVENT) {
            checkService.eventDateAfterPublicationPlusOneHourCheck(event.getEventDate());

            event.setPublishedOn(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            event.setState(Event.State.PUBLISHED);
        } else if (updateEventAdminRequest.getStateAction() == UpdateEventAdminRequest.StateAction.REJECT_EVENT) {
            event.setState(Event.State.CANCELED);
        }

        eventRepository.save(event);

        return eventMapper.eventToEventFullDTO(event);
    }

    @Override
    public EventFullDTO patchEventCommentsState(Long eventId, UpdateEventCommentsState updateEventCommentsState) {
        if (updateEventCommentsState.getShowComments() == null && updateEventCommentsState.getCommentsAvailable()) {
            throw new BadRequestException("Отсутствуют параметры!");
        }

        Event event = checkService.findEvent(eventId);
        event = eventMapper.patchEventCommentsState(event, updateEventCommentsState);

        eventRepository.save(event);

        return eventMapper.eventToEventFullDTO(event);
    }
}
