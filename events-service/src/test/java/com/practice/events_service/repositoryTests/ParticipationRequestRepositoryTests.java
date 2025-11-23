package com.practice.events_service.repositoryTests;

import com.practice.events_service.generators.CategoryGenerator;
import com.practice.events_service.generators.EventGenerator;
import com.practice.events_service.generators.ParticipationRequestGenerator;
import com.practice.events_service.generators.UserGenerator;
import com.practice.events_service.model.Category;
import com.practice.events_service.model.Event;
import com.practice.events_service.model.ParticipationRequest;
import com.practice.events_service.model.User;
import com.practice.events_service.repository.CategoryRepository;
import com.practice.events_service.repository.EventRepository;
import com.practice.events_service.repository.ParticipationRequestRepository;
import com.practice.events_service.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ParticipationRequestRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ParticipationRequestRepository participationRequestRepository;

    private final UserGenerator userGenerator = new UserGenerator();
    private final CategoryGenerator categoryGenerator = new CategoryGenerator();
    private final EventGenerator eventGenerator = new EventGenerator();
    private final ParticipationRequestGenerator participationRequestGenerator = new ParticipationRequestGenerator();

    private User initiator;
    private Category category;
    private Event event;

    private User requestor;
    private ParticipationRequest request;

    @BeforeEach
    void save() {
        initiator = userGenerator.generateUser();
        userRepository.save(initiator);

        category = categoryGenerator.generateCategory();
        categoryRepository.save(category);

        event = eventGenerator.generateEvent(initiator, category);
        eventRepository.save(event);

        requestor = userGenerator.generateUser();
        userRepository.save(requestor);

        request = participationRequestGenerator.generateParticipationRequest(requestor, event);
        participationRequestRepository.save(request);
    }

    @Test
    @Order(1)
    void findById() {
        Optional<ParticipationRequest> checkRequest = participationRequestRepository.findById(request.getId());
        assertTrue(checkRequest.isPresent());
        assertNotNull(checkRequest.get().getId());
        assertEquals(request, checkRequest.get());
    }

    @Test
    @Order(2)
    void getRequesterRequests() {
        List<ParticipationRequest> getRequesterRequests = participationRequestRepository.getRequesterRequests(requestor.getId());
        assertFalse(getRequesterRequests.isEmpty());
    }

    @Test
    @Order(3)
    void getEventInitiatorRequests() {
        List<ParticipationRequest> getEventInitiatorRequests = participationRequestRepository.getEventInitiatorRequests(initiator.getId(), event.getId());
        assertFalse(getEventInitiatorRequests.isEmpty());
    }

    @Test
    @Order(4)
    void checkRequestExists() {
        Boolean checkRequestExists = participationRequestRepository.checkRequestExists(requestor.getId(), event.getId());
        assertTrue(checkRequestExists);
    }

    @Test
    @Order(5)
    void updateRequests() {
        participationRequestRepository.updateRequests(new ArrayList<>(List.of(request.getId())), ParticipationRequest.Status.CONFIRMED.toString());

        Optional<ParticipationRequest> checkRequest = participationRequestRepository.findById(request.getId());
        assertTrue(checkRequest.isPresent());
        assertEquals(ParticipationRequest.Status.CONFIRMED, checkRequest.get().getStatus());
    }

    @Test
    @Order(6)
    void getAllConfirmedAndRejectedRequests() {
        participationRequestRepository.updateRequests(new ArrayList<>(List.of(request.getId())), ParticipationRequest.Status.CONFIRMED.toString());

        List<ParticipationRequest> getAllConfirmedAndRejectedRequests = participationRequestRepository.getAllConfirmedAndRejectedRequests(event.getId());
        assertFalse(getAllConfirmedAndRejectedRequests.isEmpty());
        System.out.println(getAllConfirmedAndRejectedRequests);
    }

    @Test
    @Order(7)
    void cancelRequest() {
        Event event2 = eventGenerator.generateEvent(initiator, category);
        eventRepository.save(event2);

        ParticipationRequest request2 = participationRequestGenerator.generateParticipationRequest(requestor, event2);
        participationRequestRepository.save(request2);

        participationRequestRepository.cancelRequest(requestor.getId(), request2.getId());

        Optional<ParticipationRequest> checkRequest = participationRequestRepository.findById(request2.getId());
        assertTrue(checkRequest.isPresent());
        assertEquals(ParticipationRequest.Status.CANCELED, checkRequest.get().getStatus());
    }

    @Test
    @Order(8)
    void findAll() {
        List<ParticipationRequest> findAllRequests = participationRequestRepository.findAll();
        assertTrue(findAllRequests.contains(request));
    }

    @Test
    @Order(9)
    void update() {
        ParticipationRequest updateRequest = participationRequestGenerator.generateParticipationRequest(requestor, event);

        request.setCreated(updateRequest.getCreated());
        eventRepository.save(event);

        Optional<ParticipationRequest> checkUpdatedRequest = participationRequestRepository.findById(request.getId());
        assertTrue(checkUpdatedRequest.isPresent());
        assertEquals(request, checkUpdatedRequest.get());
    }

    @Test
    @Order(10)
    void delete() {
        participationRequestRepository.deleteById(request.getId());

        Optional<ParticipationRequest> checkRequest = participationRequestRepository.findById(request.getId());
        assertTrue(checkRequest.isEmpty());
    }
}
