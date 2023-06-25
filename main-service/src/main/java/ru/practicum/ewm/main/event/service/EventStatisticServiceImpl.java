package ru.practicum.ewm.main.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.client.stats.StatsClient;
import ru.practicum.ewm.dto.stats.HitDto;
import ru.practicum.ewm.dto.stats.ViewDto;
import ru.practicum.ewm.main.event.model.Event;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventStatisticServiceImpl implements EventStatisticService {
    private final StatsClient statsClient;
    private static final String EVENT_URI = "/events/";

    @Override
    @Transactional
    public void postHit(HitDto hitDto) {
        statsClient.postHit(hitDto);
    }

    @Override
    public Map<Long, Long> getEventsViews(List<Event> events) {
        Set<Long> eventsIds = getEventIds(events);
        List<String> eventsUri = getUris(eventsIds);
        Map<Long, Long> eventsViewsMap = new HashMap<>();
        LocalDateTime start = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .min(Comparator.nullsLast(Comparator.comparing(Event::getPublishedOn)))
                .map(Event::getPublishedOn).orElse(null);
        if (start == null) {
            return eventsViewsMap;
        }
        List<ViewDto> stats = statsClient.getStats(start, LocalDateTime.now(), eventsUri, true);
        for (ViewDto view : stats) {
            String eventIdUri = view.getUri().substring(EVENT_URI.length());
            Long eventId = Long.parseLong(eventIdUri);
            eventsViewsMap.put(eventId, view.getHits());
        }
        return eventsViewsMap;
    }

    @Override
    public Long getEventViews(Event event) {
        if (Objects.isNull(event.getPublishedOn())) {
            return 0L;
        }
        Optional<ViewDto> dto = statsClient.getStats(
                        event.getPublishedOn(),
                        LocalDateTime.now(),
                        List.of(EVENT_URI + event.getId()),
                        true)
                .stream().findFirst();
        return dto.map(ViewDto::getHits).orElse(0L);
    }

    private Set<Long> getEventIds(List<Event> events) {
        Set<Long> eventIds = new HashSet<>();
        for (Event event : events) {
            eventIds.add(event.getId());
        }
        return eventIds;
    }

    private List<String> getUris(Set<Long> eventIds) {
        List<String> uris = new ArrayList<>();
        for (Long eventId : eventIds) {
            uris.add(EVENT_URI + eventId);
        }
        return uris;
    }
}