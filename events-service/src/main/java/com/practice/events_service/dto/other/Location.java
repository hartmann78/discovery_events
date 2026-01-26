package com.practice.events_service.dto.other;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    @NotNull
    private Float lat;

    @NotNull
    private Float lon;
}
