package com.practice.events_service.exception.conflict;

public class EventHasNotStartedException extends RuntimeException {
    public EventHasNotStartedException(String message) {
        super(message);
    }
}
