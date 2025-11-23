package com.practice.events_service.exception.not_found;

public class ParticipationRequestNotFoundException extends RuntimeException {
    public ParticipationRequestNotFoundException(String message) {
        super(message);
    }
}
