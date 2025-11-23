package com.practice.events_service.service.privateService;

import com.practice.events_service.dto.modelDTO.CommentDTO;
import com.practice.events_service.dto.newDTO.NewCommentDTO;
import com.practice.events_service.dto.updateRequest.UpdateCommentRequest;

public interface PrivateCommentsService {
    CommentDTO postComment(Long authorId, Long eventId, NewCommentDTO newCommentDTO);

    CommentDTO updateComment(Long authorId, Long eventId, Long commentId, UpdateCommentRequest updateCommentRequest);

    void deleteComment(Long authorId, Long eventId, Long commentId);
}
