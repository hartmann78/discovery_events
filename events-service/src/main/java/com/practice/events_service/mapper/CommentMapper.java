package com.practice.events_service.mapper;

import com.practice.events_service.dto.modelDTO.CommentDTO;
import com.practice.events_service.dto.newDTO.NewCommentDTO;
import com.practice.events_service.dto.updateRequest.UpdateCommentRequest;
import com.practice.events_service.model.Comment;
import com.practice.events_service.model.Event;
import com.practice.events_service.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class CommentMapper {
    public Comment createNewComment(NewCommentDTO newCommentDTO, User author, Event event) {
        return Comment.builder()
                .text(newCommentDTO.getText())
                .fromEventInitiator(event.getInitiator().equals(author))
                .author(author)
                .event(event)
                .createdOn(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    public CommentDTO commentToCommentDto(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .text(comment.getText())
                .fromEventInitiator(comment.getFromEventInitiator())
                .authorName(comment.getAuthor().getName())
                .createdOn(comment.getCreatedOn())
                .updatedOn(comment.getUpdatedOn())
                .build();
    }

    public List<CommentDTO> commentListToCommentDTOList(List<Comment> comments) {
        List<CommentDTO> commentDTOS = new ArrayList<>();

        for (Comment comment : comments) {
            commentDTOS.add(commentToCommentDto(comment));
        }

        return commentDTOS;
    }

    public Comment patchCommentByUpdateCommentRequest(Comment comment, UpdateCommentRequest updateCommentRequest) {
        comment.setText(updateCommentRequest.getText());
        comment.setUpdatedOn(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return comment;
    }
}
