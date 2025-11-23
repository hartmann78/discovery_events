package com.practice.events_service.controller.privateControllers;

import com.practice.events_service.dto.modelDTO.EventFullDTO;
import com.practice.events_service.dto.shortDTO.EventShortDTO;
import com.practice.events_service.dto.newDTO.NewEventDTO;
import com.practice.events_service.dto.modelDTO.ParticipationRequestDTO;
import com.practice.events_service.dto.eventRequestStatusUpdate.EventRequestStatusUpdateRequest;
import com.practice.events_service.dto.eventRequestStatusUpdate.EventRequestStatusUpdateResult;
import com.practice.events_service.dto.updateRequest.UpdateEventCommentsState;
import com.practice.events_service.dto.updateRequest.UpdateEventUserRequest;
import com.practice.events_service.service.privateService.PrivateEventsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventsController {
    private final PrivateEventsService privateEventsService;

    @GetMapping
    public ResponseEntity<List<EventShortDTO>> getEvents(@PathVariable Long userId,
                                                         @RequestParam(required = false, defaultValue = "0") int from,
                                                         @RequestParam(required = false, defaultValue = "10") int size) {
        return new ResponseEntity<>(privateEventsService.getEvents(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDTO> getEventById(@PathVariable Long userId,
                                                     @PathVariable Long eventId) {
        return new ResponseEntity<>(privateEventsService.getEventByUserId(userId, eventId), HttpStatus.OK);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDTO>> getEventRequests(@PathVariable Long userId,
                                                                          @PathVariable Long eventId) {
        return new ResponseEntity<>(privateEventsService.getEventRequestsByUserIdAndEventId(userId, eventId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<EventFullDTO> addNewEvent(@PathVariable Long userId,
                                                    @RequestBody @Valid NewEventDTO newEventDTO) {
        return new ResponseEntity<>(privateEventsService.addNewEvent(userId, newEventDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDTO> patchEvent(@PathVariable Long userId,
                                                   @PathVariable Long eventId,
                                                   @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return new ResponseEntity<>(privateEventsService.patchEventByUserId(userId, eventId, updateEventUserRequest), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> patchEventRequests(@PathVariable Long userId,
                                                                             @PathVariable Long eventId,
                                                                             @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return new ResponseEntity<>(privateEventsService.patchEventRequestsByUserId(userId, eventId, eventRequestStatusUpdateRequest), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}/comments")
    public ResponseEntity<EventFullDTO> patchEventCommentsState(@PathVariable Long userId,
                                                                @PathVariable Long eventId,
                                                                @RequestBody @Valid UpdateEventCommentsState updateEventCommentsState) {
        return new ResponseEntity<>(privateEventsService.patchEventCommentsState(userId, eventId, updateEventCommentsState), HttpStatus.OK);
    }
}
