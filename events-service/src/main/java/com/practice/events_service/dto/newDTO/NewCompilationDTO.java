package com.practice.events_service.dto.newDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@Builder
public class NewCompilationDTO {
    @NotBlank
    @Length(max = 50)
    private String title;

    private Boolean pinned;

    private List<Long> events;
}
