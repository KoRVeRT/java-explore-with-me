package ru.practicum.ewm.main.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.event.model.EventState;
import ru.practicum.ewm.main.event.model.Location;
import ru.practicum.ewm.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    Long id;
    String annotation;
    Long confirmedRequests;
    Long views;
    Integer participantLimit;
    String description;
    String title;
    CategoryDto category;
    UserShortDto initiator;
    Location location;
    Boolean paid;
    Boolean requestModeration;
    EventState state;
    LocalDateTime eventDate;
    LocalDateTime createdOn;
    LocalDateTime publishedOn;
}