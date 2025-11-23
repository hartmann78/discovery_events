package com.practice.events_service.serviceTests.adminService;

import com.practice.events_service.dto.modelDTO.UserDTO;
import com.practice.events_service.exception.not_found.UserNotFoundException;
import com.practice.events_service.generators.UserGenerator;
import com.practice.events_service.dto.newDTO.NewUserRequest;
import com.practice.events_service.service.adminService.AdminUsersService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminUsersServiceTests {
    @Autowired
    private AdminUsersService adminUsersService;
    @Autowired
    private UserGenerator userGenerator;

    private static UserDTO userDTO;

    @Test
    @Order(1)
    void postNewUser() {
        NewUserRequest newUserRequest = userGenerator.generateNewUserRequest();
        userDTO = adminUsersService.postNewUser(newUserRequest);
        assertNotNull(userDTO.getId());
    }

    @Test
    @Order(2)
    void getUsers() {
        List<UserDTO> users = adminUsersService.getUsers(new Long[]{userDTO.getId()}, 0, 10);
        assertFalse(users.isEmpty());
    }

    @Test
    @Order(3)
    void deleteUser() {
        adminUsersService.deleteUser(userDTO.getId());

        assertThrows(UserNotFoundException.class, () -> adminUsersService.deleteUser(userDTO.getId()));
    }
}
