package com.practice.events_service.exception.conflict;

public class EventIsPublishedException extends RuntimeException {
    public EventIsPublishedException(String message) {
        super(message);
    }
}
