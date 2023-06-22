package ru.practicum.ewm.server.stats.service;

import ru.practicum.ewm.dto.stats.HitDto;
import ru.practicum.ewm.dto.stats.ViewDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void saveHit(HitDto dto) ;

    List<ViewDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
