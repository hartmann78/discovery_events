package com.practice.events_service.exception.conflict;

public class RequestExistsException extends RuntimeException {
    public RequestExistsException(String message) {
        super(message);
    }
}
