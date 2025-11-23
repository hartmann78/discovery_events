package com.practice.events_service.service.adminService;

import com.practice.events_service.dto.modelDTO.CommentDTO;

import java.util.List;

public interface AdminCommentsService {
    List<CommentDTO> getAllUserComments(Long userId, int from, int size);

    List<CommentDTO> getAllEventComments(Long eventId, int from, int size);

    CommentDTO findCommentById(Long commentId);

    void deleteAllUserComments(Long userId);

    void deleteAllEventComments(Long eventId);

    void deleteComment(Long commentId);
}
