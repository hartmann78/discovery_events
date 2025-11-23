package com.practice.events_service.exception.conflict;

public class EventIsCanceledException extends RuntimeException {
    public EventIsCanceledException(String message) {
        super(message);
    }
}
