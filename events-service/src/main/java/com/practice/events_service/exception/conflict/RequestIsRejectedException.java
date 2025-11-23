package com.practice.events_service.exception.conflict;

public class RequestIsRejectedException extends RuntimeException {
    public RequestIsRejectedException(String message) {
        super(message);
    }
}
