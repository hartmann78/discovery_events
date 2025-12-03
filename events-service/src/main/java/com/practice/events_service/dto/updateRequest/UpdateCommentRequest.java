package com.practice.events_service.dto.updateRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCommentRequest {
    @NotBlank
    @Length(min = 3, max = 2000)
    private String text;
}
