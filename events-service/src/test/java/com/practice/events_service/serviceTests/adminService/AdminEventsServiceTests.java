package com.practice.events_service.serviceTests.adminService;

import com.practice.events_service.dto.modelDTO.CategoryDTO;
import com.practice.events_service.dto.modelDTO.EventFullDTO;
import com.practice.events_service.dto.modelDTO.UserDTO;
import com.practice.events_service.dto.newDTO.NewCategoryDTO;
import com.practice.events_service.dto.newDTO.NewEventDTO;
import com.practice.events_service.dto.newDTO.NewUserRequest;
import com.practice.events_service.dto.updateRequest.UpdateEventAdminRequest;
import com.practice.events_service.dto.updateRequest.UpdateEventCommentsState;
import com.practice.events_service.enums.State;
import com.practice.events_service.generators.CategoryGenerator;
import com.practice.events_service.generators.EventGenerator;
import com.practice.events_service.generators.UserGenerator;
import com.practice.events_service.service.adminService.AdminCategoriesService;
import com.practice.events_service.service.adminService.AdminEventsService;
import com.practice.events_service.service.adminService.AdminUsersService;
import com.practice.events_service.service.privateService.PrivateEventsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AdminEventsServiceTests {
    @Autowired
    private AdminUsersService adminUsersService;
    @Autowired
    private AdminCategoriesService adminCategoriesService;
    @Autowired
    private AdminEventsService adminEventsService;
    @Autowired
    private PrivateEventsService privateEventsService;

    @Autowired
    private UserGenerator userGenerator;
    @Autowired
    private CategoryGenerator categoryGenerator;
    @Autowired
    private EventGenerator eventGenerator;

    private UserDTO initiatorDTO;
    private CategoryDTO categoryDTO;
    private EventFullDTO eventFullDTO;

    private final LocalDateTime rangeStart = LocalDateTime.now().minusDays(7);
    private final LocalDateTime rangeEnd = LocalDateTime.now().plusDays(14);

    @BeforeEach
    void postEvent() {
        NewUserRequest newUserRequest = userGenerator.generateNewUserRequest();
        initiatorDTO = adminUsersService.postNewUser(newUserRequest);

        NewCategoryDTO newCategoryDTO = categoryGenerator.generateNewCategoryDTO();
        categoryDTO = adminCategoriesService.addNewCategory(newCategoryDTO);

        NewEventDTO newEventDTO = eventGenerator.generateNewEventDTO(categoryDTO.getId());
        eventFullDTO = privateEventsService.addNewEvent(initiatorDTO.getId(), newEventDTO);
    }

    @Test
    void getEvents() {
        List<EventFullDTO> getEvents = adminEventsService.getEvents(
                new Long[]{eventFullDTO.getInitiator().getId()},
                new String[]{eventFullDTO.getState().toString()},
                new Long[]{eventFullDTO.getCategory().getId()},
                rangeStart,
                rangeEnd,
                0,
                10);

        assertEquals(1, getEvents.size());
    }

    @Test
    void patchEvent() {
        UpdateEventAdminRequest updateEventAdminRequest = eventGenerator.generateUpdateEventAdminRequest(categoryDTO.getId());
        updateEventAdminRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);
        eventFullDTO = adminEventsService.patchEventById(eventFullDTO.getId(), updateEventAdminRequest);

        assertNotNull(eventFullDTO.getId());
        assertEquals(updateEventAdminRequest.getTitle(), eventFullDTO.getTitle());
        assertEquals(updateEventAdminRequest.getDescription(), eventFullDTO.getDescription());
        assertEquals(updateEventAdminRequest.getAnnotation(), eventFullDTO.getAnnotation());
        assertEquals(updateEventAdminRequest.getEventDate(), eventFullDTO.getEventDate());
        assertEquals(categoryDTO.getName(), eventFullDTO.getCategory().getName());
        assertEquals(updateEventAdminRequest.getLocation().getLat(), eventFullDTO.getLocation().getLat());
        assertEquals(updateEventAdminRequest.getLocation().getLon(), eventFullDTO.getLocation().getLon());
        assertEquals(updateEventAdminRequest.getParticipantLimit(), eventFullDTO.getParticipantLimit());
        assertEquals(updateEventAdminRequest.getPaid(), eventFullDTO.getPaid());
        assertEquals(updateEventAdminRequest.getRequestModeration(), eventFullDTO.getRequestModeration());
        assertNotNull(eventFullDTO.getConfirmedRequests());
        assertNotNull(eventFullDTO.getCreatedOn());
        assertNotNull(eventFullDTO.getPublishedOn());
        assertEquals(State.PUBLISHED, eventFullDTO.getState());
        assertNotNull(eventFullDTO.getViews());
        assertNotNull(eventFullDTO.getComments());
    }

    @Test
    void patchEventCommentsStateHideComments() {
        UpdateEventCommentsState updateEventCommentsState = eventGenerator.generateUpdateEventCommentsState(true, false);
        adminEventsService.patchEventCommentsState(eventFullDTO.getId(), updateEventCommentsState);

        List<EventFullDTO> getEvents = adminEventsService.getEvents(
                new Long[]{eventFullDTO.getInitiator().getId()},
                new String[]{eventFullDTO.getState().toString()},
                new Long[]{eventFullDTO.getCategory().getId()},
                rangeStart,
                rangeEnd,
                0,
                1);

        assertEquals(1, getEvents.size());
        EventFullDTO checkEvent = getEvents.get(0);

        assertNull(checkEvent.getComments());
    }

    @Test
    void patchEventCommentsStateShowComments() {
        UpdateEventCommentsState updateEventCommentsState = eventGenerator.generateUpdateEventCommentsState(true, true);
        privateEventsService.patchEventCommentsState(initiatorDTO.getId(), eventFullDTO.getId(), updateEventCommentsState);

        List<EventFullDTO> getEvents = adminEventsService.getEvents(
                new Long[]{eventFullDTO.getInitiator().getId()},
                new String[]{eventFullDTO.getState().toString()},
                new Long[]{eventFullDTO.getCategory().getId()},
                rangeStart,
                rangeEnd,
                0,
                1);

        assertEquals(1, getEvents.size());
        EventFullDTO checkEvent = getEvents.get(0);

        assertNotNull(checkEvent.getComments());
    }
}
