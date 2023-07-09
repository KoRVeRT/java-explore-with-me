package ru.practicum.ewm.main.event.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventSearchCriteriaByAdmin {
    Set<Long> users;
    Set<EventState> states;
    Set<Long> categories;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
}
