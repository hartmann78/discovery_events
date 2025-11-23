package com.practice.events_service.dto.updateRequest;

import com.practice.events_service.dto.other.Location;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class UpdateEventAdminRequest {
    @Length(min = 3, max = 120)
    private String title;

    @Length(min = 20, max = 7000)
    private String description;

    @Length(min = 20, max = 2000)
    private String annotation;

    private String eventDate;

    @PositiveOrZero
    private Long category;

    private Location location;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean paid;

    private Boolean requestModeration;

    private StateAction stateAction;

    private Boolean commentsAvailable;

    private Boolean showComments;

    public enum StateAction {
        PUBLISH_EVENT,
        REJECT_EVENT
    }
}
