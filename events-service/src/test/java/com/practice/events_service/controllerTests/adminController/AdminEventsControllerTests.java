package com.practice.events_service.controllerTests.adminController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.events_service.dto.modelDTO.CategoryDTO;
import com.practice.events_service.dto.modelDTO.EventFullDTO;
import com.practice.events_service.dto.modelDTO.UserDTO;
import com.practice.events_service.dto.newDTO.NewCategoryDTO;
import com.practice.events_service.dto.newDTO.NewEventDTO;
import com.practice.events_service.dto.newDTO.NewUserRequest;
import com.practice.events_service.dto.updateRequest.UpdateEventAdminRequest;
import com.practice.events_service.dto.updateRequest.UpdateEventCommentsState;
import com.practice.events_service.generators.CategoryGenerator;
import com.practice.events_service.generators.EventGenerator;
import com.practice.events_service.generators.UserGenerator;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminEventsControllerTests {
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

    private static NewUserRequest newUserRequest;
    private static NewCategoryDTO newCategoryDTO;
    private static NewEventDTO newEventDTO;

    private static Long initiatorId;
    private static Long categoryId;
    private static Long eventId;

    @Test
    @Order(1)
    void getEvents() throws Exception {
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
                .andExpect(status().isCreated());

        eventId = objectMapper.readValue(eventResult.andReturn().getResponse().getContentAsString(), EventFullDTO.class).getId();

        // Get events
        mockMvc.perform(get("/admin/events")
                        .param("users", initiatorId.toString())
                        .param("states", "PENDING")
                        .param("categories", categoryId.toString())
                        .param("rangeStart", LocalDateTime.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .param("rangeEnd", LocalDateTime.now().plusDays(14).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .param("from", "0")
                        .param("size", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(eventId))
                .andExpect(jsonPath("$[0].title").value(newEventDTO.getTitle()))
                .andExpect(jsonPath("$[0].description").value(newEventDTO.getDescription()))
                .andExpect(jsonPath("$[0].annotation").value(newEventDTO.getAnnotation()))
                .andExpect(jsonPath("$[0].eventDate").value(newEventDTO.getEventDate()))
                .andExpect(jsonPath("$[0].initiator.id").value(initiatorId))
                .andExpect(jsonPath("$[0].initiator.name").value(newUserRequest.getName()))
                .andExpect(jsonPath("$[0].category.id").value(categoryId))
                .andExpect(jsonPath("$[0].category.name").value(newCategoryDTO.getName()))
                .andExpect(jsonPath("$[0].location.lat").value(newEventDTO.getLocation().getLat()))
                .andExpect(jsonPath("$[0].location.lon").value(newEventDTO.getLocation().getLon()))
                .andExpect(jsonPath("$[0].participantLimit").value(newEventDTO.getParticipantLimit()))
                .andExpect(jsonPath("$[0].paid").value(newEventDTO.getPaid()))
                .andExpect(jsonPath("$[0].requestModeration").value(newEventDTO.getRequestModeration()))
                .andExpect(jsonPath("$[0].confirmedRequests").exists())
                .andExpect(jsonPath("$[0].createdOn").exists())
                .andExpect(jsonPath("$[0].publishedOn").doesNotExist())
                .andExpect(jsonPath("$[0].state").value(EventFullDTO.State.PENDING.toString()))
                .andExpect(jsonPath("$[0].views").exists())
                .andExpect(jsonPath("$[0].comments").exists());
    }

    @Test
    @Order(2)
    void patchEvent() throws Exception {
        UpdateEventAdminRequest updateEventAdminRequest = eventGenerator.generateUpdateEventAdminRequest(newEventDTO.getCategory());
        updateEventAdminRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);

        String updateEventAdminRequestJson = objectMapper.writeValueAsString(updateEventAdminRequest);

        mockMvc.perform(patch("/admin/events/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEventAdminRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.title").value(updateEventAdminRequest.getTitle()))
                .andExpect(jsonPath("$.description").value(updateEventAdminRequest.getDescription()))
                .andExpect(jsonPath("$.annotation").value(updateEventAdminRequest.getAnnotation()))
                .andExpect(jsonPath("$.eventDate").value(updateEventAdminRequest.getEventDate()))
                .andExpect(jsonPath("$.initiator.id").value(initiatorId))
                .andExpect(jsonPath("$.initiator.name").value(newUserRequest.getName()))
                .andExpect(jsonPath("$.category.id").value(categoryId))
                .andExpect(jsonPath("$.category.name").value(newCategoryDTO.getName()))
                .andExpect(jsonPath("$.location.lat").value(updateEventAdminRequest.getLocation().getLat()))
                .andExpect(jsonPath("$.location.lon").value(updateEventAdminRequest.getLocation().getLon()))
                .andExpect(jsonPath("$.participantLimit").value(updateEventAdminRequest.getParticipantLimit()))
                .andExpect(jsonPath("$.paid").value(updateEventAdminRequest.getPaid()))
                .andExpect(jsonPath("$.requestModeration").value(updateEventAdminRequest.getRequestModeration()))
                .andExpect(jsonPath("$.confirmedRequests").exists())
                .andExpect(jsonPath("$.createdOn").exists())
                .andExpect(jsonPath("$.publishedOn").exists())
                .andExpect(jsonPath("$.state").value(EventFullDTO.State.PUBLISHED.toString()))
                .andExpect(jsonPath("$.views").exists())
                .andExpect(jsonPath("$.comments").exists());
    }

    @Test
    @Order(3)
    void patchEventCommentsStateHideComments() throws Exception {
        // To the first event (Published)
        UpdateEventCommentsState updateEventCommentsState = eventGenerator.generateUpdateEventCommentsState(true, false);
        String updateEventCommentsStateJson = objectMapper.writeValueAsString(updateEventCommentsState);

        mockMvc.perform(patch("/admin/events/{eventId}/comments", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEventCommentsStateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments").doesNotExist());
    }

    @Test
    @Order(4)
    void patchEventCommentsStateShowComments() throws Exception {
        // To the first event (Published)
        UpdateEventCommentsState updateEventCommentsState = eventGenerator.generateUpdateEventCommentsState(true, true);
        String updateEventCommentsStateJson = objectMapper.writeValueAsString(updateEventCommentsState);

        mockMvc.perform(patch("/admin/events/{eventId}/comments", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEventCommentsStateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments").exists());
    }
}
