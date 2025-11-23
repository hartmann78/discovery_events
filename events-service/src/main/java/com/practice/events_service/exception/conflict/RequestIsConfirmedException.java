package com.practice.events_service.exception.conflict;

public class RequestIsConfirmedException extends RuntimeException {
    public RequestIsConfirmedException(String message) {
        super(message);
    }
}
