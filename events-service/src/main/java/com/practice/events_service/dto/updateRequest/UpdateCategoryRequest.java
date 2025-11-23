package com.practice.events_service.dto.updateRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class UpdateCategoryRequest {
    @Length(max = 50)
    @NotBlank
    private String name;
}
