package com.practice.events_service.exception.conflict;

public class RequestIsCanceledException extends RuntimeException {
    public RequestIsCanceledException(String message) {
        super(message);
    }
}
