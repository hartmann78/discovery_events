package com.practice.events_service.controllerTests.adminController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.events_service.dto.modelDTO.UserDTO;
import com.practice.events_service.generators.UserGenerator;
import com.practice.events_service.dto.newDTO.NewUserRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminUsersControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserGenerator userGenerator;

    private Long userId;

    @Test
    @BeforeEach
    void postNewUser() throws Exception {
        NewUserRequest newUserRequest = userGenerator.generateNewUserRequest();
        String newUserRequestJson = objectMapper.writeValueAsString(newUserRequest);

        ResultActions userResult = mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserRequestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(newUserRequest.getName()))
                .andExpect(jsonPath("$.email").value(newUserRequest.getEmail()));

        userId = objectMapper.readValue(userResult.andReturn().getResponse().getContentAsString(), UserDTO.class).getId();
    }

    @Test
    void getUsers() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .param("ids", userId.toString())
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/admin/users/{userId}", userId))
                .andExpect(status().isNoContent());
    }
}
