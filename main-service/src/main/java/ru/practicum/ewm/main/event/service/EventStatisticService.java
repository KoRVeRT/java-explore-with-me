package ru.practicum.ewm.main.event.service;

import ru.practicum.ewm.dto.stats.HitDto;
import ru.practicum.ewm.main.event.model.Event;

import java.util.List;
import java.util.Map;

public interface EventStatisticService {
    void postHit(HitDto hitDto);

    Map<Long, Long> getEventsViews(List<Event> events);

    Long getEventViews(Event event);
}