package ru.practicum.ewm.server.stats.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.stats.HitDto;
import ru.practicum.ewm.server.stats.model.Hit;

@Component
@RequiredArgsConstructor
public class StatsMapper {
    public Hit toHit(HitDto dto) {
        return Hit.builder()
                .ip(dto.getIp())
                .app(dto.getApp())
                .uri(dto.getUri())
                .timestamp(dto.getTimestamp())
                .build();
    }
}