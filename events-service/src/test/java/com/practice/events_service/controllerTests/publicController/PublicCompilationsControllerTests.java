package com.practice.events_service.controllerTests.publicController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.events_service.dto.modelDTO.CategoryDTO;
import com.practice.events_service.dto.modelDTO.CompilationDTO;
import com.practice.events_service.dto.modelDTO.EventFullDTO;
import com.practice.events_service.dto.modelDTO.UserDTO;
import com.practice.events_service.dto.newDTO.NewCategoryDTO;
import com.practice.events_service.dto.newDTO.NewCompilationDTO;
import com.practice.events_service.dto.newDTO.NewEventDTO;
import com.practice.events_service.dto.newDTO.NewUserRequest;
import com.practice.events_service.generators.CategoryGenerator;
import com.practice.events_service.generators.CompilationGenerator;
import com.practice.events_service.generators.EventGenerator;
import com.practice.events_service.generators.UserGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PublicCompilationsControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserGenerator userGenerator;
    @Autowired
    private CategoryGenerator categoryGenerator;
    @Autowired
    private EventGenerator eventGenerator;
    @Autowired
    private CompilationGenerator compilationGenerator;

    private static NewUserRequest newUserRequest;
    private static NewCategoryDTO newCategoryDTO;
    private static NewEventDTO newEventDTO;
    private static NewCompilationDTO newCompilationDTO;

    private static Long initiatorId;
    private static Long categoryId;
    private static Long eventId;
    private static Long compilationId;

    @Test
    @Order(1)
    void getCompilations() throws Exception {
        // Create user
        newUserRequest = userGenerator.generateNewUserRequest();
        String newUserRequestJson = objectMapper.writeValueAsString(newUserRequest);

        ResultActions userResult = mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserRequestJson))
                .andExpect(status().isCreated());

        initiatorId = objectMapper.readValue(userResult.andReturn().getResponse().getContentAsString(), UserDTO.class).getId();

        // Create category
        newCategoryDTO = categoryGenerator.generateNewCategoryDTO();
        String newCategoryDTOJson = objectMapper.writeValueAsString(newCategoryDTO);

        ResultActions categoryResult = mockMvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCategoryDTOJson))
                .andExpect(status().isCreated());

        categoryId = objectMapper.readValue(categoryResult.andReturn().getResponse().getContentAsString(), CategoryDTO.class).getId();

        // Create event
        newEventDTO = eventGenerator.generateNewEventDTO(categoryId);
        String newEventDTOJson = objectMapper.writeValueAsString(newEventDTO);

        ResultActions eventResult = mockMvc.perform(post("/users/{userId}/events", initiatorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newEventDTOJson))
                .andExpect(status().isCreated());

        eventId = objectMapper.readValue(eventResult.andReturn().getResponse().getContentAsString(), EventFullDTO.class).getId();

        // Create compilation
        newCompilationDTO = compilationGenerator.generateNewCompilationDTO(List.of(eventId));
        String newCompilationDTOJson = objectMapper.writeValueAsString(newCompilationDTO);

        ResultActions compilationResult = mockMvc.perform(post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCompilationDTOJson))
                .andExpect(status().isCreated());

        compilationId = objectMapper.readValue(compilationResult.andReturn().getResponse().getContentAsString(), CompilationDTO.class).getId();

        mockMvc.perform(get("/compilations")
                        .param("pinned", newCompilationDTO.getPinned().toString())
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @Order(2)
    void getCompilationsById() throws Exception {
        mockMvc.perform(get("/compilations/{compId}", compilationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(compilationId))
                .andExpect(jsonPath("$.title").value(newCompilationDTO.getTitle()))
                .andExpect(jsonPath("$.pinned").value(newCompilationDTO.getPinned()))
                .andExpect(jsonPath("$.events.[0].id").value(eventId))
                .andExpect(jsonPath("$.events.[0].title").value(newEventDTO.getTitle()))
                .andExpect(jsonPath("$.events.[0].annotation").value(newEventDTO.getAnnotation()))
                .andExpect(jsonPath("$.events.[0].eventDate").value(newEventDTO.getEventDate()))
                .andExpect(jsonPath("$.events.[0].initiator.id").value(initiatorId))
                .andExpect(jsonPath("$.events.[0].initiator.name").value(newUserRequest.getName()))
                .andExpect(jsonPath("$.events.[0].category.id").value(categoryId))
                .andExpect(jsonPath("$.events.[0].category.name").value(newCategoryDTO.getName()))
                .andExpect(jsonPath("$.events.[0].paid").value(newEventDTO.getPaid()))
                .andExpect(jsonPath("$.events.[0].confirmedRequests").exists())
                .andExpect(jsonPath("$.events.[0].views").exists());
    }
}
