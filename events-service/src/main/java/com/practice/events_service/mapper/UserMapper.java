package com.practice.events_service.mapper;

import com.practice.events_service.dto.modelDTO.UserDTO;
import com.practice.events_service.dto.shortDTO.UserShortDTO;
import com.practice.events_service.dto.newDTO.NewUserRequest;
import com.practice.events_service.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {
    public User createNewUser(NewUserRequest newUserRequest) {
        return User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
    }

    public UserDTO userToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public UserShortDTO userToUserShortDTO(User user) {
        return UserShortDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public List<UserDTO> userListToUserDTOList(List<User> users) {
        List<UserDTO> userDTOS = new ArrayList<>();

        for (User user : users) {
            userDTOS.add(userToUserDTO(user));
        }

        return userDTOS;
    }
}
