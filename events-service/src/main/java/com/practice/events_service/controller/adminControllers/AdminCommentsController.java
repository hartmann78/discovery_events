package com.practice.events_service.controller.adminControllers;

import com.practice.events_service.dto.modelDTO.CommentDTO;
import com.practice.events_service.service.adminService.AdminCommentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminCommentsController {
    private final AdminCommentsService adminCommentsService;

    @GetMapping("/admin/users/{userId}/comments")
    public ResponseEntity<List<CommentDTO>> getAllUserComments(@PathVariable Long userId,
                                                               @RequestParam(required = false, defaultValue = "0") int from,
                                                               @RequestParam(required = false, defaultValue = "10") int size) {
        return new ResponseEntity<>(adminCommentsService.getAllUserComments(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/admin/events/{eventId}/comments")
    public ResponseEntity<List<CommentDTO>> getAllEventComments(@PathVariable Long eventId,
                                                                @RequestParam(required = false, defaultValue = "0") int from,
                                                                @RequestParam(required = false, defaultValue = "10") int size) {
        return new ResponseEntity<>(adminCommentsService.getAllEventComments(eventId, from, size), HttpStatus.OK);
    }

    @GetMapping("/admin/comments/{commentId}")
    public ResponseEntity<CommentDTO> findCommentById(@PathVariable Long commentId) {
        return new ResponseEntity<>(adminCommentsService.findCommentById(commentId), HttpStatus.OK);
    }

    @DeleteMapping("/admin/users/{userId}/comments")
    public ResponseEntity<HttpStatus> deleteAllUserComments(@PathVariable Long userId) {
        adminCommentsService.deleteAllUserComments(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/admin/events/{eventId}/comments")
    public ResponseEntity<HttpStatus> deleteAllEventComments(@PathVariable Long eventId) {
        adminCommentsService.deleteAllEventComments(eventId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/admin/comments/{commentId}")
    public ResponseEntity<HttpStatus> deleteComment(@PathVariable Long commentId) {
        adminCommentsService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
