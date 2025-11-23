package com.practice.events_service.controller.adminControllers;

import com.practice.events_service.dto.modelDTO.EventFullDTO;
import com.practice.events_service.dto.updateRequest.UpdateEventAdminRequest;
import com.practice.events_service.dto.updateRequest.UpdateEventCommentsState;
import com.practice.events_service.service.adminService.AdminEventsService;
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
@RequestMapping("/admin/events")
public class AdminEventsController {
    private final AdminEventsService adminEventsService;

    @GetMapping
    public ResponseEntity<List<EventFullDTO>> getEvents(@RequestParam(required = false) Long[] users,
                                                        @RequestParam(required = false) String[] states,
                                                        @RequestParam(required = false) Long[] categories,
                                                        @RequestParam(required = false) String rangeStart,
                                                        @RequestParam(required = false) String rangeEnd,
                                                        @RequestParam(required = false, defaultValue = "0") int from,
                                                        @RequestParam(required = false, defaultValue = "10") int size) {
        return new ResponseEntity<>(adminEventsService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDTO> patchEvent(@PathVariable Long eventId,
                                                   @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        return new ResponseEntity<>(adminEventsService.patchEventById(eventId, updateEventAdminRequest), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}/comments")
    public ResponseEntity<EventFullDTO> patchEventCommentsState(@PathVariable Long eventId,
                                                                @RequestBody @Valid UpdateEventCommentsState updateEventCommentsState) {
        return new ResponseEntity<>(adminEventsService.patchEventCommentsState(eventId, updateEventCommentsState), HttpStatus.OK);
    }
}
