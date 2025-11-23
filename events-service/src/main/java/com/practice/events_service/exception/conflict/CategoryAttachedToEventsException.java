package com.practice.events_service.exception.conflict;

public class CategoryAttachedToEventsException extends RuntimeException {
    public CategoryAttachedToEventsException(String message) {
        super(message);
    }
}
