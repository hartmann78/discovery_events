package com.practice.events_service.dto.newDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.practice.events_service.dto.other.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewEventDTO {
    @NotBlank
    @Length(min = 3, max = 120)
    private String title;

    @NotBlank
    @Length(min = 20, max = 7000)
    private String description;

    @NotBlank
    @Length(min = 20, max = 2000)
    private String annotation;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    @PositiveOrZero
    private Long category;

    @NotNull
    private Location location;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean paid;

    private Boolean requestModeration;

    private Boolean commentsAvailable;

    private Boolean showComments;
}
