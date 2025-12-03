package com.practice.events_service.repositoryTests;

import com.practice.events_service.generators.CompilationGenerator;
import com.practice.events_service.model.Compilation;
import com.practice.events_service.repository.CompilationRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class CompilationRepositoryTests {
    @Autowired
    private CompilationRepository compilationRepository;

    private final CompilationGenerator compilationGenerator = new CompilationGenerator();

    private Compilation compilation;

    @Test
    @BeforeEach
    void save() {
        compilation = compilationGenerator.generateCompilation();
        compilationRepository.save(compilation);
    }

    @Test
    void findById() {
        Optional<Compilation> checkCompilation = compilationRepository.findById(compilation.getId());
        assertTrue(checkCompilation.isPresent());
        assertNotNull(checkCompilation.get().getId());
        assertEquals(compilation, checkCompilation.get());
    }

    @Test
    void getCompilations() {
        List<Compilation> getCompilations = compilationRepository.getCompilations(compilation.getPinned(), 0, 10);
        assertFalse(getCompilations.isEmpty());
    }

    @Test
    void findAll() {
        List<Compilation> findAllCompilations = compilationRepository.findAll();
        assertTrue(findAllCompilations.contains(compilation));
    }

    @Test
    void update() {
        Compilation updateCompilation = compilationGenerator.generateCompilation();

        compilation.setTitle(updateCompilation.getTitle());
        compilation.setPinned(updateCompilation.getPinned());
        compilation.setEvents(updateCompilation.getEvents());
        compilationRepository.save(compilation);

        Optional<Compilation> checkUpdatedCompilation = compilationRepository.findById(compilation.getId());
        assertTrue(checkUpdatedCompilation.isPresent());
        assertEquals(compilation.getId(), checkUpdatedCompilation.get().getId());
        assertEquals(updateCompilation.getTitle(), checkUpdatedCompilation.get().getTitle());
        assertEquals(updateCompilation.getEvents(), checkUpdatedCompilation.get().getEvents());
    }

    @Test
    void delete() {
        compilationRepository.deleteById(compilation.getId());

        Optional<Compilation> checkCompilation = compilationRepository.findById(compilation.getId());
        assertTrue(checkCompilation.isEmpty());
    }
}
