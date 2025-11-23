package com.practice.events_service.exception.conflict;

public class TooManyConfirmedRequestsException extends RuntimeException {
    public TooManyConfirmedRequestsException(String message) {
        super(message);
    }
}
