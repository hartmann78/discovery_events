package com.practice.events_service.exception.conflict;

public class RequestIsFromInitiatorException extends RuntimeException {
    public RequestIsFromInitiatorException(String message) {
        super(message);
    }
}
