package ru.practicum.ewm.server.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.dto.stats.ViewDto;
import ru.practicum.ewm.server.stats.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query(value = "SELECT NEW ru.practicum.ewm.dto.stats.ViewDto(hit.app, hit.uri, COUNT(DISTINCT hit.ip)) " +
            "FROM Hit AS hit " +
            "WHERE (hit.timestamp BETWEEN :start AND :end) " +
            "GROUP BY hit.app, hit.uri " +
            "ORDER BY COUNT(DISTINCT hit.ip) DESC")
    List<ViewDto> getStatsWithUniqUris(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT NEW ru.practicum.ewm.dto.stats.ViewDto(hit.app, hit.uri, COUNT(hit.ip)) " +
            "FROM Hit AS hit " +
            "WHERE (hit.timestamp BETWEEN :start AND :end) " +
            "GROUP BY hit.app, hit.uri " +
            "ORDER BY COUNT(hit.ip) DESC")
    List<ViewDto> getStats(LocalDateTime start, LocalDateTime end);
}