package com.practice.events_service.serviceTests.privateService;

import com.practice.events_service.dto.eventRequestStatusUpdate.EventRequestStatusUpdateRequest;
import com.practice.events_service.dto.eventRequestStatusUpdate.EventRequestStatusUpdateResult;
import com.practice.events_service.dto.modelDTO.*;
import com.practice.events_service.dto.newDTO.NewCategoryDTO;
import com.practice.events_service.dto.newDTO.NewCommentDTO;
import com.practice.events_service.dto.newDTO.NewEventDTO;
import com.practice.events_service.dto.shortDTO.EventShortDTO;
import com.practice.events_service.dto.updateRequest.UpdateEventAdminRequest;
import com.practice.events_service.dto.updateRequest.UpdateEventCommentsState;
import com.practice.events_service.dto.updateRequest.UpdateEventUserRequest;
import com.practice.events_service.generators.*;
import com.practice.events_service.dto.newDTO.NewUserRequest;
import com.practice.events_service.service.adminService.AdminCategoriesService;
import com.practice.events_service.service.adminService.AdminEventsService;
import com.practice.events_service.service.adminService.AdminUsersService;
import com.practice.events_service.service.privateService.PrivateCommentsService;
import com.practice.events_service.service.privateService.PrivateEventsService;
import com.practice.events_service.service.privateService.PrivateRequestsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PrivateEventsServiceTests {
    @Autowired
    private AdminUsersService adminUsersService;
    @Autowired
    private AdminCategoriesService adminCategoriesService;
    @Autowired
    private AdminEventsService adminEventsService;
    @Autowired
    private PrivateEventsService privateEventsService;
    @Autowired
    private PrivateRequestsService privateRequestsService;
    @Autowired
    private PrivateCommentsService privateCommentsService;

    @Autowired
    private UserGenerator userGenerator;
    @Autowired
    private CategoryGenerator categoryGenerator;
    @Autowired
    private EventGenerator eventGenerator;
    @Autowired
    private EventRequestStatusUpdateRequestGenerator eventRequestStatusUpdateRequestGenerator;
    @Autowired
    private CommentGenerator commentGenerator;

    private static UserDTO initiatorDTO;
    private static CategoryDTO categoryDTO;
    private static EventFullDTO eventFullDTO1;

    private static UserDTO requesterDTO;
    private static ParticipationRequestDTO participationRequestDTO;
    private static CommentDTO commentDTO;

    @Test
    @Order(1)
    void addNewEvent() {
        NewUserRequest newUserRequest = userGenerator.generateNewUserRequest();
        initiatorDTO = adminUsersService.postNewUser(newUserRequest);

        NewCategoryDTO newCategoryDTO = categoryGenerator.generateNewCategoryDTO();
        categoryDTO = adminCategoriesService.addNewCategory(newCategoryDTO);

        NewEventDTO newEventDTO1 = eventGenerator.generateNewEventDTO(categoryDTO.getId());
        eventFullDTO1 = privateEventsService.addNewEvent(initiatorDTO.getId(), newEventDTO1);

        assertNotNull(eventFullDTO1.getId());
        assertEquals(newEventDTO1.getTitle(), eventFullDTO1.getTitle());
        assertEquals(newEventDTO1.getDescription(), eventFullDTO1.getDescription());
        assertEquals(newEventDTO1.getAnnotation(), eventFullDTO1.getAnnotation());
        assertEquals(newEventDTO1.getEventDate(), eventFullDTO1.getEventDate());
        assertEquals(newEventDTO1.getCategory(), eventFullDTO1.getCategory().getId());
        assertEquals(categoryDTO.getName(), eventFullDTO1.getCategory().getName());
        assertEquals(newEventDTO1.getLocation().getLat(), eventFullDTO1.getLocation().getLat());
        assertEquals(newEventDTO1.getLocation().getLon(), eventFullDTO1.getLocation().getLon());
        assertEquals(newEventDTO1.getParticipantLimit(), eventFullDTO1.getParticipantLimit());
        assertEquals(newEventDTO1.getPaid(), eventFullDTO1.getPaid());
        assertEquals(newEventDTO1.getRequestModeration(), eventFullDTO1.getRequestModeration());
        assertNotNull(eventFullDTO1.getConfirmedRequests());
        assertNotNull(eventFullDTO1.getCreatedOn());
        assertNull(eventFullDTO1.getPublishedOn());
        assertEquals(EventFullDTO.State.PENDING, eventFullDTO1.getState());
        assertNotNull(eventFullDTO1.getViews());
        assertNotNull(eventFullDTO1.getComments());
    }

    @Test
    @Order(2)
    void getEvents() {
        List<EventShortDTO> eventShortDTOS = privateEventsService.getEvents(initiatorDTO.getId(), 0, 10);
        assertFalse(eventShortDTOS.isEmpty());
    }

    @Test
    @Order(3)
    void getEventById() {
        EventFullDTO getEvent = privateEventsService.getEventByUserId(initiatorDTO.getId(), eventFullDTO1.getId());

        assertEquals(eventFullDTO1.getId(), getEvent.getId());
        assertEquals(eventFullDTO1.getTitle(), getEvent.getTitle());
        assertEquals(eventFullDTO1.getDescription(), getEvent.getDescription());
        assertEquals(eventFullDTO1.getAnnotation(), getEvent.getAnnotation());
        assertEquals(eventFullDTO1.getEventDate(), getEvent.getEventDate());
        assertEquals(eventFullDTO1.getInitiator().getId(), getEvent.getInitiator().getId());
        assertEquals(eventFullDTO1.getInitiator().getName(), getEvent.getInitiator().getName());
        assertEquals(eventFullDTO1.getCategory().getId(), getEvent.getCategory().getId());
        assertEquals(eventFullDTO1.getCategory().getName(), getEvent.getCategory().getName());
        assertEquals(eventFullDTO1.getLocation().getLat(), getEvent.getLocation().getLat());
        assertEquals(eventFullDTO1.getLocation().getLon(), getEvent.getLocation().getLon());
        assertEquals(eventFullDTO1.getParticipantLimit(), getEvent.getParticipantLimit());
        assertEquals(eventFullDTO1.getPaid(), getEvent.getPaid());
        assertEquals(eventFullDTO1.getRequestModeration(), getEvent.getRequestModeration());
        assertEquals(eventFullDTO1.getConfirmedRequests(), getEvent.getConfirmedRequests());
        assertEquals(eventFullDTO1.getCreatedOn(), getEvent.getCreatedOn());
        assertEquals(eventFullDTO1.getPublishedOn(), getEvent.getPublishedOn());
        assertEquals(eventFullDTO1.getState(), getEvent.getState());
        assertEquals(eventFullDTO1.getViews(), getEvent.getViews());
        assertEquals(eventFullDTO1.getComments(), getEvent.getComments());
    }

    @Test
    @Order(4)
    void getEventRequests() {
        NewUserRequest requester = userGenerator.generateNewUserRequest();
        requesterDTO = adminUsersService.postNewUser(requester);

        UpdateEventAdminRequest updateEventAdminRequest = eventGenerator.generateUpdateEventAdminRequest(categoryDTO.getId());
        updateEventAdminRequest.setShowComments(true);

        updateEventAdminRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);
        eventFullDTO1 = adminEventsService.patchEventById(eventFullDTO1.getId(), updateEventAdminRequest);

        participationRequestDTO = privateRequestsService.postEventParticipationRequest(requesterDTO.getId(), eventFullDTO1.getId());

        List<ParticipationRequestDTO> requestDTOS = privateEventsService.getEventRequestsByUserIdAndEventId(initiatorDTO.getId(), eventFullDTO1.getId());
        assertFalse(requestDTOS.isEmpty());
    }

    @Test
    @Order(5)
    void patchEventRequests() {
        EventRequestStatusUpdateRequest updateRequest = eventRequestStatusUpdateRequestGenerator.generateUpdateRequest
                (List.of(participationRequestDTO.getId()), EventRequestStatusUpdateRequest.Status.CONFIRMED);

        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = privateEventsService.patchEventRequestsByUserId
                (initiatorDTO.getId(), eventFullDTO1.getId(), updateRequest);

        assertFalse(eventRequestStatusUpdateResult.getConfirmedRequests().isEmpty());
        assertTrue(eventRequestStatusUpdateResult.getRejectedRequests().isEmpty());
    }

    @Test
    @Order(6)
    void patchEvent() {
        // Create second event
        NewEventDTO newEventDTO2 = eventGenerator.generateNewEventDTO(categoryDTO.getId());
        EventFullDTO eventFullDTO2 = privateEventsService.addNewEvent(initiatorDTO.getId(), newEventDTO2);

        UpdateEventUserRequest updateEventUserRequest = eventGenerator.generateUpdateEventUserRequest(categoryDTO.getId());
        updateEventUserRequest.setStateAction(UpdateEventUserRequest.StateAction.CANCEL_REVIEW);

        eventFullDTO2 = privateEventsService.patchEventByUserId(initiatorDTO.getId(), eventFullDTO2.getId(), updateEventUserRequest);

        assertNotNull(eventFullDTO2.getId());
        assertEquals(updateEventUserRequest.getTitle(), eventFullDTO2.getTitle());
        assertEquals(updateEventUserRequest.getDescription(), eventFullDTO2.getDescription());
        assertEquals(updateEventUserRequest.getAnnotation(), eventFullDTO2.getAnnotation());
        assertEquals(updateEventUserRequest.getEventDate(), eventFullDTO2.getEventDate());
        assertEquals(categoryDTO.getName(), eventFullDTO2.getCategory().getName());
        assertEquals(updateEventUserRequest.getLocation().getLat(), eventFullDTO2.getLocation().getLat());
        assertEquals(updateEventUserRequest.getLocation().getLon(), eventFullDTO2.getLocation().getLon());
        assertEquals(updateEventUserRequest.getParticipantLimit(), eventFullDTO2.getParticipantLimit());
        assertEquals(updateEventUserRequest.getPaid(), eventFullDTO2.getPaid());
        assertEquals(updateEventUserRequest.getRequestModeration(), eventFullDTO2.getRequestModeration());
        assertNotNull(eventFullDTO2.getConfirmedRequests());
        assertNotNull(eventFullDTO2.getCreatedOn());
        assertNull(eventFullDTO2.getPublishedOn());
        assertEquals(EventFullDTO.State.CANCELED, eventFullDTO2.getState());
        assertNotNull(eventFullDTO2.getViews());
        assertNotNull(eventFullDTO2.getComments());
    }

    @Test
    @Order(7)
    void patchEventCommentsStateHideComments() {
        // To the first event (Published)
        NewCommentDTO newCommentDTO = commentGenerator.generateNewCommentDTO();
        commentDTO = privateCommentsService.postComment(requesterDTO.getId(), eventFullDTO1.getId(), newCommentDTO);

        UpdateEventCommentsState updateEventCommentsState = eventGenerator.generateUpdateEventCommentsState(true, false);
        privateEventsService.patchEventCommentsState(initiatorDTO.getId(), eventFullDTO1.getId(), updateEventCommentsState);

        EventFullDTO getEvent = privateEventsService.getEventByUserId(initiatorDTO.getId(), eventFullDTO1.getId());
        assertNull(getEvent.getComments());
    }

    @Test
    @Order(8)
    void patchEventCommentsStateShowComments() {
        // To the first event (Published)
        UpdateEventCommentsState updateEventCommentsState = eventGenerator.generateUpdateEventCommentsState(true, true);
        privateEventsService.patchEventCommentsState(initiatorDTO.getId(), eventFullDTO1.getId(), updateEventCommentsState);

        EventFullDTO getEvent = privateEventsService.getEventByUserId(initiatorDTO.getId(), eventFullDTO1.getId());
        assertEquals(1, getEvent.getComments().size());
        assertTrue(getEvent.getComments().contains(commentDTO));
    }
}
