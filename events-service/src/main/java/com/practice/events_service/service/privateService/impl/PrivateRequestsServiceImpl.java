package com.practice.events_service.service.privateService.impl;

import com.practice.events_service.dto.modelDTO.ParticipationRequestDTO;
import com.practice.events_service.enums.State;
import com.practice.events_service.exception.other.ForbiddenException;
import com.practice.events_service.exception.conflict.*;
import com.practice.events_service.mapper.ParticipationRequestMapper;
import com.practice.events_service.model.Event;
import com.practice.events_service.model.ParticipationRequest;
import com.practice.events_service.model.User;
import com.practice.events_service.repository.EventRepository;
import com.practice.events_service.repository.ParticipationRequestRepository;
import com.practice.events_service.service.privateService.PrivateRequestsService;
import com.practice.events_service.utils.CheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivateRequestsServiceImpl implements PrivateRequestsService {
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;
    private final CheckService checkService;

    @Override
    public List<ParticipationRequestDTO> getUserEventRequests(Long userId) {
        checkService.findUser(userId);
        List<ParticipationRequest> requests = participationRequestRepository.getRequesterRequests(userId);

        return participationRequestMapper.requestListToRequestDTOList(requests);
    }

    @Override
    public ParticipationRequestDTO postEventParticipationRequest(Long userId, Long eventId) {
        User user = checkService.findUser(userId);
        Event event = checkService.findEvent(eventId);

        if (participationRequestRepository.checkRequestExists(userId, eventId)) {
            throw new RequestExistsException("Запрос на участие уже существует!");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new RequestIsFromInitiatorException("Нельзя добавить запрос от инициатора события!");
        }

        if (event.getState() != State.PUBLISHED) {
            throw new EventNotPublishedException("Событие ещё не опубликовано!");
        }

        if (eventRepository.eventContainsConfirmedRequests(eventId) &&
                eventRepository.getAvailableRequestsCount(eventId) == 0) {
            throw new EventIsUnavailableException("Исчерпан лимит запросов на событие!");
        }

        ParticipationRequest participationRequest = participationRequestMapper.createNewParticipationRequest(user, event);

        if (event.getParticipantLimit() == 0 || event.getRequestModeration() == false) {
            participationRequest.setStatus(ParticipationRequest.Status.CONFIRMED);
        } else {
            participationRequest.setStatus(ParticipationRequest.Status.PENDING);
        }

        participationRequestRepository.save(participationRequest);

        if (participationRequest.getStatus() == ParticipationRequest.Status.CONFIRMED) {
            eventRepository.updateConfirmedRequestsCount(eventId);
        }

        return participationRequestMapper.requestToRequestDTO(participationRequest);
    }

    @Override
    public ParticipationRequestDTO cancelEventParticipationRequest(Long userId, Long requestId) {
        checkService.findUser(userId);
        ParticipationRequest request = checkService.findParticipationRequest(requestId);

        if (!request.getRequester().getId().equals(userId)) {
            throw new ForbiddenException("id пользователя не совпадает с id создателя заявки!");
        }

        participationRequestRepository.cancelRequest(userId, requestId);

        return participationRequestMapper.requestToRequestDTO(checkService.findParticipationRequest(requestId));
    }
}
