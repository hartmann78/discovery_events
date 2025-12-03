package com.practice.events_service.service.privateService.impl;

import com.practice.events_service.dto.modelDTO.CommentDTO;
import com.practice.events_service.dto.newDTO.NewCommentDTO;
import com.practice.events_service.dto.updateRequest.UpdateCommentRequest;
import com.practice.events_service.enums.State;
import com.practice.events_service.exception.conflict.EventNotPublishedException;
import com.practice.events_service.exception.other.ForbiddenException;
import com.practice.events_service.mapper.CommentMapper;
import com.practice.events_service.model.Comment;
import com.practice.events_service.model.Event;
import com.practice.events_service.model.User;
import com.practice.events_service.repository.CommentRepository;
import com.practice.events_service.repository.ParticipationRequestRepository;
import com.practice.events_service.service.privateService.PrivateCommentsService;
import com.practice.events_service.utils.CheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrivateCommentsServiceImpl implements PrivateCommentsService {
    private final CommentRepository commentRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    private final CommentMapper commentMapper;
    private final CheckService checkService;

    @Override
    public CommentDTO postComment(Long authorId, Long eventId, NewCommentDTO newCommentDTO) {
        User author = checkService.findUser(authorId);
        Event event = checkService.findEvent(eventId);

        if (!event.getInitiator().getId().equals(authorId) && !participationRequestRepository.checkRequestorConfirmed(authorId, eventId)) {
            throw new ForbiddenException("Комментировать могут только участники события и инициатор!");
        }

        if (event.getState() != State.PUBLISHED) {
            throw new EventNotPublishedException("Событие ещё не опубликовано!");
        }

        if (!event.getCommentsAvailable()) {
            throw new ForbiddenException("Комментарии отключены!");
        }

        Comment comment = commentMapper.createNewComment(newCommentDTO, author, event);
        commentRepository.save(comment);

        return commentMapper.commentToCommentDto(comment);
    }

    @Override
    public CommentDTO updateComment(Long authorId, Long eventId, Long commentId, UpdateCommentRequest updateCommentRequest) {
        checkService.findUser(authorId);
        checkService.findEvent(eventId);
        Comment comment = checkService.findComment(commentId);

        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new ForbiddenException("Удалить комментарий может только автор!");
        }

        if (!comment.getEvent().getId().equals(eventId)) {
            throw new ForbiddenException("Комментарий не относится к текущему событию!");
        }

        comment = commentMapper.patchCommentByUpdateCommentRequest(comment, updateCommentRequest);
        commentRepository.save(comment);

        return commentMapper.commentToCommentDto(comment);
    }

    @Override
    public void deleteComment(Long authorId, Long eventId, Long commentId) {
        checkService.findUser(authorId);
        checkService.findEvent(eventId);
        Comment comment = checkService.findComment(commentId);

        if (!comment.getEvent().getId().equals(eventId)) {
            throw new ForbiddenException("Комментарий не относится к текущему событию!");
        }

        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new ForbiddenException("Комментарий может удалить только автор!");
        }

        commentRepository.deleteById(commentId);
    }
}
