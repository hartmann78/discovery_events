package com.practice.events_service.repositoryTests;

import com.practice.events_service.generators.UserGenerator;
import com.practice.events_service.model.User;
import com.practice.events_service.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    private final UserGenerator userGenerator = new UserGenerator();

    private User user;

    @Test
    @BeforeEach
    void save() {
        user = userGenerator.generateUser();
        userRepository.save(user);
    }

    @Test
    void findById() {
        assertNotNull(user.getId());

        Optional<User> checkUser = userRepository.findById(user.getId());
        assertTrue(checkUser.isPresent());
        assertNotNull(checkUser.get().getId());
        assertEquals(user, checkUser.get());
    }

    @Test
    void findAll() {
        List<User> findAllUsers = userRepository.findAll();
        assertTrue(findAllUsers.contains(user));
    }

    @Test
    void update() {
        User updateUser = userGenerator.generateUser();

        user.setName(updateUser.getName());
        user.setEmail(updateUser.getEmail());
        userRepository.save(user);

        Optional<User> checkUpdatedUser = userRepository.findById(user.getId());
        assertTrue(checkUpdatedUser.isPresent());
        assertEquals(user.getId(), checkUpdatedUser.get().getId());
        assertEquals(updateUser.getName(), checkUpdatedUser.get().getName());
        assertEquals(updateUser.getEmail(), checkUpdatedUser.get().getEmail());
    }

    @Test
    void delete() {
        userRepository.deleteById(user.getId());

        Optional<User> checkUser = userRepository.findById(user.getId());
        assertTrue(checkUser.isEmpty());
    }

    @Test
    void checkUserEmailExists() {
        Boolean check = userRepository.checkUserEmailExists(user.getEmail());
        assertTrue(check);
    }
}
