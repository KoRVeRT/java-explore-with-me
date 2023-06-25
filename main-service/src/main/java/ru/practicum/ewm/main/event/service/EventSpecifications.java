package ru.practicum.ewm.main.event.service;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.EventState;
import ru.practicum.ewm.main.user.model.User;

import javax.persistence.criteria.Join;
import java.time.LocalDateTime;
import java.util.Set;


public final class EventSpecifications {
    private EventSpecifications() {
    }

    public static Specification<Event> hasInitiatorWithId(Long initiatorId) {
        return (root, query, criteriaBuilder) -> {
            if (initiatorId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Event, User> joinInitiator = root.join("initiator");
            return criteriaBuilder.equal(joinInitiator.get("id"), initiatorId);
        };
    }

    public static Specification<Event> hasIdIn(Set<Long> ids) {
        return (root, query, cb) -> root.get("id").in(ids);
    }

    public static Specification<Event> usersIn(Set<Long> userIds) {
        return (root, query, cb) -> userIds == null || userIds.isEmpty() ? null : root.get("initiator")
                .get("id").in(userIds);
    }

    public static Specification<Event> statesIn(Set<EventState> states) {
        return (root, query, cb) -> states == null || states.isEmpty() ? null : root.get("eventState").in(states);
    }

    public static Specification<Event> categoriesIn(Set<Long> categoryIds) {
        return (root, query, cb) -> categoryIds == null || categoryIds.isEmpty() ? null : root.get("category")
                .get("id").in(categoryIds);
    }

    public static Specification<Event> startsAfter(LocalDateTime start) {
        return (root, query, cb) -> start == null ? null : cb.greaterThanOrEqualTo(root.get("eventDate"), start);
    }

    public static Specification<Event> endsBefore(LocalDateTime end) {
        return (root, query, cb) -> end == null ? null : cb.lessThanOrEqualTo(root.get("eventDate"), end);
    }

    public static Specification<Event> isPaid(Boolean paid) {
        return (root, query, builder) -> paid == null ? null : builder.equal(root.get("paid"), paid);
    }

    public static Specification<Event> textInDescriptionOrAnnotation(String text) {
        return (root, query, builder) -> {
            if (text == null) {
                return null;
            }
            String containsLikePattern = "%" + text.toLowerCase() + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("description")), containsLikePattern),
                    builder.like(builder.lower(root.get("annotation")), containsLikePattern)
            );
        };
    }
}
