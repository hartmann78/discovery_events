package com.practice.events_service.dto.shortDTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDTO {
    private Long id;
    private String name;
}
