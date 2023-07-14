package ru.practicum.ewm.main.comment.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentSearchCriteriaByAdmin  {
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    Set<Long> userIds;
    Set<Long> eventIds;
    String text;
    CommentSortByAdmin sort;

    public Specification<Comment> toSpecification() {
        return Specification
                .where(hasEventIdIn(eventIds))
                .and(hasUserIdIn(userIds))
                .and(hasTextLike(text))
                .and(isCreatedAfter(rangeStart))
                .and(isCreatedBefore(rangeEnd));
    }

    private Specification<Comment> hasEventIdIn(Set<Long> eventIds) {
        return (root, query, criteriaBuilder) -> eventIds == null ? null :
                root.get("event").get("id").in(eventIds);
    }

    private Specification<Comment> hasUserIdIn(Set<Long> userIds) {
        return (root, query, criteriaBuilder) -> userIds == null ? null :
                root.get("commentator").get("id").in(userIds);
    }

    private Specification<Comment> hasTextLike(String text) {
        return (root, query, criteriaBuilder) -> text == null ? null :
                criteriaBuilder.like(criteriaBuilder.lower(root.get("text")), "%" + text.toLowerCase() + "%");
    }

    private Specification<Comment> isCreatedAfter(LocalDateTime start) {
        return (root, query, criteriaBuilder) -> start == null ? null :
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdOn"), start);
    }

    private Specification<Comment> isCreatedBefore(LocalDateTime end) {
        return (root, query, criteriaBuilder) -> end == null ? null :
                criteriaBuilder.lessThanOrEqualTo(root.get("createdOn"), end);
    }
}
