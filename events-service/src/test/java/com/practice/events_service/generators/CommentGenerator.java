package com.practice.events_service.generators;

import com.practice.events_service.dto.newDTO.NewCommentDTO;
import com.practice.events_service.dto.updateRequest.UpdateCommentRequest;
import com.practice.events_service.model.Comment;
import com.practice.events_service.model.Event;
import com.practice.events_service.model.User;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentGenerator {
    public Comment generateComment(User author, Event event) {
        return Comment.builder()
                .text(generateText())
                .fromEventInitiator(event.getInitiator().equals(author))
                .author(author)
                .event(event)
                .createdOn(LocalDateTime.now())
                .build();
    }

    public NewCommentDTO generateNewCommentDTO() {
        return NewCommentDTO.builder()
                .text(generateText())
                .build();
    }

    public UpdateCommentRequest generateUpdateCommentRequest() {
        return UpdateCommentRequest.builder()
                .text(generateText())
                .build();
    }

    private String generateText() {
        return RandomString.make(2000);
    }
}
