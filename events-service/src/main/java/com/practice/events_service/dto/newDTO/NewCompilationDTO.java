package com.practice.events_service.dto.newDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCompilationDTO {
    @NotBlank
    @Length(max = 50)
    private String title;

    private Boolean pinned;
    private List<Long> events;
}
