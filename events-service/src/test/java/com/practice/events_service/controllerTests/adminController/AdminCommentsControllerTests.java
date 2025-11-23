package com.practice.events_service.controllerTests.adminController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.events_service.dto.eventRequestStatusUpdate.EventRequestStatusUpdateRequest;
import com.practice.events_service.dto.modelDTO.*;
import com.practice.events_service.dto.newDTO.NewCategoryDTO;
import com.practice.events_service.dto.newDTO.NewCommentDTO;
import com.practice.events_service.dto.newDTO.NewEventDTO;
import com.practice.events_service.dto.newDTO.NewUserRequest;
import com.practice.events_service.dto.updateRequest.UpdateEventAdminRequest;
import com.practice.events_service.generators.*;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminCommentsControllerTests {
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
    @Autowired
    private CommentGenerator commentGenerator;

    private static NewUserRequest commentAuthor;
    private static NewCommentDTO newCommentDTO1;
    private static NewCommentDTO newCommentDTO2;
    private static NewCommentDTO newCommentDTO3;

    private static Long initiatorId;
    private static Long categoryId;
    private static Long eventId;

    private static Long commentAuthorId;
    private static Long requestId;
    private static Long commentId1;
    private static Long commentId2;
    private static Long commentId3;

    @Test
    @Order(1)
    void getAllUserComments() throws Exception {
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

        // Patch and publish event
        UpdateEventAdminRequest updateEventAdminRequest = eventGenerator.generateUpdateEventAdminRequest(newEventDTO.getCategory());
        updateEventAdminRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);

        String updateEventAdminRequestJson = objectMapper.writeValueAsString(updateEventAdminRequest);

        mockMvc.perform(patch("/admin/events/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEventAdminRequestJson))
                .andExpect(status().isOk());

        // Create requester
        commentAuthor = userGenerator.generateNewUserRequest();
        String requesterJson = objectMapper.writeValueAsString(commentAuthor);

        ResultActions requesterResult = mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requesterJson))
                .andExpect(status().isCreated());

        commentAuthorId = objectMapper.readValue(requesterResult.andReturn().getResponse().getContentAsString(), UserDTO.class).getId();

        // Create participationRequest
        ResultActions requestResult = mockMvc.perform(post("/users/{userId}/requests", commentAuthorId)
                        .param("eventId", eventId.toString()))
                .andExpect(status().isCreated());

        requestId = objectMapper.readValue(requestResult.andReturn().getResponse().getContentAsString(), ParticipationRequestDTO.class).getId();

        // Accept request
        EventRequestStatusUpdateRequest updateRequest = eventRequestStatusUpdateRequestGenerator
                .generateUpdateRequest(List.of(requestId), EventRequestStatusUpdateRequest.Status.CONFIRMED);

        String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(patch("/users/{userId}/events/{eventId}/requests", initiatorId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isOk());

        // Post comment 1
        newCommentDTO1 = commentGenerator.generateNewCommentDTO();
        String newCommentDTOJson1 = objectMapper.writeValueAsString(newCommentDTO1);

        ResultActions newCommentResult1 = mockMvc.perform(post("/users/{userId}/events/{eventId}/comments", initiatorId, eventId)
                        .header("X-Sharer-Author-Id", commentAuthorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCommentDTOJson1))
                .andExpect(status().isCreated());

        commentId1 = objectMapper.readValue(newCommentResult1.andReturn().getResponse().getContentAsString(), CommentDTO.class).getId();

        mockMvc.perform(get("/admin/users/{userId}/comments", commentAuthorId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(commentId1))
                .andExpect(jsonPath("$[0].text").value(newCommentDTO1.getText()))
                .andExpect(jsonPath("$[0].fromEventInitiator").value("false"))
                .andExpect(jsonPath("$[0].authorName").value(commentAuthor.getName()))
                .andExpect(jsonPath("$[0].createdOn").exists())
                .andExpect(jsonPath("$[0].updatedOn").doesNotExist());

    }

    @Test
    @Order(2)
    void getAllEventComments() throws Exception {
        mockMvc.perform(get("/admin/events/{eventId}/comments", eventId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(commentId1))
                .andExpect(jsonPath("$[0].text").value(newCommentDTO1.getText()))
                .andExpect(jsonPath("$[0].fromEventInitiator").value("false"))
                .andExpect(jsonPath("$[0].authorName").value(commentAuthor.getName()))
                .andExpect(jsonPath("$[0].createdOn").exists())
                .andExpect(jsonPath("$[0].updatedOn").doesNotExist());
    }

    @Test
    @Order(3)
    void findCommentById() throws Exception {
        mockMvc.perform(get("/admin/comments/{commentId}", commentId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId1))
                .andExpect(jsonPath("$.text").value(newCommentDTO1.getText()))
                .andExpect(jsonPath("$.fromEventInitiator").value("false"))
                .andExpect(jsonPath("$.authorName").value(commentAuthor.getName()))
                .andExpect(jsonPath("$.createdOn").exists())
                .andExpect(jsonPath("$.updatedOn").doesNotExist());
    }

    @Test
    @Order(4)
    void deleteAllUserComments() throws Exception {
        // Delete comment 1
        mockMvc.perform(delete("/admin/users/{userId}/comments", commentAuthorId))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(5)
    void deleteAllEventComments() throws Exception {
        // Post comment 2
        newCommentDTO2 = commentGenerator.generateNewCommentDTO();
        String newCommentDTOJson2 = objectMapper.writeValueAsString(newCommentDTO2);

        // Delete comment 1
        ResultActions newCommentResult2 = mockMvc.perform(post("/users/{userId}/events/{eventId}/comments", initiatorId, eventId)
                        .header("X-Sharer-Author-Id", commentAuthorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCommentDTOJson2))
                .andExpect(status().isCreated());

        commentId2 = objectMapper.readValue(newCommentResult2.andReturn().getResponse().getContentAsString(), CommentDTO.class).getId();

        mockMvc.perform(delete("/admin/events/{eventId}/comments", eventId))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(6)
    void deleteComment() throws Exception {
        // Post comment 3
        newCommentDTO3 = commentGenerator.generateNewCommentDTO();
        String newCommentDTOJson3 = objectMapper.writeValueAsString(newCommentDTO2);

        ResultActions newCommentResult3 = mockMvc.perform(post("/users/{userId}/events/{eventId}/comments", initiatorId, eventId)
                        .header("X-Sharer-Author-Id", commentAuthorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCommentDTOJson3))
                .andExpect(status().isCreated());

        commentId3 = objectMapper.readValue(newCommentResult3.andReturn().getResponse().getContentAsString(), CommentDTO.class).getId();

        // Delete comment 3
        mockMvc.perform(delete("/admin/comments/{commentId}", commentId3))
                .andExpect(status().isNoContent());
    }
}
