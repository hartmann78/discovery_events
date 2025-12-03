package com.practice.events_service.service.adminService.impl;

import com.practice.events_service.dto.modelDTO.UserDTO;
import com.practice.events_service.exception.conflict.EmailExistsException;
import com.practice.events_service.mapper.UserMapper;
import com.practice.events_service.dto.newDTO.NewUserRequest;
import com.practice.events_service.model.User;
import com.practice.events_service.repository.UserRepository;
import com.practice.events_service.service.adminService.AdminUsersService;
import com.practice.events_service.utils.CheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUsersServiceImpl implements AdminUsersService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CheckService checkService;

    @Override
    public List<UserDTO> getUsers(Long[] ids, int from, int size) {
        checkService.fromAndSizeCheck(from, size);
        List<User> users = userRepository.getUsersByIds(ids, from, size);

        return userMapper.userListToUserDTOList(users);
    }

    @Override
    public UserDTO postNewUser(NewUserRequest newUserRequest) {
        if (userRepository.checkUserEmailExists(newUserRequest.getEmail())) {
            throw new EmailExistsException("Почтовый адрес " + newUserRequest.getEmail() + " уже занят!");
        }

        User newUser = userMapper.createNewUser(newUserRequest);
        userRepository.save(newUser);

        return userMapper.userToUserDTO(newUser);
    }

    @Override
    public void deleteUser(Long userId) {
        checkService.findUser(userId);

        userRepository.deleteById(userId);
    }
}
