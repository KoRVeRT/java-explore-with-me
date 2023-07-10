package ru.practicum.ewm.main.event.dto;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.stats.HitDto;

import java.time.LocalDateTime;

@Component
public class HitDtoMapper {
    public HitDto toHiDto(String uri, String ip, String appName) {
        return HitDto.builder()
                .app(appName)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
    }
}