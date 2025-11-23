package com.practice.events_service.repository;

import com.practice.events_service.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = """
            select exists(select name
                          from categories
                          where name = ?1)
            """,
            nativeQuery = true)
    Boolean checkCategoryNameExists(String name);

    @Query(value = """
            select exists(select *
                          from events
                          where category_id = ?1)
            """,
            nativeQuery = true)
    Boolean checkCategoryIsAttachedToEvents(Long catId);
}
