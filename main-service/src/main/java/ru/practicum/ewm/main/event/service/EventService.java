package ru.practicum.ewm.main.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.event.dto.EventFullDto;
import ru.practicum.ewm.main.event.dto.EventShortDto;
import ru.practicum.ewm.main.event.dto.EventUpdateDto;
import ru.practicum.ewm.main.event.dto.NewEventDto;
import ru.practicum.ewm.main.event.model.EventSearchCriteriaByAdmin;
import ru.practicum.ewm.main.event.model.EventSearchCriteriaByUserWithRequestInfo;

import java.util.List;

public interface EventService {
    EventFullDto addEvent(NewEventDto newEventDto, Long userId);

    EventFullDto updateEventByUser(EventUpdateDto eventUpdateDto, Long userId, Long eventId);

    List<EventShortDto> getUserEvents(Integer from, Integer size, Long userId);

    EventFullDto getUserEventById(Long userId, Long eventId);

    List<EventFullDto> searchEventsByAdmin(Pageable pageable, EventSearchCriteriaByAdmin criteria);

    EventFullDto updateEventByAdmin(EventUpdateDto eventUpdateDto, Long eventId);

    List<EventShortDto> searchEventsByUser(Pageable pageable, EventSearchCriteriaByUserWithRequestInfo criteriaWithRequestInfo);

    EventFullDto getEventById(Long eventId, String uri, String ip);
}