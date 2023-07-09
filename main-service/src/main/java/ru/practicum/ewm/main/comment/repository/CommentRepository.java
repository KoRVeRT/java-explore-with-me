package ru.practicum.ewm.main.comment.repository;

import io.micrometer.core.lang.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.ewm.main.comment.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    @NonNull
    @EntityGraph(attributePaths = {"commentator", "event"})
    Page<Comment> findAll(Specification<Comment> spec, @NonNull Pageable pageable);
}