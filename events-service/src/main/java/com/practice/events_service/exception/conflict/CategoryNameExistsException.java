package com.practice.events_service.exception.conflict;

public class CategoryNameExistsException extends RuntimeException {
    public CategoryNameExistsException(String message) {
        super(message);
    }
}
