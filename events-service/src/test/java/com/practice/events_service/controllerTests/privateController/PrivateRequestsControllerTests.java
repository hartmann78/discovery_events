package com.practice.events_service.controllerTests.privateController;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PrivateRequestsControllerTests {
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

    private static Long initiatorId;
    private static Long requesterId;
    private static Long categoryId;
    private static Long eventId;
    private static Long requestId;

    @Test
    @Order(1)
    void postEventParticipationRequest() throws Exception {
        // Create user
        NewUserRequest newUserRequest = userGenerator.generateNewUserRequest();
        String newUserRequestJson = objectMapper.writeValueAsString(newUserRequest);

        ResultActions userResult = mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserRequestJson))
                .andExpect(status().isCreated());

        initiatorId = objectMapper.readValue(userResult.andReturn().getResponse().getContentAsString(), UserDTO.class).getId();

        // Create category
        NewCategoryDTO newCategoryDTO = categoryGenerator.generateNewCategoryDTO();
        String newCategoryDTOJson = objectMapper.writeValueAsString(newCategoryDTO);

        ResultActions categoryResult = mockMvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCategoryDTOJson))
                .andExpect(status().isCreated());

        categoryId = objectMapper.readValue(categoryResult.andReturn().getResponse().getContentAsString(), CategoryDTO.class).getId();

        // Create event
        NewEventDTO newEventDTO = eventGenerator.generateNewEventDTO(categoryId);
        String newEventDTOJson = objectMapper.writeValueAsString(newEventDTO);

        ResultActions eventResult = mockMvc.perform(post("/users/{userId}/events", initiatorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newEventDTOJson))
                .andExpect(status().isCreated());

        eventId = objectMapper.readValue(eventResult.andReturn().getResponse().getContentAsString(), EventFullDTO.class).getId();

        // Publish event
        UpdateEventAdminRequest updateEventAdminRequest = eventGenerator.generateUpdateEventAdminRequest(newEventDTO.getCategory());
        updateEventAdminRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);

        String updateEventAdminRequestJson = objectMapper.writeValueAsString(updateEventAdminRequest);

        mockMvc.perform(patch("/admin/events/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEventAdminRequestJson))
                .andExpect(status().isOk());

        // Create requester
        NewUserRequest requester = userGenerator.generateNewUserRequest();
        String requesterJson = objectMapper.writeValueAsString(requester);

        ResultActions requesterResult = mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requesterJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(requester.getName()))
                .andExpect(jsonPath("$.email").value(requester.getEmail()));

        requesterId = objectMapper.readValue(requesterResult.andReturn().getResponse().getContentAsString(), UserDTO.class).getId();

        // Create participationRequest
        ResultActions requestResult = mockMvc.perform(post("/users/{userId}/requests", requesterId)
                        .param("eventId", eventId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.event").value(eventId))
                .andExpect(jsonPath("$.requester").value(requesterId))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.status").value(ParticipationRequest.Status.PENDING.toString()));

        requestId = objectMapper.readValue(requestResult.andReturn().getResponse().getContentAsString(), ParticipationRequestDTO.class).getId();
    }

    @Test
    @Order(2)
    void getUserEventRequests() throws Exception {
        mockMvc.perform(get("/users/{userId}/requests", requesterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestId))
                .andExpect(jsonPath("$[0].event").value(eventId))
                .andExpect(jsonPath("$[0].requester").value(requesterId))
                .andExpect(jsonPath("$[0].created").exists())
                .andExpect(jsonPath("$[0].status").value(ParticipationRequest.Status.PENDING.toString()));
    }

    @Test
    @Order(3)
    void cancelEventParticipationRequest() throws Exception {
        mockMvc.perform(patch("/users/{userId}/requests/{requestId}/cancel", requesterId, requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.event").value(eventId))
                .andExpect(jsonPath("$.requester").value(requesterId))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.status").value(ParticipationRequest.Status.CANCELED.toString()));
    }
}
