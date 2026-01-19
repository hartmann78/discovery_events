package com.practice.events_service.controller.adminControllers;

import com.practice.events_service.dto.modelDTO.UserDTO;
import com.practice.events_service.dto.newDTO.NewUserRequest;
import com.practice.events_service.service.adminService.AdminUsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.practice.events_service.utils.SetLog.setLog;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUsersController {
    private final AdminUsersService adminUsersService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers(@RequestParam(required = false) Long[] ids,
                                                  @RequestParam(required = false, defaultValue = "0") int from,
                                                  @RequestParam(required = false, defaultValue = "10") int size,
                                                  HttpServletRequest request) {
        setLog(log, request);
        return new ResponseEntity<>(adminUsersService.getUsers(ids, from, size), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDTO> postNewUser(@RequestBody @Valid NewUserRequest newUserRequest,
                                               HttpServletRequest request) {
        setLog(log, request);
        return new ResponseEntity<>(adminUsersService.postNewUser(newUserRequest), HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long userId,
                                                 HttpServletRequest request) {
        setLog(log, request);
        adminUsersService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
