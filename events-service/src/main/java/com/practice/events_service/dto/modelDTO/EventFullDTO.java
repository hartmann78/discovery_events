package com.practice.events_service.dto.modelDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.practice.events_service.dto.other.Location;
import com.practice.events_service.dto.shortDTO.UserShortDTO;
import com.practice.events_service.enums.State;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFullDTO {
    private Long id;
    private String title;
    private String description;
    private String annotation;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortDTO initiator;
    private CategoryDTO category;
    private Location location;
    private Integer participantLimit;
    private Boolean paid;
    private Boolean requestModeration;
    private Long confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private State state;
    private Long views;
    private List<CommentDTO> comments;
}
