package com.practice.events_service.generators;

import com.practice.events_service.dto.newDTO.NewEventDTO;
import com.practice.events_service.dto.updateRequest.UpdateEventAdminRequest;
import com.practice.events_service.dto.updateRequest.UpdateEventCommentsState;
import com.practice.events_service.dto.updateRequest.UpdateEventUserRequest;
import com.practice.events_service.model.Category;
import com.practice.events_service.model.Event;
import com.practice.events_service.dto.other.Location;
import com.practice.events_service.model.User;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class EventGenerator {
    private final Random random = new Random();

    public Event generateEvent(User initiator, Category category) {
        return Event.builder()
                .title(generateTitle())
                .description(generateDescription())
                .annotation(generateAnnotation())
                .eventDate(generateEventDate())
                .initiator(initiator)
                .category(category)
                .lat(generateLat())
                .lon(generateLon())
                .participantLimit(generateParticipantLimit())
                .paid(true)
                .requestModeration(true)
                .confirmedRequests(0L)
                .views(0L)
                .createdOn(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .publishedOn(null)
                .state(Event.State.PUBLISHED)
                .build();
    }

    public NewEventDTO generateNewEventDTO(Long categoryId) {
        return NewEventDTO.builder()
                .title(generateTitle())
                .description(generateDescription())
                .annotation(generateAnnotation())
                .eventDate(generateEventDate())
                .category(categoryId)
                .location(generateLocation())
                .participantLimit(generateParticipantLimit())
                .paid(true)
                .requestModeration(true)
                .build();
    }

    public UpdateEventUserRequest generateUpdateEventUserRequest(Long categoryId) {
        return UpdateEventUserRequest.builder()
                .title(generateTitle())
                .description(generateDescription())
                .annotation(generateAnnotation())
                .eventDate(generateEventDate())
                .category(categoryId)
                .location(generateLocation())
                .participantLimit(generateParticipantLimit())
                .paid(true)
                .requestModeration(true)
                .build();
    }

    public UpdateEventAdminRequest generateUpdateEventAdminRequest(Long categoryId) {
        return UpdateEventAdminRequest.builder()
                .title(generateTitle())
                .description(generateDescription())
                .annotation(generateAnnotation())
                .eventDate(generateEventDate())
                .category(categoryId)
                .location(generateLocation())
                .participantLimit(generateParticipantLimit())
                .paid(true)
                .requestModeration(true)
                .build();
    }

    public UpdateEventCommentsState generateUpdateEventCommentsState(Boolean commentsAvailable, Boolean showComments) {
        return UpdateEventCommentsState.builder()
                .commentsAvailable(commentsAvailable)
                .showComments(showComments)
                .build();
    }

    private String generateTitle() {
        return RandomString.make(10);
    }

    private String generateDescription() {
        return RandomString.make(50);
    }

    private String generateAnnotation() {
        return RandomString.make(20);
    }

    private String generateEventDate() {
        return LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private Float generateLat() {
        return random.nextFloat(100);
    }

    private Float generateLon() {
        return random.nextFloat(100);
    }

    private Location generateLocation() {
        return new Location(random.nextFloat(100), random.nextFloat(100));
    }

    private Integer generateParticipantLimit() {
        return random.nextInt(1000);
    }
}
