package com.practice.events_service.repository;

import com.practice.events_service.model.Compilation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query(value = """
            select *
            from compilations
            where ?1 is null
               or pinned = ?1
            limit ?3 offset ?2
            """,
            nativeQuery = true)
    List<Compilation> getCompilations(Boolean pinned, int from, int size);
}
