package com.practice.events_service.repository;

import com.practice.events_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = """
            select exists(select u.email
                          from users as u
                          where u.email = ?1)
            """,
            nativeQuery = true)
    Boolean checkUserEmailExists(String email);

    @Query(value = """
            select *
            from users
            where cast(?1 as anyarray) is null
               or id in (?1)
            limit ?3 offset ?2
            """,
            nativeQuery = true)
    List<User> getUsersByIds(Long[] ids, int from, int size);
}
