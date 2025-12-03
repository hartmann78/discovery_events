package com.practice.events_service.repositoryTests;

import com.practice.events_service.generators.*;
import com.practice.events_service.model.*;
import com.practice.events_service.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class EventRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ParticipationRequestRepository participationRequestRepository;
    @Autowired
    private CommentRepository commentRepository;

    private final UserGenerator userGenerator = new UserGenerator();
    private final CategoryGenerator categoryGenerator = new CategoryGenerator();
    private final EventGenerator eventGenerator = new EventGenerator();
    private final ParticipationRequestGenerator participationRequestGenerator = new ParticipationRequestGenerator();
    private final CommentGenerator commentGenerator = new CommentGenerator();

    private User initiator;
    private Category category;
    private Event event;

    private User requestor;
    private ParticipationRequest request;

    @Test
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
    void findById() {
        Optional<Event> checkEvent = eventRepository.findById(event.getId());
        assertTrue(checkEvent.isPresent());
        assertNotNull(checkEvent.get().getId());
        assertEquals(event, checkEvent.get());
    }

    @Test
    void findAll() {
        List<Event> findAllEvents = eventRepository.findAll();
        assertTrue(findAllEvents.contains(event));
    }

    @Test
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
    void delete() {
        eventRepository.deleteById(event.getId());

        Optional<Event> checkEvent = eventRepository.findById(event.getId());
        assertTrue(checkEvent.isEmpty());
    }

    @Test
    void getEventByInitiatorId() {
        Optional<Event> getEventByInitiatorId = eventRepository.getEventByInitiatorId(initiator.getId(), event.getId());
        assertTrue(getEventByInitiatorId.isPresent());
        assertEquals(event, getEventByInitiatorId.get());
    }

    @Test
    void getInitiatorEvents() {
        List<Event> getInitiatorEvents = eventRepository.getInitiatorEvents(initiator.getId(), 0, 10);
        assertFalse(getInitiatorEvents.isEmpty());
    }

    @Test
    void getPublishedEventById() {
        Optional<Event> getPublishedEventById = eventRepository.getPublishedEventById(event.getId());
        assertTrue(getPublishedEventById.isPresent());
        assertEquals(event, getPublishedEventById.get());
    }

    @Test
    void incrementEventViews() {
        assertDoesNotThrow(() -> eventRepository.incrementEventViews(event.getId()));

        Optional<Event> checkEvent = eventRepository.findById(event.getId());
        assertTrue(checkEvent.isPresent());
        assertEquals(1, checkEvent.get().getViews());
    }

    @Test
    void getAvailableRequestsCount() {
        Long getAvailableRequestsCount = eventRepository.getAvailableRequestsCount(event.getId());
        assertEquals(event.getParticipantLimit() - event.getConfirmedRequests(), getAvailableRequestsCount);
    }

    @Test
    void eventContainsConfirmedRequests() {
        participationRequestRepository.updateRequests(new ArrayList<>(List.of(request.getId())), ParticipationRequest.Status.CONFIRMED.toString());

        Boolean eventContainsConfirmedRequests = eventRepository.eventContainsConfirmedRequests(event.getId());
        assertTrue(eventContainsConfirmedRequests);
    }

    @Test
    void updateConfirmedRequestsCount() {
        participationRequestRepository.updateRequests(new ArrayList<>(List.of(request.getId())), ParticipationRequest.Status.CONFIRMED.toString());
        eventRepository.updateConfirmedRequestsCount(event.getId());

        Optional<Event> checkEvent = eventRepository.findById(event.getId());
        assertTrue(checkEvent.isPresent());
        assertEquals(1, checkEvent.get().getConfirmedRequests());
    }

    @Test
    void getEventComments() {
        Comment comment1 = commentGenerator.generateComment(requestor, event);
        commentRepository.save(comment1);

        Comment comment2 = commentGenerator.generateComment(requestor, event);
        commentRepository.save(comment2);

        List<Comment> comments = eventRepository.getEventComments(event.getId());
        assertEquals(2, comments.size());
    }
}
