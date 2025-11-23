package com.practice.events_service.errorHandler;

import com.practice.events_service.exception.conflict.*;
import com.practice.events_service.exception.not_found.*;
import com.practice.events_service.exception.other.BadRequestException;
import com.practice.events_service.exception.other.ForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.practice.events_service.controller")
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError badRequest(final BadRequestException exception) {
        return new ApiError("Неверно составлен запрос.", exception.getMessage(), 400, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError forbidden(final ForbiddenException exception) {
        return new ApiError("Доступ запрещён.", exception.getMessage(), 403, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError userNotFound(final UserNotFoundException exception) {
        return new ApiError("Требуемый объект не найден.", exception.getMessage(), 404, exception.getStackTrace());

    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError categoryNotFound(final CategoryNotFoundException exception) {
        return new ApiError("Требуемый объект не найден.", exception.getMessage(), 404, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError eventNotFound(final EventNotFoundException exception) {
        return new ApiError("Требуемый объект не найден.", exception.getMessage(), 404, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError ParticipationRequestNotFound(final ParticipationRequestNotFoundException exception) {
        return new ApiError("Требуемый объект не найден.", exception.getMessage(), 404, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError compilationNotFound(final CompilationNotFoundException exception) {
        return new ApiError("Требуемый объект не найден.", exception.getMessage(), 404, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError commentNotFound(final CommentNotFoundException exception) {
        return new ApiError("Требуемый объект не найден.", exception.getMessage(), 404, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError emailExists(final EmailExistsException exception) {
        return new ApiError("Нарушение целостности данных.", exception.getMessage(), 409, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError categoryAttachedToEvents(final CategoryAttachedToEventsException exception) {
        return new ApiError("Нарушение целостности данных.", exception.getMessage(), 409, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError categoryNameExists(final CategoryNameExistsException exception) {
        return new ApiError("Нарушение целостности данных.", exception.getMessage(), 409, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError eventIsCanceled(final EventIsCanceledException exception) {
        return new ApiError("Нарушение целостности данных.", exception.getMessage(), 409, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError eventIsPublished(final EventIsPublishedException exception) {
        return new ApiError("Нарушение целостности данных.", exception.getMessage(), 409, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError eventIsUnavailable(final EventIsUnavailableException exception) {
        return new ApiError("Нарушение целостности данных.", exception.getMessage(), 409, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError eventIsUnpublished(final EventNotPublishedException exception) {
        return new ApiError("Нарушение целостности данных.", exception.getMessage(), 409, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError requestExists(final RequestExistsException exception) {
        return new ApiError("Нарушение целостности данных.", exception.getMessage(), 409, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError requestIsFromInitiator(final RequestIsFromInitiatorException exception) {
        return new ApiError("Нарушение целостности данных.", exception.getMessage(), 409, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError requestIsConfirmedException(final RequestIsConfirmedException exception) {
        return new ApiError("Нарушение целостности данных.", exception.getMessage(), 409, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError requestIsRejectedException(final RequestIsRejectedException exception) {
        return new ApiError("Нарушение целостности данных.", exception.getMessage(), 409, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError requestIsCanceledException(final RequestIsCanceledException exception) {
        return new ApiError("Нарушение целостности данных.", exception.getMessage(), 409, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError tooManyRequests(final TooManyConfirmedRequestsException exception) {
        return new ApiError("Нарушение целостности данных.", exception.getMessage(), 409, exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError eventHasNotStarted(final EventHasNotStartedException exception) {
        return new ApiError("Нарушение целостности данных.", exception.getMessage(), 409, exception.getStackTrace());
    }
}
