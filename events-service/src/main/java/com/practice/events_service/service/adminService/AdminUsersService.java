package com.practice.events_service.service.adminService;

import com.practice.events_service.dto.modelDTO.UserDTO;
import com.practice.events_service.dto.newDTO.NewUserRequest;

import java.util.List;

public interface AdminUsersService {
    List<UserDTO> getUsers(Long[] ids, int from, int size);

    UserDTO postNewUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);
}
