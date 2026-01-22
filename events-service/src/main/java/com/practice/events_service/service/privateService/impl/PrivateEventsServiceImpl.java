package com.practice.events_service.service.privateService.impl;

import com.practice.events_service.dto.modelDTO.EventFullDTO;
import com.practice.events_service.dto.eventRequestStatusUpdate.EventRequestStatusUpdateRequest;
import com.practice.events_service.dto.eventRequestStatusUpdate.EventRequestStatusUpdateResult;
import com.practice.events_service.dto.shortDTO.EventShortDTO;
import com.practice.events_service.dto.newDTO.NewEventDTO;
import com.practice.events_service.dto.modelDTO.ParticipationRequestDTO;
import com.practice.events_service.dto.updateRequest.UpdateEventCommentsState;
import com.practice.events_service.dto.updateRequest.UpdateEventUserRequest;
import com.practice.events_service.enums.State;
import com.practice.events_service.exception.other.BadRequestException;
import com.practice.events_service.exception.other.ForbiddenException;
import com.practice.events_service.exception.conflict.*;
import com.practice.events_service.exception.not_found.EventNotFoundException;
import com.practice.events_service.mapper.EventMapper;
import com.practice.events_service.mapper.ParticipationRequestMapper;
import com.practice.events_service.model.*;
import com.practice.events_service.repository.EventRepository;
import com.practice.events_service.repository.ParticipationRequestRepository;
import com.practice.events_service.service.privateService.PrivateEventsService;
import com.practice.events_service.utils.CheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrivateEventsServiceImpl implements PrivateEventsService {
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final EventMapper eventMapper;
    private final ParticipationRequestMapper participationRequestMapper;
    private final CheckService checkService;

    @Override
    public List<EventShortDTO> getEvents(Long userId, int from, int size) {
        checkService.fromAndSizeCheck(from, size);
        checkService.findUser(userId);

        List<Event> events = eventRepository.getInitiatorEvents(userId, from, size);

        return eventMapper.eventListToEventShortDTOList(events);
    }

    @Override
    public EventFullDTO getEventByUserId(Long userId, Long eventId) {
        Optional<Event> findEvent = eventRepository.getEventByInitiatorId(userId, eventId);
        if (findEvent.isEmpty()) {
            throw new EventNotFoundException("Событие с id=" + eventId + " и инициатором с id=" + userId + " не найдено в базе данных!");
        }

        return eventMapper.eventToEventFullDTO(findEvent.get());
    }

    @Override
    public List<ParticipationRequestDTO> getEventRequestsByUserIdAndEventId(Long userId, Long eventId) {
        checkService.findUser(userId);
        checkService.findEvent(eventId);

        List<ParticipationRequest> requests = participationRequestRepository.getEventInitiatorRequests(userId, eventId);

        return participationRequestMapper.requestListToRequestDTOList(requests);
    }

    @Override
    public EventFullDTO addNewEvent(Long userId, NewEventDTO newEventDTO) {
        User user = checkService.findUser(userId);
        Category category = checkService.findCategory(newEventDTO.getCategory());
        checkService.eventDateAfterNowPlusTwoHoursCheck(newEventDTO.getEventDate());

        Event event = eventMapper.createNewEvent(newEventDTO, category, user);
        eventRepository.save(event);

        return eventMapper.eventToEventFullDTO(event);
    }

    @Override
    public EventFullDTO patchEventByUserId(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        checkService.findUser(userId);
        Event event = checkService.findEvent(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("id пользователя не совпадает с id организатора события!");
        }

        if (event.getState() == State.PUBLISHED) {
            throw new EventIsPublishedException("Событие уже опубликовано!");
        }

        checkService.eventDateAfterNowPlusTwoHoursCheck(updateEventUserRequest.getEventDate());

        event = eventMapper.patchEventByUpdateEventUserRequest(event, updateEventUserRequest);

        if (updateEventUserRequest.getStateAction() == UpdateEventUserRequest.StateAction.SEND_TO_REVIEW) {
            event.setState(State.PENDING);
        } else if (updateEventUserRequest.getStateAction() == UpdateEventUserRequest.StateAction.CANCEL_REVIEW) {
            event.setState(State.CANCELED);
        }

        eventRepository.save(event);

        return eventMapper.eventToEventFullDTO(event);
    }

    @Override
    public EventRequestStatusUpdateResult patchEventRequestsByUserId(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        checkService.findUser(userId);
        Event event = checkService.findEvent(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("id пользователя не совпадает с id организатора события!");
        }

        List<Long> requestIds = eventRequestStatusUpdateRequest.getRequestIds();
        EventRequestStatusUpdateRequest.Status updateStatus = eventRequestStatusUpdateRequest.getStatus();

        for (Long requestId : requestIds) {
            ParticipationRequest request = checkService.findParticipationRequest(requestId);
            checkService.checkRequestStatusForPatch(request.getStatus(), updateStatus);
        }

        if (eventRepository.getAvailableRequestsCount(eventId) < requestIds.size() &&
                updateStatus == EventRequestStatusUpdateRequest.Status.CONFIRMED) {
            throw new TooManyConfirmedRequestsException("Количество подтверждений запросов превышает количество свободных мест события!");
        }

        if (updateStatus == EventRequestStatusUpdateRequest.Status.CONFIRMED) {
            participationRequestRepository.updateRequests(requestIds, ParticipationRequest.Status.CONFIRMED.toString());
        } else if (updateStatus == EventRequestStatusUpdateRequest.Status.REJECTED) {
            participationRequestRepository.updateRequests(requestIds, ParticipationRequest.Status.REJECTED.toString());
        }

        eventRepository.updateConfirmedRequestsCount(eventId);

        List<ParticipationRequest> requests = participationRequestRepository.getAllConfirmedAndRejectedRequests(eventId);

        return participationRequestMapper.requestsListToEventRequestStatusUpdateResult(requests);
    }

    @Override
    public EventFullDTO patchEventCommentsState(Long userId, Long eventId, UpdateEventCommentsState updateEventCommentsState) {
        if (updateEventCommentsState.getShowComments() == null && updateEventCommentsState.getCommentsAvailable()) {
            throw new BadRequestException("Отсутствуют параметры!");
        }

        checkService.findUser(userId);
        Event event = checkService.findEvent(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("id пользователя не совпадает с id организатора события!");
        }

        event = eventMapper.patchEventCommentsState(event, updateEventCommentsState);
        eventRepository.save(event);

        return eventMapper.eventToEventFullDTO(event);
    }
}