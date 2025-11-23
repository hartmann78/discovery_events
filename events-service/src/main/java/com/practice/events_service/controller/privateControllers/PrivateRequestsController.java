package com.practice.events_service.controller.privateControllers;

import com.practice.events_service.dto.modelDTO.ParticipationRequestDTO;
import com.practice.events_service.service.privateService.PrivateRequestsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestsController {
    private final PrivateRequestsService privateRequestsService;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDTO>> getUserEventRequests(@PathVariable Long userId) {
        return new ResponseEntity<>(privateRequestsService.getUserEventRequests(userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDTO> postEventParticipationRequest(@PathVariable Long userId,
                                                                                 @RequestParam Long eventId) {
        return new ResponseEntity<>(privateRequestsService.postEventParticipationRequest(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDTO> cancelEventParticipationRequest(@PathVariable Long userId,
                                                                                   @PathVariable Long requestId) {
        return new ResponseEntity<>(privateRequestsService.cancelEventParticipationRequest(userId, requestId), HttpStatus.OK);
    }
}
