package ru.practicum.ewm.main.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.event.dto.EventFullDto;
import ru.practicum.ewm.main.event.dto.EventShortDto;
import ru.practicum.ewm.main.event.model.EventSearchCriteriaByUser;
import ru.practicum.ewm.main.event.model.EventSearchCriteriaByUserWithRequestInfo;
import ru.practicum.ewm.main.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsByPublic(@ModelAttribute @Valid EventSearchCriteriaByUser criteria,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive Integer size,
                                                 HttpServletRequest request) {
        EventSearchCriteriaByUserWithRequestInfo criteriaWithRequestInfo = new EventSearchCriteriaByUserWithRequestInfo();
        BeanUtils.copyProperties(criteria, criteriaWithRequestInfo);
        criteriaWithRequestInfo.setUri(request.getRequestURI());
        criteriaWithRequestInfo.setIp(request.getRemoteAddr());
        Pageable pageable = PageRequest.of(from, size);
        List<EventShortDto> events = eventService.searchEventsByUser(pageable, criteriaWithRequestInfo);
        log.info("Events list with size={} has been got", events.size());
        return events;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        EventFullDto event = eventService.getEventById(id, httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());
        log.info("Event with id={} has been got", id);
        return event;
    }
}