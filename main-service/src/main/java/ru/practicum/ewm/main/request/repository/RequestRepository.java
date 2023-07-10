package ru.practicum.ewm.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.request.model.Request;
import ru.practicum.ewm.main.request.model.RequestStatus;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query("SELECT COUNT(*) FROM Request AS r " +
            "WHERE r.event.id = :eventId " +
            "AND r.requestStatus = :requestStatus")
    Long getEventConfirmedRequestsCount(Long eventId, RequestStatus requestStatus);

    Boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventInitiatorIdAndEventId(Long userId, Long eventId);

    @Query("SELECT NEW ru.practicum.ewm.main.request.dto" +
            ".ParticipationRequestDto(r.event.id, COUNT(*), r.requestStatus) " +
            "FROM Request AS r " +
            "WHERE (r.event.id IN (:eventIds)) " +
            "AND (r.requestStatus = :status) " +
            "GROUP BY r.id")
    List<ParticipationRequestDto> findEventParticipation(Set<Long> eventIds, RequestStatus status);

    default Map<Long, Long> getEventConfirmedRequestsCount(Set<Long> eventIds, RequestStatus status) {
        return findEventParticipation(eventIds, status).stream()
                .collect(Collectors.toMap(ParticipationRequestDto::getEvent, ParticipationRequestDto::getCount));
    }
}