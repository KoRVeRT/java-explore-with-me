package ru.practicum.ewm.main.event.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.main.category.dto.CategoryMapper;
import ru.practicum.ewm.main.category.model.Category;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.EventState;
import ru.practicum.ewm.main.event.model.Location;
import ru.practicum.ewm.main.user.dto.UserMapper;
import ru.practicum.ewm.main.user.model.User;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    public Event toEvent(NewEventDto newEventDto, User initiator, Category category) {
        return Event.builder()
                .title(newEventDto.getTitle())
                .description(newEventDto.getDescription())
                .annotation(newEventDto.getAnnotation())
                .eventState(EventState.PENDING)
                .eventDate(newEventDto.getEventDate())
                .paid(newEventDto.getPaid() != null ? newEventDto.getPaid() : Boolean.FALSE)
                .requestModeration(newEventDto.getRequestModeration() != null
                        ? newEventDto.getRequestModeration() : Boolean.TRUE)
                .participantLimit(newEventDto.getParticipantLimit() != null
                        ? newEventDto.getParticipantLimit() : 0)
                .latitude(newEventDto.getLocation().getLatitude())
                .longitude(newEventDto.getLocation().getLongitude())
                .category(category)
                .initiator(initiator)
                .build();
    }


    public EventFullDto toEventFullDto(Event event, Long views, Long confirmedRequests) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .participantLimit(event.getParticipantLimit())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .paid(event.getPaid())
                .requestModeration(event.getRequestModeration())
                .state(event.getEventState())
                .eventDate(event.getEventDate())
                .publishedOn(event.getPublishedOn())
                .location(new Location(event))
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn() == null ? LocalDateTime.now() : event.getCreatedOn())
                .views(views)
                .confirmedRequests(confirmedRequests)
                .build();
    }

    public EventShortDto toEventShortDto(Event event, Long views, Long confirmedRequests) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .title(event.getTitle())
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .views(views)
                .confirmedRequests(confirmedRequests)
                .build();
    }
}