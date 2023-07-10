package ru.practicum.ewm.main.compilation.service;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.main.compilation.model.Compilation;

public final class CompilationSpecifications {
    private CompilationSpecifications() {
    }

    public static Specification<Compilation> isPinned(Boolean pinned) {
        return (root, query, criteriaBuilder) -> {
            if (pinned == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("pinned"), pinned);
        };
    }
}