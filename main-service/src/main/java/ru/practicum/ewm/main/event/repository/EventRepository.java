package ru.practicum.ewm.main.event.repository;

import io.micrometer.core.lang.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.ewm.main.event.model.Event;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    @NonNull
    @Override
    @EntityGraph(attributePaths = {"initiator", "category"})
    Page<Event> findAll(Specification<Event> spec, @NonNull Pageable pageable);
}