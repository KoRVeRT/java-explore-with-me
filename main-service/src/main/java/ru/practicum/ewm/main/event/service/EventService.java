package ru.practicum.ewm.main.event.service;

import ru.practicum.ewm.main.event.dto.EventFullDto;
import ru.practicum.ewm.main.event.dto.EventShortDto;
import ru.practicum.ewm.main.event.dto.EventUpdateDto;
import ru.practicum.ewm.main.event.dto.NewEventDto;
import ru.practicum.ewm.main.event.model.EventSort;
import ru.practicum.ewm.main.event.model.EventState;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventService {
    EventFullDto addEvent(NewEventDto newEventDto, Long userId);

    EventFullDto updateEventByUser(EventUpdateDto eventUpdateDto, Long userId, Long eventId);

    List<EventShortDto> getUserEvents(Integer from, Integer size, Long userId);

    EventFullDto getUserEventById(Long userId, Long eventId);

    List<EventFullDto> searchEventsByAdmin(Integer from, Integer size, LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd, Set<Long> userIds, Set<Long> categoryIds,
                                                  Set<EventState> eventStates);

    EventFullDto updateEventByAdmin(EventUpdateDto eventUpdateDto, Long eventId);

    List<EventShortDto> searchEventsByUser(Integer from, Integer size, Set<Long> categoryIds, Boolean paid,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, String text,
                                           EventSort sort, Boolean onlyAvailable, HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);
}