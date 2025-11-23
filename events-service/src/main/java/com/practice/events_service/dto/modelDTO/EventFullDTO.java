package com.practice.events_service.dto.modelDTO;

import com.practice.events_service.dto.other.Location;
import com.practice.events_service.dto.shortDTO.UserShortDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDTO {
    private Long id;
    private String title;
    private String description;
    private String annotation;
    private String eventDate;
    private UserShortDTO initiator;
    private CategoryDTO category;
    private Location location;
    private Integer participantLimit;
    private Boolean paid;
    private Boolean requestModeration;
    private Long confirmedRequests;
    private String createdOn;
    private String publishedOn;
    private State state;
    private Long views;
    private List<CommentDTO> comments;

    public enum State {
        PENDING,
        PUBLISHED,
        CANCELED
    }
}
