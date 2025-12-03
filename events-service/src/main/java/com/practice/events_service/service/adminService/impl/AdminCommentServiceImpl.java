package com.practice.events_service.service.adminService.impl;

import com.practice.events_service.dto.modelDTO.CommentDTO;
import com.practice.events_service.mapper.CommentMapper;
import com.practice.events_service.model.Comment;
import com.practice.events_service.repository.CommentRepository;
import com.practice.events_service.service.adminService.AdminCommentsService;
import com.practice.events_service.utils.CheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentsService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CheckService checkService;

    @Override
    public List<CommentDTO> getAllUserComments(Long userId, int from, int size) {
        List<Comment> comments = commentRepository.getAllUserComments(userId, from, size);

        return commentMapper.commentListToCommentDTOList(comments);
    }

    @Override
    public List<CommentDTO> getAllEventComments(Long eventId, int from, int size) {
        List<Comment> comments = commentRepository.getAllEventComments(eventId, from, size);

        return commentMapper.commentListToCommentDTOList(comments);
    }

    @Override
    public CommentDTO findCommentById(Long commentId) {
        Comment comment = checkService.findComment(commentId);

        return commentMapper.commentToCommentDto(comment);
    }

    @Override
    public void deleteAllUserComments(Long userId) {
        checkService.findUser(userId);

        commentRepository.deleteAllUserComments(userId);
    }

    @Override
    public void deleteAllEventComments(Long eventId) {
        checkService.findEvent(eventId);

        commentRepository.deleteAllEventComments(eventId);
    }

    @Override
    public void deleteComment(Long commentId) {
        checkService.findComment(commentId);

        commentRepository.deleteById(commentId);
    }
}
