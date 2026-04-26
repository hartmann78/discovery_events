package com.practice.events_service.errorHandler;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
@JsonPropertyOrder({"reason", "message", "status", "timestamp", "errors"})
public class ApiError {
    private final String reason;
    private final String message;
    private final String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    private final List<String> errors;

    public ApiError(String reason, String message, int code, StackTraceElement[] errors) {
        this.reason = reason;
        this.message = message;
        this.status = HttpStatus.valueOf(code).toString();
        this.timestamp = LocalDateTime.now();
        this.errors = Arrays.stream(errors).map(StackTraceElement::toString).toList();
    }
}
