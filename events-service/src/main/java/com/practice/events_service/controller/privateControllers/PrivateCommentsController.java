package com.practice.events_service.controller.privateControllers;

import com.practice.events_service.dto.modelDTO.CommentDTO;
import com.practice.events_service.dto.newDTO.NewCommentDTO;
import com.practice.events_service.dto.updateRequest.UpdateCommentRequest;
import com.practice.events_service.service.privateService.PrivateCommentsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.practice.events_service.utils.SetLog.setLog;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/comments")
public class PrivateCommentsController {
    private final PrivateCommentsService privateCommentsService;

    @PostMapping
    public ResponseEntity<CommentDTO> postComment(@PathVariable Long userId,
                                                  @PathVariable Long eventId,
                                                  @RequestHeader("X-Sharer-Author-Id") Long authorId,
                                                  @Valid @RequestBody NewCommentDTO newCommentDTO,
                                                  HttpServletRequest request) {
        setLog(log, request);
        return new ResponseEntity<>(privateCommentsService.postComment(authorId, eventId, newCommentDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long userId,
                                                    @PathVariable Long eventId,
                                                    @PathVariable Long commentId,
                                                    @RequestHeader("X-Sharer-Author-Id") Long authorId,
                                                    @Valid @RequestBody UpdateCommentRequest updateCommentRequest,
                                                    HttpServletRequest request) {
        setLog(log, request);
        return new ResponseEntity<>(privateCommentsService.updateComment(authorId, eventId, commentId, updateCommentRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<HttpStatus> deleteComment(@PathVariable Long userId,
                                                    @PathVariable Long eventId,
                                                    @PathVariable Long commentId,
                                                    @RequestHeader("X-Sharer-Author-Id") Long authorId,
                                                    HttpServletRequest request) {
        setLog(log, request);
        privateCommentsService.deleteComment(authorId, eventId, commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
