package com.practice.events_service.serviceTests.publicService;

import com.practice.events_service.dto.modelDTO.CategoryDTO;
import com.practice.events_service.dto.modelDTO.EventFullDTO;
import com.practice.events_service.dto.modelDTO.UserDTO;
import com.practice.events_service.dto.newDTO.NewCategoryDTO;
import com.practice.events_service.dto.newDTO.NewEventDTO;
import com.practice.events_service.dto.newDTO.NewUserRequest;
import com.practice.events_service.dto.shortDTO.EventShortDTO;
import com.practice.events_service.dto.updateRequest.UpdateEventAdminRequest;
import com.practice.events_service.generators.CategoryGenerator;
import com.practice.events_service.generators.EventGenerator;
import com.practice.events_service.generators.UserGenerator;
import com.practice.events_service.service.adminService.AdminCategoriesService;
import com.practice.events_service.service.adminService.AdminEventsService;
import com.practice.events_service.service.adminService.AdminUsersService;
import com.practice.events_service.service.privateService.PrivateEventsService;
import com.practice.events_service.service.publicService.PublicEventsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PublicEventsServiceTests {
    @Autowired
    private AdminUsersService adminUsersService;
    @Autowired
    private AdminCategoriesService adminCategoriesService;
    @Autowired
    private AdminEventsService adminEventsService;
    @Autowired
    private PrivateEventsService privateEventsService;
    @Autowired
    private PublicEventsService publicEventsService;

    @Autowired
    private UserGenerator userGenerator;
    @Autowired
    private CategoryGenerator categoryGenerator;
    @Autowired
    private EventGenerator eventGenerator;

    private static UserDTO initiatorDTO;
    private static CategoryDTO categoryDTO;
    private static EventFullDTO eventFullDTO;

    private static MockHttpServletRequest request;

    @BeforeAll
    static void createMockHttpServletRequest() {
        request = new MockHttpServletRequest();
    }

    @Test
    @Order(1)
    void getPublishedEvents() throws IOException, InterruptedException, URISyntaxException {
        NewUserRequest newUserRequest = userGenerator.generateNewUserRequest();
        initiatorDTO = adminUsersService.postNewUser(newUserRequest);

        NewCategoryDTO newCategoryDTO = categoryGenerator.generateNewCategoryDTO();
        categoryDTO = adminCategoriesService.addNewCategory(newCategoryDTO);

        NewEventDTO newEventDTO = eventGenerator.generateNewEventDTO(categoryDTO.getId());
        eventFullDTO = privateEventsService.addNewEvent(initiatorDTO.getId(), newEventDTO);

        UpdateEventAdminRequest updateEventAdminRequest = eventGenerator.generateUpdateEventAdminRequest(categoryDTO.getId());
        updateEventAdminRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);
        eventFullDTO = adminEventsService.patchEventById(eventFullDTO.getId(), updateEventAdminRequest);

        request.setRequestURI("/events");

        List<EventShortDTO> eventShortDTOS = publicEventsService.getPublishedEvents(
                eventFullDTO.getDescription(),
                new Long[]{eventFullDTO.getCategory().getId()},
                newEventDTO.getPaid(),
                LocalDateTime.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.now().plusDays(14).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                true,
                "EVENT_DATE",
                0,
                10,
                request);

        assertFalse(eventShortDTOS.isEmpty());
    }

    @Test
    @Order(2)
    void getPublishedEventById() throws IOException, InterruptedException, URISyntaxException {
        EventFullDTO getEvent = publicEventsService.getPublishedEventById(eventFullDTO.getId(), request);

        assertEquals(eventFullDTO.getId(), getEvent.getId());
        assertEquals(eventFullDTO.getTitle(), getEvent.getTitle());
        assertEquals(eventFullDTO.getDescription(), getEvent.getDescription());
        assertEquals(eventFullDTO.getAnnotation(), getEvent.getAnnotation());
        assertEquals(eventFullDTO.getEventDate(), getEvent.getEventDate());
        assertEquals(eventFullDTO.getInitiator().getId(), getEvent.getInitiator().getId());
        assertEquals(eventFullDTO.getInitiator().getName(), getEvent.getInitiator().getName());
        assertEquals(eventFullDTO.getCategory().getId(), getEvent.getCategory().getId());
        assertEquals(eventFullDTO.getCategory().getName(), getEvent.getCategory().getName());
        assertEquals(eventFullDTO.getLocation().getLat(), getEvent.getLocation().getLat());
        assertEquals(eventFullDTO.getLocation().getLon(), getEvent.getLocation().getLon());
        assertEquals(eventFullDTO.getParticipantLimit(), getEvent.getParticipantLimit());
        assertEquals(eventFullDTO.getPaid(), getEvent.getPaid());
        assertEquals(eventFullDTO.getRequestModeration(), getEvent.getRequestModeration());
        assertEquals(eventFullDTO.getConfirmedRequests(), getEvent.getConfirmedRequests());
        assertEquals(eventFullDTO.getCreatedOn(), getEvent.getCreatedOn());
        assertNotNull(getEvent.getPublishedOn());
        assertEquals(EventFullDTO.State.PUBLISHED, getEvent.getState());
        assertEquals(0, getEvent.getViews());
    }
}
