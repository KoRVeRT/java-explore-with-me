package ru.practicum.ewm.main.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.stats.HitDto;
import ru.practicum.ewm.main.category.model.Category;
import ru.practicum.ewm.main.category.repository.CategoryRepository;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.event.model.*;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.request.model.RequestStatus;
import ru.practicum.ewm.main.request.repository.RequestRepository;
import ru.practicum.ewm.main.user.model.User;
import ru.practicum.ewm.main.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static ru.practicum.ewm.main.event.service.EventSpecifications.hasInitiatorWithId;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EventStatisticService eventStatisticService;
    private final EventMapper eventMapper;
    private final HitDtoMapper hitDtoMapper;
    private static final int TIME_FOR_EDIT_EVENT_FOR_USER = 2;
    private static final int TIME_FOR_EDIT_EVENT_FOR_ADMIN = 1;
    private static final String APP_NAME = "main-service";

    @Override
    @Transactional
    public EventFullDto addEvent(NewEventDto newEventDto, Long userId) {
        checkEventDate(newEventDto.getEventDate(), TIME_FOR_EDIT_EVENT_FOR_USER);
        Event event = createEvent(newEventDto, userId);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toEventFullDto(savedEvent, 0L, 0L);
    }

    @Override
    public List<EventShortDto> getUserEvents(Integer from, Integer size, Long userId) {
        findUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findAll(hasInitiatorWithId(userId), pageable)
                .stream()
                .map(e -> eventMapper.toEventShortDto(e, getViews(e), getConfirmedRequests(e.getId())))
                .collect(toList());
    }

    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        findUserById(userId);
        Event event = findEventById(eventId);
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new EntityNotFoundException(String.format("Error in the eventId=%d from non-initiator userId=%d",
                    event.getId(), userId));
        }
        return eventMapper.toEventFullDto(event, getViews(event), getConfirmedRequests(event.getId()));
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(EventUpdateDto eventUpdateDto, Long userId, Long eventId) {
        findUserById(userId);
        Event event = findEventById(eventId);
        checkEventDate(event.getEventDate(), TIME_FOR_EDIT_EVENT_FOR_USER);
        checkEventUserStateUpdate(event);
        setEventState(event, eventUpdateDto.getStateAction());
        Event eventToUpdate = createUpdateEvent(event, eventUpdateDto, TIME_FOR_EDIT_EVENT_FOR_USER);
        Event updatedEvent = eventRepository.save(eventToUpdate);
        Long eventsViews = getViews(event);
        Long confirmedRequests = getConfirmedRequests(eventId);
        return eventMapper.toEventFullDto(updatedEvent, eventsViews, confirmedRequests);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(EventUpdateDto eventUpdateDto, Long eventId) {
        Event event = findEventById(eventId);
        checkEventDate(event.getEventDate(), TIME_FOR_EDIT_EVENT_FOR_ADMIN);
        Event eventToUpdate = createUpdateEvent(event, eventUpdateDto, TIME_FOR_EDIT_EVENT_FOR_ADMIN);
        checkEventAdminStateUpdate(event, eventUpdateDto.getStateAction());
        setEventState(event, eventUpdateDto.getStateAction());
        Event updatedEvent = eventRepository.save(eventToUpdate);
        return eventMapper.toEventFullDto(updatedEvent, getViews(event), getConfirmedRequests(event.getId()));
    }

    @Override
    public List<EventShortDto> searchEventsByUser(Pageable pageable,
                                                  EventSearchCriteriaByUserWithRequestInfo criteriaWithRequestInfo) {
        Specification<Event> spec = Specification
                .where(EventSpecifications.categoriesIn(criteriaWithRequestInfo.getCategories()))
                .and(EventSpecifications.isPaid(criteriaWithRequestInfo.getPaid()))
                .and(EventSpecifications.statesIn(Set.of(EventState.PUBLISHED)))
                .and(EventSpecifications.startsAfter(criteriaWithRequestInfo.getRangeStart()))
                .and(EventSpecifications.endsBefore(criteriaWithRequestInfo.getRangeEnd()))
                .and(EventSpecifications.textInDescriptionOrAnnotation(criteriaWithRequestInfo.getText()));
        HitDto hitDto = hitDtoMapper.toHiDto(criteriaWithRequestInfo.getUri(), criteriaWithRequestInfo.getIp(), APP_NAME);
        eventStatisticService.postHit(hitDto);
        EventContainer data = getEventData(spec, pageable);
        if (criteriaWithRequestInfo.getOnlyAvailable() == Boolean.TRUE) {
            data.setEvents(findAvailableEvents(data.getEvents(), data.getEventsIds()));
        }
        if (criteriaWithRequestInfo.getSort() == null) {
            return data.getEvents()
                    .stream()
                    .map(event -> eventMapper.toEventShortDto(event,
                            data.getEventsViews().getOrDefault(event.getId(), 0L),
                            data.getConfirmedRequests().getOrDefault(event.getId(), 0L)))
                    .collect(Collectors.toList());
        }
        return getSortedEventsShortDto(data.getEvents(), data.getEventsViews(), data.getConfirmedRequests(),
                criteriaWithRequestInfo.getSort());
    }

    @Override
    public List<EventFullDto> searchEventsByAdmin(Pageable pageable, EventSearchCriteriaByAdmin criteria) {
        Specification<Event> spec = Specification
                .where(EventSpecifications.usersIn(criteria.getUsers()))
                .and(EventSpecifications.statesIn(criteria.getStates()))
                .and(EventSpecifications.categoriesIn(criteria.getCategories()))
                .and(EventSpecifications.startsAfter(criteria.getRangeStart()))
                .and(EventSpecifications.endsBefore(criteria.getRangeEnd()));
        EventContainer data = getEventData(spec, pageable);
        return data.getEvents()
                .stream()
                .map(event -> eventMapper.toEventFullDto(event,
                        data.getEventsViews().getOrDefault(event.getId(), 0L),
                        data.getConfirmedRequests().getOrDefault(event.getId(), 0L)))
                .sorted(Comparator.comparing(EventFullDto::getId).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(Long eventId, String uri, String ip) {
        Event event = findEventById(eventId);
        if (!event.getEventState().equals(EventState.PUBLISHED)) {
            throw new EntityNotFoundException(String.format("EventId=%d was not found", eventId));
        }
        Long eventsViews = getViews(event);
        Long confirmedRequests = getConfirmedRequests(event.getId());
        HitDto hitDto = hitDtoMapper.toHiDto(uri, ip, APP_NAME);
        eventStatisticService.postHit(hitDto);
        return eventMapper.toEventFullDto(event, eventsViews, confirmedRequests);
    }

    public EventContainer getEventData(Specification<Event> spec, Pageable pageable) {
        List<Event> events = eventRepository.findAll(spec, pageable).getContent();
        Set<Long> eventsIds = getEventIds(events);
        Map<Long, Long> eventsViews = eventStatisticService.getEventsViews(events);
        Map<Long, Long> confirmedRequests = requestRepository.getEventConfirmedRequestsCount(eventsIds,
                RequestStatus.CONFIRMED);

        return new EventContainer(events, eventsIds, eventsViews, confirmedRequests);
    }

    private List<Event> findAvailableEvents(List<Event> events, Set<Long> eventIds) {
        List<Event> availableEvents = new ArrayList<>();
        var confirmedRequest = requestRepository.getEventConfirmedRequestsCount(eventIds, RequestStatus.CONFIRMED);

        for (Event event : events) {
            Long eventId = event.getId();
            Integer participantLimit = event.getParticipantLimit();

            if (confirmedRequest.get(eventId) < participantLimit) {
                availableEvents.add(event);
            }
        }
        return availableEvents;
    }

    private List<EventShortDto> getSortedEventsShortDto(List<Event> events, Map<Long, Long> eventsViews,
                                                        Map<Long, Long> confirmedRequests, EventSort sort) {
        switch (sort) {
            case VIEWS:
                return events.stream()
                        .map(event -> eventMapper.toEventShortDto(event,
                                eventsViews.getOrDefault(event.getId(), 0L),
                                confirmedRequests.getOrDefault(event.getId(), 0L)))
                        .sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                        .collect(toList());
            case EVENT_DATE:
                return events.stream()
                        .map(event -> eventMapper.toEventShortDto(event,
                                eventsViews.getOrDefault(event.getId(), 0L),
                                confirmedRequests.getOrDefault(event.getId(), 0L)))
                        .sorted(Comparator.comparing(EventShortDto::getEventDate).reversed())
                        .collect(toList());
            default:
                throw new ConflictException(String
                        .format("Sort parameter expects VIEWS or EVENT_DATE, actual: %s", sort));
        }
    }


    private Set<Long> getEventIds(List<Event> events) {
        Set<Long> eventIds = new HashSet<>();
        for (Event event : events) {
            eventIds.add(event.getId());
        }
        return eventIds;
    }

    private void checkEventDate(LocalDateTime eventDate, int hours) {
        if (eventDate != null && !eventDate.minusHours(hours).isAfter(LocalDateTime.now())) {
            throw new ValidationException("Scheduled event time cannot be less than two hours from current moment.");
        }
    }

    private Event createEvent(NewEventDto eventDto, Long userId) {
        User initiator = findUserById(userId);
        Category category = findCategoryById(eventDto.getCategory());
        return eventMapper.toEvent(eventDto, initiator, category);
    }

    private Event createUpdateEvent(Event event, EventUpdateDto eventUpdateDto, int hours) {
        checkEventDate(eventUpdateDto.getEventDate(), hours);
        updateCategory(event, eventUpdateDto.getCategory());
        updateLocation(event, eventUpdateDto.getLocation());
        updateIfNotNull(event::setTitle, eventUpdateDto.getTitle());
        updateIfNotNull(event::setAnnotation, eventUpdateDto.getAnnotation());
        updateIfNotNull(event::setDescription, eventUpdateDto.getDescription());
        updateIfNotNull(event::setEventDate, eventUpdateDto.getEventDate());
        updateIfNotNull(event::setPaid, eventUpdateDto.getPaid());
        updateIfNotNull(event::setParticipantLimit, eventUpdateDto.getParticipantLimit());
        updateIfNotNull(event::setRequestModeration, eventUpdateDto.getRequestModeration());
        return event;
    }

    private void updateCategory(Event event, Long categoryId) {
        if (categoryId != null) {
            categoryRepository.findById(categoryId).ifPresent(event::setCategory);
        }
    }

    private void updateLocation(Event event, Location location) {
        if (location != null) {
            event.setLatitude(location.getLat());
            event.setLongitude(location.getLon());
        }
    }

    private <T> void updateIfNotNull(Consumer<T> setter, T value) {
        Optional.ofNullable(value).ifPresent(setter);
    }

    private void setEventState(Event event, StateAction stateAction) {
        if (stateAction == null) {
            return;
        }
        switch (stateAction) {
            case PUBLISH_EVENT:
                event.setEventState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                break;
            case REJECT_EVENT:
            case CANCEL_REVIEW:
                event.setEventState(EventState.CANCELED);
                break;
            case SEND_TO_REVIEW:
                event.setEventState(EventState.PENDING);
                break;
            default:
                throw new ConflictException(String.format("Event state action expect CANCEL_REVIEW or SEND_TO_REVIEW or" +
                        " REJECT_EVENT or PUBLISH_EVENT, " +
                        "actual=%s", stateAction));
        }
    }

    private void checkEventAdminStateUpdate(Event event, StateAction stateAction) {
        EventState eventState = event.getEventState();
        if (stateAction == StateAction.PUBLISH_EVENT) {
            if (eventState != EventState.PENDING) {
                throw new ConflictException(String
                        .format("EventId=%d not in PENDING state, cannot be published", event.getId()));
            }
        } else if (stateAction == StateAction.REJECT_EVENT && (eventState == EventState.PUBLISHED)) {
            throw new ConflictException(String
                    .format("EventId=%d not in PENDING state, cannot be rejected", event.getId()));
        }
    }

    private void checkEventUserStateUpdate(Event event) {
        EventState eventState = event.getEventState();
        if (eventState == EventState.PUBLISHED) {
            throw new ConflictException(String
                    .format("EventId=%d only pending or canceled events can be changed", event.getId()));
        }
    }

    private Long getConfirmedRequests(Long eventId) {
        return requestRepository.getEventConfirmedRequestsCount(eventId, RequestStatus.CONFIRMED);
    }

    private Long getViews(Event event) {
        return eventStatisticService.getEventViews(event);
    }


    private Event findEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("EventId=%d was not found", id)));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("UserId=%d was not found", id)));
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("CategoryId=%d was not found", id)));
    }
}