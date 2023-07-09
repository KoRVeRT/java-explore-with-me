package ru.practicum.ewm.main.comment.service;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.main.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.Set;

public class CommentSpecification {
    private CommentSpecification() {
    }

    public static Specification<Comment> hasEventIdIn(Set<Long> eventIds) {
        return (root, query, criteriaBuilder) -> eventIds == null ? null :
                root.get("event").get("id").in(eventIds);
    }

    public static Specification<Comment> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) -> userId == null ? null :
                criteriaBuilder.equal(root.get("commentator").get("id"), userId);
    }

    public static Specification<Comment> hasEventId(Long eventId) {
        return (root, query, criteriaBuilder) -> eventId == null ? null :
                criteriaBuilder.equal(root.get("event").get("id"), eventId);
    }

    public static Specification<Comment> hasUserIdIn(Set<Long> userIds) {
        return (root, query, criteriaBuilder) -> userIds == null ? null :
                root.get("commentator").get("id").in(userIds);
    }

    public static Specification<Comment> hasTextLike(String text) {
        return (root, query, criteriaBuilder) -> text == null ? null :
                criteriaBuilder.like(criteriaBuilder.lower(root.get("text")), "%" + text.toLowerCase() + "%");
    }

    public static Specification<Comment> isCreatedAfter(LocalDateTime start) {
        return (root, query, criteriaBuilder) -> start == null ? null :
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdOn"), start);
    }

    public static Specification<Comment> isCreatedBefore(LocalDateTime end) {
        return (root, query, criteriaBuilder) -> end == null ? null :
                criteriaBuilder.lessThanOrEqualTo(root.get("createdOn"), end);
    }
}