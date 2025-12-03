package com.practice.events_service.serviceTests.adminService;

import com.practice.events_service.dto.modelDTO.UserDTO;
import com.practice.events_service.exception.not_found.UserNotFoundException;
import com.practice.events_service.generators.UserGenerator;
import com.practice.events_service.dto.newDTO.NewUserRequest;
import com.practice.events_service.service.adminService.AdminUsersService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AdminUsersServiceTests {
    @Autowired
    private AdminUsersService adminUsersService;
    @Autowired
    private UserGenerator userGenerator;

    private UserDTO userDTO;

    @Test
    @BeforeEach
    void postNewUser() {
        NewUserRequest newUserRequest = userGenerator.generateNewUserRequest();
        userDTO = adminUsersService.postNewUser(newUserRequest);
        assertNotNull(userDTO.getId());
    }

    @Test
    void getUsers() {
        List<UserDTO> users = adminUsersService.getUsers(new Long[]{userDTO.getId()}, 0, 10);
        assertFalse(users.isEmpty());
    }

    @Test
    void deleteUser() {
        adminUsersService.deleteUser(userDTO.getId());

        assertThrows(UserNotFoundException.class, () -> adminUsersService.deleteUser(userDTO.getId()));
    }
}
