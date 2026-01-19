package com.practice.events_service.controller.adminControllers;

import com.practice.events_service.dto.modelDTO.EventFullDTO;
import com.practice.events_service.dto.updateRequest.UpdateEventAdminRequest;
import com.practice.events_service.dto.updateRequest.UpdateEventCommentsState;
import com.practice.events_service.service.adminService.AdminEventsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.practice.events_service.utils.SetLog.setLog;

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
                                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                        @RequestParam(required = false, defaultValue = "0") int from,
                                                        @RequestParam(required = false, defaultValue = "10") int size,
                                                        HttpServletRequest request) {
        setLog(log, request);
        return new ResponseEntity<>(adminEventsService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDTO> patchEvent(@PathVariable Long eventId,
                                                   @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest,
                                                   HttpServletRequest request) {
        setLog(log, request);
        return new ResponseEntity<>(adminEventsService.patchEventById(eventId, updateEventAdminRequest), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}/comments")
    public ResponseEntity<EventFullDTO> patchEventCommentsState(@PathVariable Long eventId,
                                                                @RequestBody @Valid UpdateEventCommentsState updateEventCommentsState,
                                                                HttpServletRequest request) {
        setLog(log, request);
        return new ResponseEntity<>(adminEventsService.patchEventCommentsState(eventId, updateEventCommentsState), HttpStatus.OK);
    }
}
