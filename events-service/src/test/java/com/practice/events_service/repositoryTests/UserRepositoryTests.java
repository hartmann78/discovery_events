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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    private final UserGenerator userGenerator = new UserGenerator();

    private User user;

    @BeforeEach
    void save() {
        user = userGenerator.generateUser();
        userRepository.save(user);
    }

    @Test
    @Order(1)
    void findById() {
        assertNotNull(user.getId());

        Optional<User> checkUser = userRepository.findById(user.getId());
        assertTrue(checkUser.isPresent());
        assertNotNull(checkUser.get().getId());
        assertEquals(user, checkUser.get());
    }

    @Test
    @Order(2)
    void checkUserEmailExists() {
        Boolean check = userRepository.checkUserEmailExists(user.getEmail());
        assertTrue(check);
    }

    @Test
    @Order(3)
    void findAll() {
        List<User> findAllUsers = userRepository.findAll();
        assertTrue(findAllUsers.contains(user));
    }

    @Test
    @Order(4)
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
    @Order(5)
    void delete() {
        userRepository.deleteById(user.getId());

        Optional<User> checkUser = userRepository.findById(user.getId());
        assertTrue(checkUser.isEmpty());
    }
}
