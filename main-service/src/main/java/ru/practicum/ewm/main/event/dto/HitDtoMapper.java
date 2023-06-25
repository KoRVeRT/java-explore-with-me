package ru.practicum.ewm.main.event.dto;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.stats.HitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Component
public class HitDtoMapper {
    public HitDto toHiDto(HttpServletRequest request, String appName) {
        return HitDto.builder()
                .app(appName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
    }
}