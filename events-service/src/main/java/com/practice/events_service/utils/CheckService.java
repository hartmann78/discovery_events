package com.practice.events_service.utils;

import com.practice.events_service.dto.eventRequestStatusUpdate.EventRequestStatusUpdateRequest;
import com.practice.events_service.exception.conflict.RequestIsCanceledException;
import com.practice.events_service.exception.conflict.RequestIsConfirmedException;
import com.practice.events_service.exception.conflict.RequestIsRejectedException;
import com.practice.events_service.exception.not_found.*;
import com.practice.events_service.exception.other.BadRequestException;
import com.practice.events_service.model.*;
import com.practice.events_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CheckService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final CommentRepository commentRepository;

    // find

    public User findUser(Long userId) {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isEmpty()) {
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден в базе данных!");
        }

        return findUser.get();
    }

    public Category findCategory(Long catId) {
        Optional<Category> findCategory = categoryRepository.findById(catId);
        if (findCategory.isEmpty()) {
            throw new CategoryNotFoundException("Категория с id=" + catId + " не найдена в базе данных!");
        }

        return findCategory.get();
    }

    public Event findEvent(Long eventId) {
        Optional<Event> findEvent = eventRepository.findById(eventId);
        if (findEvent.isEmpty()) {
            throw new EventNotFoundException("Событие с id=" + eventId + " не найдено в базе данных!");
        }

        return findEvent.get();
    }

    public ParticipationRequest findParticipationRequest(Long requestId) {
        Optional<ParticipationRequest> findParticipationRequest = participationRequestRepository.findById(requestId);
        if (findParticipationRequest.isEmpty()) {
            throw new ParticipationRequestNotFoundException("Запрос на участие с id=" + requestId + " не найден в базе данных!");
        }

        return findParticipationRequest.get();
    }

    public Compilation findCompilation(Long compId) {
        Optional<Compilation> findCompilation = compilationRepository.findById(compId);
        if (findCompilation.isEmpty()) {
            throw new CompilationNotFoundException("Подборка с id=" + compId + " не найдена в базе данных!");
        }

        return findCompilation.get();
    }

    public Comment findComment(Long commentId) {
        Optional<Comment> findComment = commentRepository.findById(commentId);
        if (findComment.isEmpty()) {
            throw new CommentNotFoundException("Комментарий с id=" + commentId + " не найден в базе данных!");
        }

        return findComment.get();
    }

    // check exists

    public void userExistsCheck(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден в базе данных!");
        }
    }

    public void categoryExistsCheck(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new CategoryNotFoundException("Категория с id=" + catId + " не найдена в базе данных!");
        }
    }

    public void eventExistsCheck(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Событие с id=" + eventId + " не найдено в базе данных!");
        }
    }

    public void participationRequestExistsCheck(Long requestId) {
        if (!participationRequestRepository.existsById(requestId)) {
            throw new ParticipationRequestNotFoundException("Запрос на участие с id=" + requestId + " не найден в базе данных!");
        }
    }

    public void compilationExistsCheck(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new CompilationNotFoundException("Подборка с id=" + compId + " не найдена в базе данных!");
        }
    }

    public void commentExistsCheck(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException("Комментарий с id=" + commentId + " не найден в базе данных!");
        }
    }

    // time

    public void startAndEndTimeCheck(String rangeStart, String rangeEnd) {
        if (rangeStart == null && rangeEnd == null) {
            return;
        }

        if (rangeStart == null) {
            throw new BadRequestException("Не указана начальная дата и время!");
        }

        if (rangeEnd == null) {
            throw new BadRequestException("Не указана конечная дата и время!");
        }

        LocalDateTime start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (end.isBefore(start)) {
            throw new BadRequestException("Конечная дата и время не должна быть раньше начала!");
        }

        if (start.equals(end)) {
            throw new BadRequestException("Начальная и конечная дата и время не должны быть равны!");
        }
    }

    public void eventDateAfterNowPlusTwoHoursCheck(String eventDate) {
        if (eventDate == null) {
            return;
        }

        LocalDateTime eventDateStart = LocalDateTime.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (eventDateStart.isBefore(LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.SECONDS))) {
            throw new BadRequestException("Дата и время на которые намечено событие не может быть раньше, " +
                    "чем через 2 часа от текущего момента!");
        }
    }

    public void eventDateAfterPublicationPlusOneHourCheck(String eventDate) {
        LocalDateTime time = LocalDateTime.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (time.isBefore(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS))) {
            throw new BadRequestException("Дата начала изменяемого события должна быть " +
                    "не ранее чем за 1 час от даты публикации!");
        }
    }

    // other

    public void fromAndSizeCheck(int from, int size) {
        if (from < 0) {
            throw new BadRequestException("Значение from не должно быть меньше 0");
        }

        if (size < 1) {
            throw new BadRequestException("Значение size не должно быть меньше 1");
        }
    }

    public void checkRequestStatusForPatch(ParticipationRequest.Status requestStatus, EventRequestStatusUpdateRequest.Status updateStatus) {
        if (requestStatus == ParticipationRequest.Status.CONFIRMED && updateStatus == EventRequestStatusUpdateRequest.Status.REJECTED) {
            throw new RequestIsConfirmedException("Нельзя отклонить подтверждённый запрос!");
        } else if (requestStatus == ParticipationRequest.Status.REJECTED && updateStatus == EventRequestStatusUpdateRequest.Status.CONFIRMED) {
            throw new RequestIsRejectedException("Нельзя подтвердить отклонённый запрос!");
        } else if (requestStatus == ParticipationRequest.Status.CANCELED) {
            throw new RequestIsCanceledException("Нельзя подтвердить или отклонить отменённый запрос!");
        }
    }
}
