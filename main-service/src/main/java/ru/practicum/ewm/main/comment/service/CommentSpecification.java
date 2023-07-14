package ru.practicum.ewm.main.comment.service;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.main.comment.model.Comment;

public class CommentSpecification {
    private CommentSpecification() {
    }

    public static Specification<Comment> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) -> userId == null ? null :
                criteriaBuilder.equal(root.get("commentator").get("id"), userId);
    }

    public static Specification<Comment> hasEventId(Long eventId) {
        return (root, query, criteriaBuilder) -> eventId == null ? null :
                criteriaBuilder.equal(root.get("event").get("id"), eventId);
    }
}