package com.practice.events_service.serviceTests.adminService;

import com.practice.events_service.dto.eventRequestStatusUpdate.EventRequestStatusUpdateRequest;
import com.practice.events_service.dto.modelDTO.*;
import com.practice.events_service.dto.newDTO.NewCategoryDTO;
import com.practice.events_service.dto.newDTO.NewCommentDTO;
import com.practice.events_service.dto.newDTO.NewEventDTO;
import com.practice.events_service.dto.newDTO.NewUserRequest;
import com.practice.events_service.dto.updateRequest.UpdateEventAdminRequest;
import com.practice.events_service.exception.not_found.CommentNotFoundException;
import com.practice.events_service.generators.*;
import com.practice.events_service.service.adminService.AdminCategoriesService;
import com.practice.events_service.service.adminService.AdminCommentsService;
import com.practice.events_service.service.adminService.AdminEventsService;
import com.practice.events_service.service.adminService.AdminUsersService;
import com.practice.events_service.service.privateService.PrivateCommentsService;
import com.practice.events_service.service.privateService.PrivateEventsService;
import com.practice.events_service.service.privateService.PrivateRequestsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminCommentsServiceTests {
    @Autowired
    private AdminUsersService adminUsersService;
    @Autowired
    private AdminCategoriesService adminCategoriesService;
    @Autowired
    private AdminEventsService adminEventsService;
    @Autowired
    private AdminCommentsService adminCommentsService;
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
    private static UserDTO commentAuthorDTO;
    private static CategoryDTO categoryDTO;
    private static EventFullDTO eventFullDTO;
    private static ParticipationRequestDTO participationRequestDTO;
    private static CommentDTO commentDTO1;
    private static CommentDTO commentDTO2;
    private static CommentDTO commentDTO3;

    @Test
    @Order(1)
    void getAllUserComments() {
        NewUserRequest newUserRequest = userGenerator.generateNewUserRequest();
        initiatorDTO = adminUsersService.postNewUser(newUserRequest);

        NewCategoryDTO newCategoryDTO = categoryGenerator.generateNewCategoryDTO();
        categoryDTO = adminCategoriesService.addNewCategory(newCategoryDTO);

        NewEventDTO newEventDTO = eventGenerator.generateNewEventDTO(categoryDTO.getId());
        newEventDTO.setEventDate(LocalDateTime.now().plusHours(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        eventFullDTO = privateEventsService.addNewEvent(initiatorDTO.getId(), newEventDTO);

        UpdateEventAdminRequest updateEventAdminRequest = eventGenerator.generateUpdateEventAdminRequest(categoryDTO.getId());
        updateEventAdminRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);
        eventFullDTO = adminEventsService.patchEventById(eventFullDTO.getId(), updateEventAdminRequest);

        NewUserRequest author = userGenerator.generateNewUserRequest();
        commentAuthorDTO = adminUsersService.postNewUser(author);

        participationRequestDTO = privateRequestsService.postEventParticipationRequest(commentAuthorDTO.getId(), eventFullDTO.getId());

        EventRequestStatusUpdateRequest updateRequest = eventRequestStatusUpdateRequestGenerator.generateUpdateRequest
                (List.of(participationRequestDTO.getId()), EventRequestStatusUpdateRequest.Status.CONFIRMED);
        privateEventsService.patchEventRequestsByUserId(initiatorDTO.getId(), eventFullDTO.getId(), updateRequest);

        // First comment
        NewCommentDTO newCommentDTO1 = commentGenerator.generateNewCommentDTO();
        commentDTO1 = privateCommentsService.postComment(commentAuthorDTO.getId(), eventFullDTO.getId(), newCommentDTO1);

        // Second comment
        NewCommentDTO newCommentDTO2 = commentGenerator.generateNewCommentDTO();
        commentDTO2 = privateCommentsService.postComment(commentAuthorDTO.getId(), eventFullDTO.getId(), newCommentDTO2);

        List<CommentDTO> getAllUserComments = adminCommentsService.getAllUserComments(commentAuthorDTO.getId(), 0, 10);
        assertEquals(2, getAllUserComments.size());
    }

    @Test
    @Order(2)
    void getAllEventComments() {
        List<CommentDTO> getAllEventComments = adminCommentsService.getAllEventComments(eventFullDTO.getId(), 0, 10);
        assertEquals(2, getAllEventComments.size());
    }

    @Test
    @Order(3)
    void findCommentById() {
        CommentDTO findComment = adminCommentsService.findCommentById(commentDTO1.getId());

        assertEquals(commentDTO1, findComment);
    }

    @Test
    @Order(4)
    void deleteComment() {
        adminCommentsService.deleteComment(commentDTO1.getId());

        assertThrows(CommentNotFoundException.class, () -> adminCommentsService.deleteComment(commentDTO1.getId()));
    }

    @Test
    @Order(5)
    void deleteAllUserComments() {
        adminCommentsService.deleteAllUserComments(commentAuthorDTO.getId());

        List<CommentDTO> getAllUserComments = adminCommentsService.getAllUserComments(commentAuthorDTO.getId(), 0, 10);
        assertTrue(getAllUserComments.isEmpty());

    }

    @Test
    @Order(6)
    void deleteAllEventComments() {
        // Third comment
        NewCommentDTO newCommentDTO3 = commentGenerator.generateNewCommentDTO();
        commentDTO3 = privateCommentsService.postComment(commentAuthorDTO.getId(), eventFullDTO.getId(), newCommentDTO3);

        adminCommentsService.deleteAllEventComments(eventFullDTO.getId());

        List<CommentDTO> getAllEventComments = adminCommentsService.getAllEventComments(eventFullDTO.getId(), 0, 10);
        assertTrue(getAllEventComments.isEmpty());
    }
}
