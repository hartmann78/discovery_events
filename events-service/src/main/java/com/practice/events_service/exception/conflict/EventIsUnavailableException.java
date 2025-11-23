package com.practice.events_service.exception.conflict;

public class EventIsUnavailableException extends RuntimeException {
    public EventIsUnavailableException(String message) {
        super(message);
    }
}
