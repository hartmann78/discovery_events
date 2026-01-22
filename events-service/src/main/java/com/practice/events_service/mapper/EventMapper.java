package com.practice.events_service.mapper;

import com.practice.events_service.dto.modelDTO.CommentDTO;
import com.practice.events_service.dto.modelDTO.EventFullDTO;
import com.practice.events_service.dto.shortDTO.EventShortDTO;
import com.practice.events_service.dto.newDTO.NewEventDTO;
import com.practice.events_service.dto.updateRequest.UpdateEventAdminRequest;
import com.practice.events_service.dto.updateRequest.UpdateEventCommentsState;
import com.practice.events_service.dto.updateRequest.UpdateEventUserRequest;
import com.practice.events_service.enums.State;
import com.practice.events_service.model.Category;
import com.practice.events_service.model.Event;
import com.practice.events_service.dto.other.Location;
import com.practice.events_service.model.User;
import com.practice.events_service.repository.EventRepository;
import com.practice.events_service.utils.CheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final CommentMapper commentMapper;
    private final EventRepository eventRepository;
    private final CheckService checkService;

    public Event createNewEvent(NewEventDTO newEventDTO, Category category, User initiator) {
        int participantLimit;
        boolean paid;
        boolean requestModeration;
        boolean commentsAvailable;
        boolean showComments;

        if (newEventDTO.getParticipantLimit() == null) {
            participantLimit = 0;
        } else {
            participantLimit = newEventDTO.getParticipantLimit();
        }

        if (newEventDTO.getPaid() == null) {
            paid = false;
        } else {
            paid = newEventDTO.getPaid();
        }

        if (newEventDTO.getRequestModeration() == null) {
            requestModeration = true;
        } else {
            requestModeration = newEventDTO.getRequestModeration();
        }

        if (newEventDTO.getCommentsAvailable() == null) {
            commentsAvailable = true;
        } else {
            commentsAvailable = newEventDTO.getCommentsAvailable();
        }

        if (newEventDTO.getShowComments() == null) {
            showComments = true;
        } else {
            showComments = newEventDTO.getShowComments();
        }

        return Event.builder()
                .title(newEventDTO.getTitle())
                .description(newEventDTO.getDescription())
                .annotation(newEventDTO.getAnnotation())
                .eventDate(newEventDTO.getEventDate())
                .initiator(initiator)
                .category(category)
                .lat(newEventDTO.getLocation().getLat())
                .lon(newEventDTO.getLocation().getLon())
                .participantLimit(participantLimit)
                .paid(paid)
                .requestModeration(requestModeration)
                .confirmedRequests(0L)
                .views(0L)
                .createdOn(LocalDateTime.now())
                .publishedOn(null)
                .state(State.PENDING)
                .comments(new ArrayList<>())
                .commentsAvailable(commentsAvailable)
                .showComments(showComments)
                .build();
    }

    public EventFullDTO eventToEventFullDTO(Event event) {
        List<CommentDTO> comments;

        if (event.getShowComments() == true) {
            comments = commentMapper.commentListToCommentDTOList(eventRepository.getEventComments(event.getId()));
        } else {
            comments = null;
        }

        return EventFullDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .initiator(userMapper.userToUserShortDTO(event.getInitiator()))
                .category(categoryMapper.categoryToCategoryDTO(event.getCategory()))
                .location(new Location(event.getLat(), event.getLon()))
                .participantLimit(event.getParticipantLimit())
                .paid(event.getPaid())
                .requestModeration(event.getRequestModeration())
                .confirmedRequests(event.getConfirmedRequests())
                .views(event.getViews())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .state(event.getState())
                .comments(comments)
                .build();
    }

    public EventShortDTO eventToEventShortDTO(Event event) {
        return EventShortDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .initiator(userMapper.userToUserShortDTO(event.getInitiator()))
                .category(categoryMapper.categoryToCategoryDTO(event.getCategory()))
                .paid(event.getPaid())
                .confirmedRequests(event.getConfirmedRequests())
                .views(event.getViews())
                .build();
    }


    public List<EventShortDTO> eventListToEventShortDTOList(List<Event> events) {
        List<EventShortDTO> eventShortDTOS = new ArrayList<>();

        for (Event event : events) {
            eventShortDTOS.add(eventToEventShortDTO(event));
        }

        return eventShortDTOS;
    }

    public List<EventFullDTO> eventListToEventEvenFullDTOList(List<Event> events) {
        List<EventFullDTO> eventFullDTOS = new ArrayList<>();

        for (Event event : events) {
            eventFullDTOS.add(eventToEventFullDTO(event));
        }

        return eventFullDTOS;
    }

    public Event patchEventByUpdateEventUserRequest(Event event, UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }

        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }

        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(checkService.findCategory(updateEventUserRequest.getCategory()));
        }

        if (updateEventUserRequest.getLocation() != null) {
            if (updateEventUserRequest.getLocation().getLat() != null) {
                event.setLat(updateEventUserRequest.getLocation().getLat());
            }

            if (updateEventUserRequest.getLocation().getLon() != null) {
                event.setLon(updateEventUserRequest.getLocation().getLon());
            }
        }

        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }

        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        if (updateEventUserRequest.getCommentsAvailable() != null) {
            event.setCommentsAvailable(updateEventUserRequest.getCommentsAvailable());
        }

        if (updateEventUserRequest.getShowComments() != null) {
            event.setShowComments(updateEventUserRequest.getShowComments());
        }

        return event;
    }

    public Event patchEventByUpdateEventAdminRequest(Event event, UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }

        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(checkService.findCategory(updateEventAdminRequest.getCategory()));
        }

        if (updateEventAdminRequest.getLocation() != null) {
            if (updateEventAdminRequest.getLocation().getLat() != null) {
                event.setLat(updateEventAdminRequest.getLocation().getLat());
            }

            if (updateEventAdminRequest.getLocation().getLon() != null) {
                event.setLon(updateEventAdminRequest.getLocation().getLon());
            }
        }

        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }

        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        if (updateEventAdminRequest.getCommentsAvailable() != null) {
            event.setCommentsAvailable(updateEventAdminRequest.getCommentsAvailable());
        }

        if (updateEventAdminRequest.getShowComments() != null) {
            event.setShowComments(updateEventAdminRequest.getShowComments());
        }

        return event;
    }

    public Event patchEventCommentsState(Event event, UpdateEventCommentsState updateEventCommentsState) {
        if (updateEventCommentsState.getCommentsAvailable() != null) {
            event.setCommentsAvailable(updateEventCommentsState.getCommentsAvailable());
        }

        if (updateEventCommentsState.getShowComments() != null) {
            event.setShowComments(updateEventCommentsState.getShowComments());
        }

        return event;
    }
}
