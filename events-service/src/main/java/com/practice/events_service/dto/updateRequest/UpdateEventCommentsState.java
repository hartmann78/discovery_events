package com.practice.events_service.dto.updateRequest;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventCommentsState {
    private Boolean commentsAvailable;
    private Boolean showComments;
}
