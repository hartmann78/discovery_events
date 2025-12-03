package com.practice.events_service.repositoryTests;

import com.practice.events_service.generators.*;
import com.practice.events_service.model.*;
import com.practice.events_service.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class CommentRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private CommentRepository commentRepository;

    private final UserGenerator userGenerator = new UserGenerator();
    private final CategoryGenerator categoryGenerator = new CategoryGenerator();
    private final EventGenerator eventGenerator = new EventGenerator();
    private final CommentGenerator commentGenerator = new CommentGenerator();

    private User initiator;
    private Category category;
    private Event event;

    private User author;
    private Comment comment1;
    private Comment comment2;

    @Test
    @BeforeEach
    void save() {
        initiator = userGenerator.generateUser();
        userRepository.save(initiator);

        category = categoryGenerator.generateCategory();
        categoryRepository.save(category);

        event = eventGenerator.generateEvent(initiator, category);
        eventRepository.save(event);

        author = userGenerator.generateUser();
        userRepository.save(author);

        comment1 = commentGenerator.generateComment(author, event);
        commentRepository.save(comment1);

        comment2 = commentGenerator.generateComment(author, event);
        commentRepository.save(comment2);
    }

    @Test
    void findById() {
        Optional<Comment> checkComment1 = commentRepository.findById(comment1.getId());
        assertTrue(checkComment1.isPresent());
        assertNotNull(checkComment1.get().getId());
        assertEquals(comment1, checkComment1.get());

        Optional<Comment> checkComment2 = commentRepository.findById(comment2.getId());
        assertTrue(checkComment2.isPresent());
        assertNotNull(checkComment2.get().getId());
        assertEquals(comment2, checkComment2.get());
    }

    @Test
    void findAll() {
        List<Comment> findAllComments = commentRepository.findAll();
        assertTrue(findAllComments.contains(comment1));
        assertTrue(findAllComments.contains(comment2));
    }

    @Test
    void update() {
        Comment updateComment = commentGenerator.generateComment(author, event);
        comment1.setText(updateComment.getText());
        comment1.setUpdatedOn(LocalDateTime.now());
        commentRepository.save(comment1);

        Optional<Comment> updatedComment = commentRepository.findById(comment1.getId());
        assertTrue(updatedComment.isPresent());
        assertEquals(comment1.getText(), updatedComment.get().getText());
        assertNotNull(updatedComment.get().getUpdatedOn());
    }

    @Test
    void delete() {
        commentRepository.deleteById(comment2.getId());

        Optional<Comment> checkComment = commentRepository.findById(comment2.getId());
        assertTrue(checkComment.isEmpty());
    }

    @Test
    void getAllUserComments() {
        List<Comment> getAllUserComments = commentRepository.getAllUserComments(author.getId(), 0, 10);
        assertEquals(2, getAllUserComments.size());
    }

    @Test
    void getAllEventComments() {
        List<Comment> getAllEventComments = commentRepository.getAllEventComments(event.getId(), 0, 10);
        assertEquals(2, getAllEventComments.size());
    }

    @Test
    void deleteAllUserComments() {
        commentRepository.deleteAllUserComments(author.getId());

        List<Comment> getAllUserComments = commentRepository.getAllUserComments(author.getId(), 0, 10);
        assertTrue(getAllUserComments.isEmpty());
    }

    @Test
    void deleteAllEventComments() {
        commentRepository.deleteAllEventComments(event.getId());

        List<Comment> deleteAllEventComments = commentRepository.getAllEventComments(event.getId(), 0, 10);
        assertTrue(deleteAllEventComments.isEmpty());
    }
}
