package com.practice.events_service.repository;

import com.practice.events_service.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = """
            select *
            from events
            where initiator_id = ?1
              and id = ?2
            """,
            nativeQuery = true)
    Optional<Event> getEventByInitiatorId(Long userId, Long eventId);

    @Query(value = """
            select *
            from events
            where initiator_id = ?1
            limit ?3 offset ?2
            """,
            nativeQuery = true)
    List<Event> getInitiatorEvents(Long userId, int from, int size);

    @Query(value = """
            select *
            from events
            where state = 'PUBLISHED'
              and id = ?1
            """,
            nativeQuery = true)
    Optional<Event> getPublishedEventById(Long eventId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = """
            update events
            set views=views + 1
            where id = ?1
            """,
            nativeQuery = true)
    void incrementEventViews(Long eventId);

    @Query(value = """
            select participant_limit - confirmed_requests
            from events
            where id = ?1
            """,
            nativeQuery = true)
    Long getAvailableRequestsCount(Long eventId);

    @Query(value = """
            select exists(select *
                          from participation_requests pr
                                   inner join events e on e.id = pr.event_id
                          where pr.status = 'CONFIRMED'
                            and e.participant_limit > 0
                            and pr.event_id = ?1)
            """,
            nativeQuery = true)
    Boolean eventContainsConfirmedRequests(Long eventId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = """
            update events
            set confirmed_requests =(select count(*)
                                     from participation_requests
                                     where event_id = ?1
                                       and status = 'CONFIRMED')
            where id = ?1
            """,
            nativeQuery = true)
    void updateConfirmedRequestsCount(Long eventId);

    @Query(value = """
            select *
            from events
            where (cast(?1 as anyarray) is null or initiator_id in (?1))
              and (cast(?2 as anyarray) is null or state in (?2))
              and (cast(?3 as anyarray) is null or category_id in (?3))
              and (((?4, ?5) is null) or ((?4, ?5) is not null and event_date between ?4 and ?5))
            limit ?7 offset ?6
            """,
            nativeQuery = true)
    List<Event> adminGetEvents(Long[] users, String[] states, Long[] categories,
                               String rangeStart, String rangeEnd, int from, int size);

    @Query(value = """
            select *
            from events
            where (?1 is null
                or (lower(description) like lower('%' || ?1 || '%')
                    or (lower(annotation) like lower('%' || ?1 || '%'))))
              and (cast(?2 as anyarray) is null or category_id in (?2))
              and (?3 is null or paid = ?3)
              and (((?4, ?5) is null and event_date > to_char(now(), 'YYYY-MM-DD HH24:MI:SS'))
                or ((?4, ?5) is not null and event_date between ?4 and ?5))
              and (?6 = false
                or (participant_limit - confirmed_requests > 0 and participant_limit > 0 or participant_limit = 0) = ?6)
              and state = 'PUBLISHED'
            order by case when ?7 = 'event_date' then event_date end desc,
                     case when ?7 = 'views' then views end desc
            limit ?9 offset ?8
            """,
            nativeQuery = true)
    List<Event> publicGetPublishedEvents(String text, Long[] categories, Boolean paid, String rangeStart, String rangeEnd,
                                         Boolean onlyAvailable, String sort, int from, int size);
}
