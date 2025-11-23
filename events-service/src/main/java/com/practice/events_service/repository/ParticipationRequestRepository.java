package com.practice.events_service.repository;

import com.practice.events_service.model.ParticipationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    @Query(value = """
            select *
            from participation_requests
            where requester_id = ?1
            """,
            nativeQuery = true)
    List<ParticipationRequest> getRequesterRequests(Long userId);

    @Query(value = """
            select pr.id, pr.event_id, pr.requester_id, pr.created, pr.status
            from participation_requests pr
                     inner join events e on e.id = pr.event_id
            where e.initiator_id = ?1
              and pr.event_id = ?2
            """,
            nativeQuery = true)
    List<ParticipationRequest> getEventInitiatorRequests(Long userId, Long eventId);

    @Query(value = """
            select exists(select *
                          from participation_requests
                          where requester_id = ?1
                            and event_id = ?2)
            """,
            nativeQuery = true)
    Boolean checkRequestExists(Long userId, Long eventId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = """
            update participation_requests
            set status = ?2
            where id in ?1
            """,
            nativeQuery = true)
    void updateRequests(List<Long> requestIds, String status);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = """
            update participation_requests
            set status = 'CANCELED'
            where requester_id = ?1
              and id = ?2
            """,
            nativeQuery = true)
    void cancelRequest(Long userId, Long requestId);

    @Query(value = """
            select *
            from participation_requests
            where event_id = ?1
              and (status = 'CONFIRMED'
                or status = 'REJECTED')
            """,
            nativeQuery = true)
    List<ParticipationRequest> getAllConfirmedAndRejectedRequests(Long eventId);

    @Query(value = """
            select exists(select *
                          from participation_requests
                          where requester_id = ?1
                            and event_id = ?2
                            and status = 'CONFIRMED')
            """,
            nativeQuery = true)
    Boolean checkRequestorConfirmed(Long userId, Long eventId);
}
