package com.practice.events_service.dto.newDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class NewCategoryDTO {
    @NotBlank
    @Length(max = 50)
    private String name;
}
