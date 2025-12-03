package com.practice.events_service.serviceTests.privateService;

import com.practice.events_service.dto.modelDTO.CategoryDTO;
import com.practice.events_service.dto.modelDTO.EventFullDTO;
import com.practice.events_service.dto.modelDTO.ParticipationRequestDTO;
import com.practice.events_service.dto.modelDTO.UserDTO;
import com.practice.events_service.dto.newDTO.NewCategoryDTO;
import com.practice.events_service.dto.newDTO.NewEventDTO;
import com.practice.events_service.dto.newDTO.NewUserRequest;
import com.practice.events_service.dto.updateRequest.UpdateEventAdminRequest;
import com.practice.events_service.generators.CategoryGenerator;
import com.practice.events_service.generators.EventGenerator;
import com.practice.events_service.generators.UserGenerator;
import com.practice.events_service.model.ParticipationRequest;
import com.practice.events_service.service.adminService.AdminCategoriesService;
import com.practice.events_service.service.adminService.AdminEventsService;
import com.practice.events_service.service.adminService.AdminUsersService;
import com.practice.events_service.service.privateService.PrivateEventsService;
import com.practice.events_service.service.privateService.PrivateRequestsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PrivateRequestsServiceTests {
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
    private UserGenerator userGenerator;
    @Autowired
    private CategoryGenerator categoryGenerator;
    @Autowired
    private EventGenerator eventGenerator;

    private UserDTO initiatorDTO;
    private UserDTO requesterDTO;
    private CategoryDTO categoryDTO;
    private EventFullDTO eventFullDTO;
    private ParticipationRequestDTO participationRequestDTO;

    @Test
    @BeforeEach
    void postEventParticipationRequest() {
        NewUserRequest newUserRequest = userGenerator.generateNewUserRequest();
        initiatorDTO = adminUsersService.postNewUser(newUserRequest);

        NewCategoryDTO newCategoryDTO = categoryGenerator.generateNewCategoryDTO();
        categoryDTO = adminCategoriesService.addNewCategory(newCategoryDTO);

        NewEventDTO newEventDTO = eventGenerator.generateNewEventDTO(categoryDTO.getId());
        eventFullDTO = privateEventsService.addNewEvent(initiatorDTO.getId(), newEventDTO);

        UpdateEventAdminRequest updateEventAdminRequest = eventGenerator.generateUpdateEventAdminRequest(categoryDTO.getId());
        updateEventAdminRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);
        eventFullDTO = adminEventsService.patchEventById(eventFullDTO.getId(), updateEventAdminRequest);

        NewUserRequest requester = userGenerator.generateNewUserRequest();
        requesterDTO = adminUsersService.postNewUser(requester);

        participationRequestDTO = privateRequestsService.postEventParticipationRequest(requesterDTO.getId(), eventFullDTO.getId());

        assertNotNull(participationRequestDTO.getId());
        assertEquals(eventFullDTO.getId(), participationRequestDTO.getEvent());
        assertEquals(requesterDTO.getId(), participationRequestDTO.getRequester());
        assertNotNull(participationRequestDTO.getCreated());
        assertEquals(ParticipationRequest.Status.PENDING.toString(), participationRequestDTO.getStatus());
    }

    @Test
    void getUserEventRequests() {
        List<ParticipationRequestDTO> requestDTOS = privateRequestsService.getUserEventRequests(requesterDTO.getId());
        assertFalse(requestDTOS.isEmpty());
    }

    @Test
    void cancelEventParticipationRequest() {
        ParticipationRequestDTO requestDTO = privateRequestsService.cancelEventParticipationRequest(requesterDTO.getId(), participationRequestDTO.getId());

        assertEquals(participationRequestDTO.getId(), requestDTO.getId());
        assertEquals(eventFullDTO.getId(), requestDTO.getEvent());
        assertEquals(requesterDTO.getId(), requestDTO.getRequester());
        assertEquals(participationRequestDTO.getCreated().truncatedTo(ChronoUnit.SECONDS), requestDTO.getCreated().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(ParticipationRequest.Status.CANCELED.toString(), requestDTO.getStatus());
    }
}
