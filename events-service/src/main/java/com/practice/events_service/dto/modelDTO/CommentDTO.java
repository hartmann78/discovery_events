package com.practice.events_service.dto.modelDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String text;
    private String authorName;
    private Boolean fromEventInitiator;
    private String createdOn;
    private String updatedOn;
}
