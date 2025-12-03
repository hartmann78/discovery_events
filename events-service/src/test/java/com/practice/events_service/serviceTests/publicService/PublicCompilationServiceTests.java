package com.practice.events_service.serviceTests.publicService;

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
import com.practice.events_service.service.adminService.AdminCategoriesService;
import com.practice.events_service.service.adminService.AdminCompilationsService;
import com.practice.events_service.service.adminService.AdminUsersService;
import com.practice.events_service.service.privateService.PrivateEventsService;
import com.practice.events_service.service.publicService.PublicCompilationsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PublicCompilationServiceTests {
    @Autowired
    private AdminUsersService adminUsersService;
    @Autowired
    private AdminCategoriesService adminCategoriesService;
    @Autowired
    private AdminCompilationsService adminCompilationsService;
    @Autowired
    private PrivateEventsService privateEventsService;
    @Autowired
    private PublicCompilationsService publicCompilationsService;

    @Autowired
    private UserGenerator userGenerator;
    @Autowired
    private CategoryGenerator categoryGenerator;
    @Autowired
    private EventGenerator eventGenerator;
    @Autowired
    private CompilationGenerator compilationGenerator;

    private CompilationDTO compilationDTO;

    @BeforeEach
    void postCompilation() {
        NewUserRequest newUserRequest = userGenerator.generateNewUserRequest();
        UserDTO initiatorDTO = adminUsersService.postNewUser(newUserRequest);

        NewCategoryDTO newCategoryDTO = categoryGenerator.generateNewCategoryDTO();
        CategoryDTO categoryDTO = adminCategoriesService.addNewCategory(newCategoryDTO);

        NewEventDTO newEventDTO = eventGenerator.generateNewEventDTO(categoryDTO.getId());
        EventFullDTO eventFullDTO = privateEventsService.addNewEvent(initiatorDTO.getId(), newEventDTO);

        NewCompilationDTO newCompilationDTO = compilationGenerator.generateNewCompilationDTO(List.of(eventFullDTO.getId()));
        compilationDTO = adminCompilationsService.postNewCompilation(newCompilationDTO);
    }

    @Test
    void getCompilations() {
        List<CompilationDTO> compilationDTOS = publicCompilationsService.getCompilations(compilationDTO.getPinned(), 0, 10);
        assertFalse(compilationDTOS.isEmpty());
    }

    @Test
    void getCompilationById() {
        CompilationDTO findCompilation = publicCompilationsService.getCompilationById(compilationDTO.getId());

        assertEquals(compilationDTO.getId(), findCompilation.getId());
        assertEquals(compilationDTO.getTitle(), findCompilation.getTitle());
        assertEquals(compilationDTO.getPinned(), findCompilation.getPinned());
        assertEquals(1, findCompilation.getEvents().size());
    }
}
