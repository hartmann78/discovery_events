package com.practice.events_service.serviceTests.privateService;

import com.practice.events_service.dto.eventRequestStatusUpdate.EventRequestStatusUpdateRequest;
import com.practice.events_service.dto.modelDTO.*;
import com.practice.events_service.dto.newDTO.NewCategoryDTO;
import com.practice.events_service.dto.newDTO.NewCommentDTO;
import com.practice.events_service.dto.newDTO.NewEventDTO;
import com.practice.events_service.dto.newDTO.NewUserRequest;
import com.practice.events_service.dto.updateRequest.UpdateCommentRequest;
import com.practice.events_service.dto.updateRequest.UpdateEventAdminRequest;
import com.practice.events_service.exception.conflict.EventNotPublishedException;
import com.practice.events_service.exception.not_found.CommentNotFoundException;
import com.practice.events_service.exception.other.ForbiddenException;
import com.practice.events_service.generators.*;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class PrivateCommentsServiceTests {
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

    private UserDTO initiatorDTO;
    private CategoryDTO categoryDTO;
    private EventFullDTO eventFullDTO1;

    private UserDTO commentAuthorDTO;
    private CommentDTO commentDTO1;

    @Test
    @BeforeEach
    void postComment() {
        NewUserRequest newUserRequest = userGenerator.generateNewUserRequest();
        initiatorDTO = adminUsersService.postNewUser(newUserRequest);

        NewCategoryDTO newCategoryDTO = categoryGenerator.generateNewCategoryDTO();
        categoryDTO = adminCategoriesService.addNewCategory(newCategoryDTO);

        NewEventDTO newEventDTO = eventGenerator.generateNewEventDTO(categoryDTO.getId());
        eventFullDTO1 = privateEventsService.addNewEvent(initiatorDTO.getId(), newEventDTO);

        UpdateEventAdminRequest updateEventAdminRequest = eventGenerator.generateUpdateEventAdminRequest(categoryDTO.getId());
        updateEventAdminRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);
        eventFullDTO1 = adminEventsService.patchEventById(eventFullDTO1.getId(), updateEventAdminRequest);

        NewUserRequest author = userGenerator.generateNewUserRequest();
        commentAuthorDTO = adminUsersService.postNewUser(author);

        ParticipationRequestDTO participationRequestDTO1 = privateRequestsService.postEventParticipationRequest(commentAuthorDTO.getId(), eventFullDTO1.getId());

        EventRequestStatusUpdateRequest updateRequest1 = eventRequestStatusUpdateRequestGenerator.generateUpdateRequest
                (List.of(participationRequestDTO1.getId()), EventRequestStatusUpdateRequest.Status.CONFIRMED);
        privateEventsService.patchEventRequestsByUserId(initiatorDTO.getId(), eventFullDTO1.getId(), updateRequest1);

        NewCommentDTO newCommentDTO1 = commentGenerator.generateNewCommentDTO();
        commentDTO1 = privateCommentsService.postComment(commentAuthorDTO.getId(), eventFullDTO1.getId(), newCommentDTO1);

        assertNotNull(commentDTO1.getId());
        assertEquals(author.getName(), commentDTO1.getAuthorName());
        assertEquals(newCommentDTO1.getText(), commentDTO1.getText());
        assertFalse(commentDTO1.getFromEventInitiator());
        assertNotNull(commentDTO1.getCreatedOn());
        assertNull(commentDTO1.getUpdatedOn());
    }

    @Test
    void updateComment() {
        UpdateCommentRequest updateCommentRequest = commentGenerator.generateUpdateCommentRequest();

        CommentDTO updatedComment = privateCommentsService.updateComment(commentAuthorDTO.getId(), eventFullDTO1.getId(), commentDTO1.getId(), updateCommentRequest);

        assertEquals(updateCommentRequest.getText(), updatedComment.getText());
        assertNotNull(updatedComment.getUpdatedOn());
    }

    @Test
    void notParticipantOrInitiatorTest() {
        NewEventDTO newEventDTO2 = eventGenerator.generateNewEventDTO(categoryDTO.getId());
        EventFullDTO eventFullDTO2 = privateEventsService.addNewEvent(initiatorDTO.getId(), newEventDTO2);

        NewCommentDTO newCommentDTO2 = commentGenerator.generateNewCommentDTO();
        assertThrows(ForbiddenException.class, () -> privateCommentsService.postComment(commentAuthorDTO.getId(), eventFullDTO2.getId(), newCommentDTO2));
    }

    @Test
    void eventNotPublishedTest() {
        NewEventDTO newEventDTO3 = eventGenerator.generateNewEventDTO(categoryDTO.getId());
        EventFullDTO eventFullDTO3 = privateEventsService.addNewEvent(initiatorDTO.getId(), newEventDTO3);

        NewCommentDTO newCommentDTO2 = commentGenerator.generateNewCommentDTO();
        assertThrows(EventNotPublishedException.class, () -> privateCommentsService.postComment(initiatorDTO.getId(), eventFullDTO3.getId(), newCommentDTO2));

    }

    @Test
    void commentsUnavailableTest() {
        UpdateEventAdminRequest updateEventAdminRequest = eventGenerator.generateUpdateEventAdminRequest(categoryDTO.getId());
        updateEventAdminRequest.setCommentsAvailable(false);
        updateEventAdminRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);

        NewEventDTO newEventDTO4 = eventGenerator.generateNewEventDTO(categoryDTO.getId());
        EventFullDTO eventFullDTO4 = privateEventsService.addNewEvent(initiatorDTO.getId(), newEventDTO4);
        adminEventsService.patchEventById(eventFullDTO4.getId(), updateEventAdminRequest);

        ParticipationRequestDTO participationRequestDTO2 = privateRequestsService.postEventParticipationRequest(commentAuthorDTO.getId(), eventFullDTO4.getId());
        EventRequestStatusUpdateRequest updateRequest2 = eventRequestStatusUpdateRequestGenerator.generateUpdateRequest
                (List.of(participationRequestDTO2.getId()), EventRequestStatusUpdateRequest.Status.CONFIRMED);
        privateEventsService.patchEventRequestsByUserId(initiatorDTO.getId(), eventFullDTO4.getId(), updateRequest2);

        NewCommentDTO newCommentDTO2 = commentGenerator.generateNewCommentDTO();
        assertThrows(ForbiddenException.class, () -> privateCommentsService.postComment(commentAuthorDTO.getId(), eventFullDTO4.getId(), newCommentDTO2));
    }

    @Test
    void deleteComment() {
        privateCommentsService.deleteComment(commentAuthorDTO.getId(), eventFullDTO1.getId(), commentDTO1.getId());

        assertThrows(CommentNotFoundException.class, () -> privateCommentsService.deleteComment(commentAuthorDTO.getId(), eventFullDTO1.getId(), commentDTO1.getId()));
    }
}
