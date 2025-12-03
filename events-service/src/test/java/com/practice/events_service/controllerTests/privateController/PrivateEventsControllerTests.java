package com.practice.events_service.controllerTests.privateController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.events_service.dto.eventRequestStatusUpdate.EventRequestStatusUpdateRequest;
import com.practice.events_service.dto.modelDTO.CategoryDTO;
import com.practice.events_service.dto.modelDTO.EventFullDTO;
import com.practice.events_service.dto.modelDTO.ParticipationRequestDTO;
import com.practice.events_service.dto.modelDTO.UserDTO;
import com.practice.events_service.dto.newDTO.NewCategoryDTO;
import com.practice.events_service.dto.newDTO.NewEventDTO;
import com.practice.events_service.dto.updateRequest.UpdateEventAdminRequest;
import com.practice.events_service.dto.updateRequest.UpdateEventCommentsState;
import com.practice.events_service.dto.updateRequest.UpdateEventUserRequest;
import com.practice.events_service.enums.State;
import com.practice.events_service.generators.CategoryGenerator;
import com.practice.events_service.generators.EventGenerator;
import com.practice.events_service.generators.EventRequestStatusUpdateRequestGenerator;
import com.practice.events_service.generators.UserGenerator;
import com.practice.events_service.dto.newDTO.NewUserRequest;
import com.practice.events_service.model.ParticipationRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PrivateEventsControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserGenerator userGenerator;
    @Autowired
    private CategoryGenerator categoryGenerator;
    @Autowired
    private EventGenerator eventGenerator;
    @Autowired
    private EventRequestStatusUpdateRequestGenerator eventRequestStatusUpdateRequestGenerator;

    private NewUserRequest newUserRequest;
    private NewUserRequest requester;
    private NewCategoryDTO newCategoryDTO;
    private NewEventDTO newEventDTO;

    private Long initiatorId;
    private Long requesterId;
    private Long categoryId;
    private Long eventId;
    private Long requestId;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    @BeforeEach
    void addNewEvent() throws Exception {
        // Create user
        newUserRequest = userGenerator.generateNewUserRequest();
        String newUserRequestJson = objectMapper.writeValueAsString(newUserRequest);

        ResultActions userResult = mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserRequestJson))
                .andExpect(status().isCreated());

        initiatorId = objectMapper.readValue(userResult.andReturn().getResponse().getContentAsString(), UserDTO.class).getId();

        // Create category
        newCategoryDTO = categoryGenerator.generateNewCategoryDTO();
        String newCategoryDTOJson = objectMapper.writeValueAsString(newCategoryDTO);

        ResultActions categoryResult = mockMvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCategoryDTOJson))
                .andExpect(status().isCreated());

        categoryId = objectMapper.readValue(categoryResult.andReturn().getResponse().getContentAsString(), CategoryDTO.class).getId();

        // Create event
        newEventDTO = eventGenerator.generateNewEventDTO(categoryId);
        String newEventDTOJson = objectMapper.writeValueAsString(newEventDTO);

        ResultActions eventResult = mockMvc.perform(post("/users/{userId}/events", initiatorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newEventDTOJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(newEventDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(newEventDTO.getDescription()))
                .andExpect(jsonPath("$.annotation").value(newEventDTO.getAnnotation()))
                .andExpect(jsonPath("$.eventDate").value(newEventDTO.getEventDate().format(dateTimeFormatter)))
                .andExpect(jsonPath("$.initiator.id").value(initiatorId))
                .andExpect(jsonPath("$.initiator.name").value(newUserRequest.getName()))
                .andExpect(jsonPath("$.category.id").value(categoryId))
                .andExpect(jsonPath("$.category.name").value(newCategoryDTO.getName()))
                .andExpect(jsonPath("$.location.lat").value(newEventDTO.getLocation().getLat()))
                .andExpect(jsonPath("$.location.lon").value(newEventDTO.getLocation().getLon()))
                .andExpect(jsonPath("$.participantLimit").value(newEventDTO.getParticipantLimit()))
                .andExpect(jsonPath("$.paid").value(newEventDTO.getPaid()))
                .andExpect(jsonPath("$.requestModeration").value(newEventDTO.getRequestModeration()))
                .andExpect(jsonPath("$.confirmedRequests").exists())
                .andExpect(jsonPath("$.createdOn").exists())
                .andExpect(jsonPath("$.publishedOn").doesNotExist())
                .andExpect(jsonPath("$.state").value(State.PENDING.toString()))
                .andExpect(jsonPath("$.views").exists())
                .andExpect(jsonPath("$.comments").exists());

        eventId = objectMapper.readValue(eventResult.andReturn().getResponse().getContentAsString(), EventFullDTO.class).getId();
    }

    @Test
    void getEvents() throws Exception {
        mockMvc.perform(get("/users/{userId}/events", initiatorId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(eventId))
                .andExpect(jsonPath("$[0].title").value(newEventDTO.getTitle()))
                .andExpect(jsonPath("$[0].annotation").value(newEventDTO.getAnnotation()))
                .andExpect(jsonPath("$[0].eventDate").value(newEventDTO.getEventDate().format(dateTimeFormatter)))
                .andExpect(jsonPath("$[0].initiator.id").value(initiatorId))
                .andExpect(jsonPath("$[0].initiator.name").value(newUserRequest.getName()))
                .andExpect(jsonPath("$[0].category.id").value(categoryId))
                .andExpect(jsonPath("$[0].category.name").value(newCategoryDTO.getName()))
                .andExpect(jsonPath("$[0].paid").value(newEventDTO.getPaid()))
                .andExpect(jsonPath("$[0].views").exists());
    }

    @Test
    void getEventById() throws Exception {
        mockMvc.perform(get("/users/{userId}/events/{eventId}", initiatorId, eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.title").value(newEventDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(newEventDTO.getDescription()))
                .andExpect(jsonPath("$.annotation").value(newEventDTO.getAnnotation()))
                .andExpect(jsonPath("$.eventDate").value(newEventDTO.getEventDate().format(dateTimeFormatter)))
                .andExpect(jsonPath("$.initiator.id").value(initiatorId))
                .andExpect(jsonPath("$.initiator.name").value(newUserRequest.getName()))
                .andExpect(jsonPath("$.category.id").value(categoryId))
                .andExpect(jsonPath("$.category.name").value(newCategoryDTO.getName()))
                .andExpect(jsonPath("$.location.lat").value(newEventDTO.getLocation().getLat()))
                .andExpect(jsonPath("$.location.lon").value(newEventDTO.getLocation().getLon()))
                .andExpect(jsonPath("$.participantLimit").value(newEventDTO.getParticipantLimit()))
                .andExpect(jsonPath("$.paid").value(newEventDTO.getPaid()))
                .andExpect(jsonPath("$.requestModeration").value(newEventDTO.getRequestModeration()))
                .andExpect(jsonPath("$.confirmedRequests").exists())
                .andExpect(jsonPath("$.createdOn").exists())
                .andExpect(jsonPath("$.publishedOn").doesNotExist())
                .andExpect(jsonPath("$.state").exists())
                .andExpect(jsonPath("$.views").exists())
                .andExpect(jsonPath("$.comments").exists());
    }

    @Test
    void getEventRequests() throws Exception {
        // Publish first event
        UpdateEventAdminRequest updateEventAdminRequest = eventGenerator.generateUpdateEventAdminRequest(newEventDTO.getCategory());
        updateEventAdminRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);

        String updateEventAdminRequestJson = objectMapper.writeValueAsString(updateEventAdminRequest);

        mockMvc.perform(patch("/admin/events/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEventAdminRequestJson))
                .andExpect(status().isOk());

        // Create requester
        requester = userGenerator.generateNewUserRequest();
        String requesterJson = objectMapper.writeValueAsString(requester);

        ResultActions userResult = mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requesterJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(requester.getName()))
                .andExpect(jsonPath("$.email").value(requester.getEmail()));

        requesterId = objectMapper.readValue(userResult.andReturn().getResponse().getContentAsString(), UserDTO.class).getId();

        // Create participationRequest
        ResultActions participationRequestResult = mockMvc.perform(post("/users/{userId}/requests", requesterId)
                        .param("eventId", eventId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.event").value(eventId))
                .andExpect(jsonPath("$.requester").value(requesterId))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.status").value(ParticipationRequest.Status.PENDING.toString()));

        requestId = objectMapper.readValue(participationRequestResult.andReturn().getResponse().getContentAsString(), ParticipationRequestDTO.class).getId();

        // Get eventRequests
        mockMvc.perform(get("/users/{userId}/events/{eventId}/requests", initiatorId, eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].event").value(eventId))
                .andExpect(jsonPath("$[0].requester").value(requesterId))
                .andExpect(jsonPath("$[0].created").exists())
                .andExpect(jsonPath("$[0].status").value(ParticipationRequest.Status.PENDING.toString()));
    }

    @Test
    void patchEvent() throws Exception {
        // Create second event
        NewEventDTO newEventDTO2 = eventGenerator.generateNewEventDTO(categoryId);
        String newEventDTOJson2 = objectMapper.writeValueAsString(newEventDTO2);

        ResultActions eventResult2 = mockMvc.perform(post("/users/{userId}/events", initiatorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newEventDTOJson2))
                .andExpect(status().isCreated());

        Long eventId2 = objectMapper.readValue(eventResult2.andReturn().getResponse().getContentAsString(), EventFullDTO.class).getId();

        // Update second event
        UpdateEventUserRequest updateEventUserRequest = eventGenerator.generateUpdateEventUserRequest(categoryId);
        updateEventUserRequest.setShowComments(false);
        updateEventUserRequest.setStateAction(UpdateEventUserRequest.StateAction.SEND_TO_REVIEW);

        String updateEventUserRequestJson = objectMapper.writeValueAsString(updateEventUserRequest);

        mockMvc.perform(patch("/users/{userId}/events/{eventId}", initiatorId, eventId2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEventUserRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId2))
                .andExpect(jsonPath("$.title").value(updateEventUserRequest.getTitle()))
                .andExpect(jsonPath("$.description").value(updateEventUserRequest.getDescription()))
                .andExpect(jsonPath("$.annotation").value(updateEventUserRequest.getAnnotation()))
                .andExpect(jsonPath("$.eventDate").value(updateEventUserRequest.getEventDate().format(dateTimeFormatter)))
                .andExpect(jsonPath("$.initiator.id").value(initiatorId))
                .andExpect(jsonPath("$.initiator.name").value(newUserRequest.getName()))
                .andExpect(jsonPath("$.category.id").value(categoryId))
                .andExpect(jsonPath("$.category.name").value(newCategoryDTO.getName()))
                .andExpect(jsonPath("$.location.lat").value(updateEventUserRequest.getLocation().getLat()))
                .andExpect(jsonPath("$.location.lon").value(updateEventUserRequest.getLocation().getLon()))
                .andExpect(jsonPath("$.participantLimit").value(updateEventUserRequest.getParticipantLimit()))
                .andExpect(jsonPath("$.paid").value(updateEventUserRequest.getPaid()))
                .andExpect(jsonPath("$.requestModeration").value(updateEventUserRequest.getRequestModeration()))
                .andExpect(jsonPath("$.confirmedRequests").exists())
                .andExpect(jsonPath("$.createdOn").exists())
                .andExpect(jsonPath("$.publishedOn").doesNotExist())
                .andExpect(jsonPath("$.state").value(State.PENDING.toString()))
                .andExpect(jsonPath("$.views").exists())
                .andExpect(jsonPath("$.comments").doesNotExist());
    }

    @Test
    void patchEventRequests() throws Exception {
        // Publish first event
        UpdateEventAdminRequest updateEventAdminRequest = eventGenerator.generateUpdateEventAdminRequest(newEventDTO.getCategory());
        updateEventAdminRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);

        String updateEventAdminRequestJson = objectMapper.writeValueAsString(updateEventAdminRequest);

        mockMvc.perform(patch("/admin/events/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEventAdminRequestJson))
                .andExpect(status().isOk());

        // Create requester
        requester = userGenerator.generateNewUserRequest();
        String requesterJson = objectMapper.writeValueAsString(requester);

        ResultActions userResult = mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requesterJson))
                .andExpect(status().isCreated());

        requesterId = objectMapper.readValue(userResult.andReturn().getResponse().getContentAsString(), UserDTO.class).getId();

        // Create participationRequest
        ResultActions participationRequestResult = mockMvc.perform(post("/users/{userId}/requests", requesterId)
                        .param("eventId", eventId.toString()))
                .andExpect(status().isCreated());

        requestId = objectMapper.readValue(participationRequestResult.andReturn().getResponse().getContentAsString(), ParticipationRequestDTO.class).getId();

        // Accept request to the first event
        EventRequestStatusUpdateRequest updateRequest = eventRequestStatusUpdateRequestGenerator
                .generateUpdateRequest(List.of(requestId), EventRequestStatusUpdateRequest.Status.CONFIRMED);

        String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(patch("/users/{userId}/events/{eventId}/requests", initiatorId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmedRequests").isNotEmpty())
                .andExpect(jsonPath("$.rejectedRequests").isEmpty());
    }

    @Test
    void patchEventCommentsStateHideComments() throws Exception {
        // To the first event (Published)
        UpdateEventCommentsState updateEventCommentsState = eventGenerator.generateUpdateEventCommentsState(true, false);
        String updateEventCommentsStateJson = objectMapper.writeValueAsString(updateEventCommentsState);

        mockMvc.perform(patch("/users/{userId}/events/{eventId}/comments", initiatorId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEventCommentsStateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments").doesNotExist());
    }

    @Test
    void patchEventCommentsStateShowComments() throws Exception {
        // To the first event (Published)
        UpdateEventCommentsState updateEventCommentsState = eventGenerator.generateUpdateEventCommentsState(true, true);
        String updateEventCommentsStateJson = objectMapper.writeValueAsString(updateEventCommentsState);

        mockMvc.perform(patch("/users/{userId}/events/{eventId}/comments", initiatorId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEventCommentsStateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments").exists());
    }
}
