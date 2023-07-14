package ru.practicum.ewm.main.comment.repository;

import io.micrometer.core.lang.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.main.comment.model.Comment;

import java.time.LocalDateTime;

public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    @NonNull
    @EntityGraph(attributePaths = {"commentator", "event"})
    Page<Comment> findAll(Specification<Comment> spec, @NonNull Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.commentator.id = :userId AND c.createdOn >= :twoHoursAgo")
    Long countCommentsByUserIdAndCreatedOnAfter(Long userId, LocalDateTime twoHoursAgo);


}