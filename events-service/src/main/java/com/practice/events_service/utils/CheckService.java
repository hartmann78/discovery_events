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

    // time

    public void startAndEndTimeCheck(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart == null && rangeEnd == null) {
            return;
        }

        if (rangeStart == null) {
            throw new BadRequestException("Не указана начальная дата и время!");
        }

        if (rangeEnd == null) {
            throw new BadRequestException("Не указана конечная дата и время!");
        }

        if (rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("Конечная дата и время не должна быть раньше начала!");
        }

        if (rangeStart.equals(rangeEnd)) {
            throw new BadRequestException("Начальная и конечная дата и время не должны быть равны!");
        }
    }

    public void eventDateAfterNowPlusTwoHoursCheck(LocalDateTime eventDate) {
        if (eventDate == null) {
            return;
        }

        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Дата и время на которые намечено событие не может быть раньше, " +
                    "чем через 2 часа от текущего момента!");
        }
    }

    public void eventDateAfterPublicationPlusOneHourCheck(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
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
