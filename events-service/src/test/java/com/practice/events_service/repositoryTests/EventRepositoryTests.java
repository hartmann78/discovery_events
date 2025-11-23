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

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EventRepositoryTests {
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
        Optional<Event> checkEvent = eventRepository.findById(event.getId());
        assertTrue(checkEvent.isPresent());
        assertNotNull(checkEvent.get().getId());
        assertEquals(event, checkEvent.get());
    }

    @Test
    @Order(2)
    void getEventByInitiatorId() {
        Optional<Event> getEventByInitiatorId = eventRepository.getEventByInitiatorId(initiator.getId(), event.getId());
        assertTrue(getEventByInitiatorId.isPresent());
        assertEquals(event, getEventByInitiatorId.get());
    }

    @Test
    @Order(3)
    void getInitiatorEvents() {
        List<Event> getInitiatorEvents = eventRepository.getInitiatorEvents(initiator.getId(), 0, 10);
        assertFalse(getInitiatorEvents.isEmpty());
    }

    @Test
    @Order(4)
    void getPublishedEventById() {
        Optional<Event> getPublishedEventById = eventRepository.getPublishedEventById(event.getId());
        assertTrue(getPublishedEventById.isPresent());
        assertEquals(event, getPublishedEventById.get());
    }

    @Test
    @Order(5)
    void incrementEventViews() {
        assertDoesNotThrow(() -> eventRepository.incrementEventViews(event.getId()));

        Optional<Event> checkEvent = eventRepository.findById(event.getId());
        assertTrue(checkEvent.isPresent());
        assertEquals(1, checkEvent.get().getViews());
    }

    @Test
    @Order(6)
    void getAvailableRequestsCount() {
        Long getAvailableRequestsCount = eventRepository.getAvailableRequestsCount(event.getId());
        assertEquals(event.getParticipantLimit() - event.getConfirmedRequests(), getAvailableRequestsCount);
    }

    @Test
    @Order(7)
    void eventContainsConfirmedRequests() {
        participationRequestRepository.updateRequests(new ArrayList<>(List.of(request.getId())), ParticipationRequest.Status.CONFIRMED.toString());

        Boolean eventContainsConfirmedRequests = eventRepository.eventContainsConfirmedRequests(event.getId());
        assertTrue(eventContainsConfirmedRequests);
    }

    @Test
    @Order(8)
    void updateConfirmedRequestsCount() {
        participationRequestRepository.updateRequests(new ArrayList<>(List.of(request.getId())), ParticipationRequest.Status.CONFIRMED.toString());
        eventRepository.updateConfirmedRequestsCount(event.getId());

        Optional<Event> checkEvent = eventRepository.findById(event.getId());
        assertTrue(checkEvent.isPresent());
        assertEquals(1, checkEvent.get().getConfirmedRequests());
    }

    @Test
    @Order(9)
    void findAll() {
        List<Event> findAllEvents = eventRepository.findAll();
        assertTrue(findAllEvents.contains(event));
    }

    @Test
    @Order(10)
    void update() {
        Event updateEvent = eventGenerator.generateEvent(event.getInitiator(), event.getCategory());
        event.setTitle(updateEvent.getTitle());
        event.setDescription(updateEvent.getDescription());
        event.setAnnotation(updateEvent.getAnnotation());
        event.setEventDate(updateEvent.getEventDate());
        event.setInitiator(updateEvent.getInitiator());
        event.setCategory(updateEvent.getCategory());
        event.setCompilation(updateEvent.getCompilation());
        event.setParticipationRequests(updateEvent.getParticipationRequests());
        event.setLat(updateEvent.getLat());
        event.setLon(updateEvent.getLon());
        event.setParticipantLimit(updateEvent.getParticipantLimit());
        event.setPaid(updateEvent.getPaid());
        event.setRequestModeration(updateEvent.getRequestModeration());
        event.setConfirmedRequests(updateEvent.getConfirmedRequests());
        event.setViews(updateEvent.getViews());
        event.setCreatedOn(updateEvent.getCreatedOn());
        event.setPublishedOn(updateEvent.getPublishedOn());
        event.setState(updateEvent.getState());

        eventRepository.save(event);

        Optional<Event> checkUpdatedEvent = eventRepository.findById(event.getId());
        assertTrue(checkUpdatedEvent.isPresent());
        assertEquals(event, checkUpdatedEvent.get());
    }

    @Test
    @Order(11)
    void delete() {
        eventRepository.deleteById(event.getId());

        Optional<Event> checkEvent = eventRepository.findById(event.getId());
        assertTrue(checkEvent.isEmpty());
    }
}
