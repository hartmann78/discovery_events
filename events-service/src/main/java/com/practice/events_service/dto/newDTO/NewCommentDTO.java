package com.practice.events_service.dto.newDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCommentDTO {
    @NotBlank
    @Length(min = 3, max = 2000)
    private String text;
}
