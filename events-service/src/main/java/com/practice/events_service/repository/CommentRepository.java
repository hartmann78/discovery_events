package com.practice.events_service.repository;

import com.practice.events_service.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = """
            select *
            from comments
            where author_id = ?1
            limit ?3 offset ?2
            """,
            nativeQuery = true)
    List<Comment> getAllUserComments(Long userId, int from, int size);

    @Query(value = """
            select *
            from comments
            where event_id = ?1
            limit ?3 offset ?2
            """,
            nativeQuery = true)
    List<Comment> getAllEventComments(Long eventId, int from, int size);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = """
            delete
            from comments
            where author_id = ?1
            """,
            nativeQuery = true)
    void deleteAllUserComments(Long userId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = """
            delete
            from comments
            where event_id = ?1
            """,
            nativeQuery = true)
    void deleteAllEventComments(Long eventId);
}
