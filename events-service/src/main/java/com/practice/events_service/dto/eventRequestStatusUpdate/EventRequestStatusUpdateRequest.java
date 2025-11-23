package com.practice.events_service.dto.eventRequestStatusUpdate;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateRequest {
    @NotEmpty
    private List<Long> requestIds;

    @NotNull
    private Status status;

    public enum Status {
        CONFIRMED,
        REJECTED
    }
}

